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
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
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
import org.eclipse.dltk.internal.ui.refactoring.reorg.RenameUserInterfaceManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.swt.widgets.Shell;
import org.pdtextensions.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameMethodProcessor;
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

	/** Flag indication that no additional update is to be performed. */
	public static final int NONE = 0;

	/** Flag indicating that references are to be updated as well. */
	public static final int UPDATE_REFERENCES = 1 << 0;

	public static final int UPDATE_TEXTUAL_MATCHES = 1 << 6;

	private RenameSupport(ScriptRenameProcessor processor, String newName, int flags) throws CoreException {
		refactoring = new ScriptRenameRefactoring(processor);
		initialize(refactoring, newName, flags);
	}
	
	/**
	 * Creates a new rename support for the given {@link IScriptProject}.
	 * 
	 * @param project the {@link IScriptProject} to be renamed.
	 * @param newName the project's new name. <code>null</code> is a valid
	 * value indicating that no new name is provided.
	 * @param flags flags controlling additional parameters. Valid flags are
	 * <code>UPDATE_REFERENCES</code> or <code>NONE</code>.
	 * @return the {@link RenameSupport}.
	 * @throws CoreException if an unexpected error occurred while creating
	 * the {@link RenameSupport}.
	 */
	public static RenameSupport create(IScriptProject project, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameScriptProjectProcessor(project), newName, flags);
	}
	
	/**
	 * Creates a new rename support for the given {@link IProjectFragment}.
	 * 
	 * @param root the {@link IProjectFragment} to be renamed.
	 * @param newName the package fragment root's new name. <code>null</code> is
	 * a valid value indicating that no new name is provided.
	 * @return the {@link RenameSupport}.
	 * @throws CoreException if an unexpected error occurred while creating
	 * the {@link RenameSupport}.
	 */
	public static RenameSupport create(IProjectFragment root, String newName) throws CoreException {
		return new RenameSupport(new RenameSourceFolderProcessor(root), newName, 0);
	}
	
	/**
	 * Creates a new rename support for the given {@link IScriptFolder}.
	 * 
	 * @param fragment the {@link IScriptFolder} to be renamed.
	 * @param newName the package fragment's new name. <code>null</code> is a
	 * valid value indicating that no new name is provided.
	 * @param flags flags controlling additional parameters. Valid flags are
	 * <code>UPDATE_REFERENCES</code>, and <code>UPDATE_TEXTUAL_MATCHES</code>,
	 * or their bitwise OR, or <code>NONE</code>.
	 * @return the {@link RenameSupport}.
	 * @throws CoreException if an unexpected error occurred while creating
	 * the {@link RenameSupport}.
	 */
	public static RenameSupport create(IScriptFolder fragment, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameScriptFolderProcessor(fragment), newName, flags);
	}
	
	/**
	 * Creates a new rename support for the given {@link ISourceModule}.
	 * 
	 * @param unit the {@link ISourceModule} to be renamed.
	 * @param newName the compilation unit's new name. <code>null</code> is a
	 * valid value indicating that no new name is provided.
	 * @param flags flags controlling additional parameters. Valid flags are
	 * <code>UPDATE_REFERENCES</code>, and <code>UPDATE_TEXTUAL_MATCHES</code>,
	 * or their bitwise OR, or <code>NONE</code>.
	 * @return the {@link RenameSupport}.
	 * @throws CoreException if an unexpected error occurred while creating
	 * the {@link RenameSupport}.
	 */
	public static RenameSupport create(ISourceModule unit, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameSourceModuleProcessor(unit), newName, flags);
	}	

	public static RenameSupport create(IType type, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameTypeProcessor(type), newName, flags);
	}	

	public static RenameSupport create(IMethod method, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameMethodProcessor(method), newName, flags);
	}	

	public static RenameSupport create(IField field, String newName, int flags) throws CoreException {
		return new RenameSupport(new RenameFieldProcessor(field), newName, flags);
	}	

	public void openDialog(Shell parent) throws CoreException {
		ensureChecked();

		if (preCheckStatus.hasFatalError()) {
			MessageDialog.openInformation(parent, DLTKUIMessages.RenameSupport_dialog_title, preCheckStatus.getMessageMatchingSeverity(RefactoringStatus.FATAL)); 
			return; 
		}

		RenameUserInterfaceManager.getDefault().getStarter(refactoring).activate(refactoring, parent, ((ScriptRenameProcessor) refactoring.getProcessor()).needsSavedEditors());
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

	private static void initialize(RenameRefactoring refactoring, String newName, int flags) {
		if (refactoring.getProcessor() == null) return;

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
}
