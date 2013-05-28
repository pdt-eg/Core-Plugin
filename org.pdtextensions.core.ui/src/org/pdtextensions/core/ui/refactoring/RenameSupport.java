/*******************************************************************************
 * Copyright (c) 2005, 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.core.ui.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptProjectProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceModuleProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.ScriptRenameProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.ScriptRenameRefactoring;
import org.eclipse.dltk.internal.corext.refactoring.tagging.INameUpdating;
import org.eclipse.dltk.internal.corext.refactoring.tagging.IReferenceUpdating;
import org.eclipse.dltk.internal.corext.refactoring.tagging.ITextUpdating;
import org.eclipse.dltk.internal.ui.DLTKUIMessages;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameLocalVariableWizard;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameScriptFolderWizard;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameScriptProjectWizard;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameSourceFolderWizard;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameSourceModuleWizard;
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameUserInterfaceStarter;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.pdtextensions.internal.corext.refactoring.rename.RenameConstantProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameLocalVariableProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameMethodProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameStaticPropertyProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameTypeProcessor;

/**
 * This is the replacement of the {@link org.eclipse.dltk.ui.refactoring.RenameSupport}
 * class for PHP elements.
 *
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameSupport {
	private RenameRefactoring refactoring;
	private RefactoringStatus preCheckStatus;
	private RenameUserInterfaceStarter starter = new RenameUserInterfaceStarter();

	/** Flag indication that no additional update is to be performed. */
	public static final int NONE = 0;

	/** Flag indicating that references are to be updated as well. */
	public static final int UPDATE_REFERENCES = 1 << 0;

	public static final int UPDATE_TEXTUAL_MATCHES = 1 << 6;

	public RenameSupport(RenameScriptProjectProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameScriptProjectWizard(refactoring));
	}

	public RenameSupport(RenameSourceFolderProcessor processor, String newName) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, 0);

		initializeStarter(new RenameSourceFolderWizard(refactoring));
	}

	public RenameSupport(RenameScriptFolderProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameScriptFolderWizard(refactoring));
	}

	public RenameSupport(RenameSourceModuleProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameSourceModuleWizard(refactoring));
	}

	public RenameSupport(RenameTypeProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}

	public RenameSupport(RenameMethodProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}

	public RenameSupport(RenameFieldProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}
	
	public RenameSupport(RenameStaticPropertyProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}

	public RenameSupport(RenameLocalVariableProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}

	public RenameSupport(RenameConstantProcessor processor, String newName, int flags) throws CoreException {
		this((ScriptRenameProcessor) processor, newName, flags);

		initializeStarter(new RenameLocalVariableWizard(refactoring));
	}

	private RenameSupport(ScriptRenameProcessor processor, String newName, int flags) throws CoreException {
		refactoring = new ScriptRenameRefactoring(processor);

		if (newName != null) {
			((INameUpdating) refactoring.getAdapter(INameUpdating.class)).setNewElementName(newName);
		}

		IReferenceUpdating reference = (IReferenceUpdating) refactoring.getAdapter(IReferenceUpdating.class);
		if (reference != null) {
			reference.setUpdateReferences((flags & UPDATE_REFERENCES) != 0);
		}

		ITextUpdating text = (ITextUpdating) refactoring.getAdapter(ITextUpdating.class);
		if (text != null) {
			text.setUpdateTextualMatches((flags & UPDATE_TEXTUAL_MATCHES) != 0);
		}
	}

	public void openDialog(Shell parent) throws CoreException {
		ensureChecked();

		if (preCheckStatus.hasFatalError()) {
			MessageDialog.openInformation(parent, DLTKUIMessages.RenameSupport_dialog_title, preCheckStatus.getMessageMatchingSeverity(RefactoringStatus.FATAL)); 
			return; 
		}

		starter.activate(refactoring, parent, ((ScriptRenameProcessor) refactoring.getProcessor()).needsSavedEditors());
	}

	/**
	 * Executes some light weight precondition checking. If the returned status
	 * is an error then the refactoring can't be executed at all. However,
	 * returning an OK status doesn't guarantee that the refactoring can be
	 * executed. It may still fail while performing the exhaustive precondition
	 * checking done inside the methods <code>openDialog</code> or
	 * <code>perform</code>.
	 * 
	 * The method is mainly used to determine enable/disablement of actions.
	 * 
	 * @return the result of the light weight precondition checking.
	 * 
	 * @throws CoreException if an unexpected exception occurs while performing the checking.
	 * 
	 * @see #openDialog(Shell)
	 * @see #perform(Shell, IRunnableContext)
	 */
	public IStatus preCheck() throws CoreException {
		ensureChecked();

		if (preCheckStatus.hasFatalError()) {
			return preCheckStatus.getEntryMatchingSeverity(RefactoringStatus.FATAL).toStatus();
		} else {
			return new Status(IStatus.OK, DLTKUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		}
	}

	private void ensureChecked() throws CoreException {
		if (preCheckStatus == null) {
			if (refactoring.isApplicable()) {
				preCheckStatus = new RefactoringStatus();
			} else {
				preCheckStatus = RefactoringStatus.createFatalErrorStatus(DLTKUIMessages.RenameSupport_not_available); 
			}
		}
	}

	private void initializeStarter(RefactoringWizard wizard) {
		starter.initialize(wizard);
	}
}
