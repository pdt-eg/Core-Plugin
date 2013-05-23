/*******************************************************************************
 * Copyright (c) 2000, 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.internal.corext.refactoring.ScriptRefactoringArguments;
import org.eclipse.dltk.internal.corext.refactoring.ScriptRefactoringDescriptor;
import org.eclipse.dltk.internal.corext.refactoring.code.ScriptableRefactoring;
import org.eclipse.dltk.internal.corext.refactoring.participants.ScriptProcessors;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameModifications;
import org.eclipse.dltk.internal.corext.refactoring.rename.ScriptRenameProcessor;
import org.eclipse.dltk.internal.corext.refactoring.tagging.IReferenceUpdating;
import org.eclipse.dltk.internal.corext.refactoring.util.ResourceUtil;
import org.eclipse.dltk.internal.corext.refactoring.util.TextChangeManager;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.pdtextensions.core.ui.Messages;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;
import org.pdtextensions.internal.corext.refactoring.RenamePHPElementDescriptor;

/**
 * This is the replacement of the {@link org.eclipse.dltk.internal.corext.refactoring.rename.RenameModelElementProcessor}
 * class for PHP elements.
 *
 * @since 0.17.0
 */
public abstract class PHPRenameProcessor extends ScriptRenameProcessor implements IReferenceUpdating {
	protected IModelElement modelElement;
	protected ISourceModule cu;

	//the following fields are set or modified after the construction
	protected boolean updateReferences;
	protected String currentName;

	protected TextChangeManager changeManager = new TextChangeManager(true);

	public PHPRenameProcessor(IModelElement modelElement) {
		this.modelElement = modelElement;
		cu = (ISourceModule) modelElement.getAncestor(IModelElement.SOURCE_MODULE);
	}

	public RefactoringStatus initialize(RefactoringArguments arguments) {
		if (!(arguments instanceof ScriptRefactoringArguments))
			return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.InitializableRefactoring_inacceptable_arguments);
		final ScriptRefactoringArguments extended = (ScriptRefactoringArguments) arguments;
		final String handle = extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_INPUT);
		if (handle != null) {
			final IModelElement element = ScriptRefactoringDescriptor.handleToElement(extended.getProject(), handle, false);
			if (element != null && element.exists()) {
				if (element.getElementType() == IModelElement.SOURCE_MODULE) {
					cu = (ISourceModule) element;
				} else if (element.getElementType() == IModelElement.LOCAL_VARIABLE) {
					modelElement = (ILocalVariable) element;
					cu = (ISourceModule) modelElement.getAncestor(IModelElement.SOURCE_MODULE);
					if (cu == null)
						return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
				} else
					return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
			} else
				return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
		} else {
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_INPUT));
		}
		final String name = extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_NAME);
		if (name != null && !"".equals(name)) //$NON-NLS-1$
			setNewElementName(name);
		else
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_NAME));
		if (cu != null && modelElement == null) {
			final String selection = extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION);
			if (selection != null) {
				int offset= -1;
				int length= -1;
				final StringTokenizer tokenizer = new StringTokenizer(selection);
				if (tokenizer.hasMoreTokens())
					offset = Integer.valueOf(tokenizer.nextToken()).intValue();
				if (tokenizer.hasMoreTokens())
					length = Integer.valueOf(tokenizer.nextToken()).intValue();
				if (offset >= 0 && length >= 0) {
					try {
						final IModelElement[] elements = cu.codeSelect(offset, length);
						if (elements != null) {
							for (int index = 0; index < elements.length; index++) {
								final IModelElement element = elements[index];
								if (element instanceof ILocalVariable)
									modelElement = (ILocalVariable) element;
							}
						}
						if (modelElement == null)
							return ScriptableRefactoring.createInputFatalStatus(null, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
					} catch (ModelException exception) {
						PEXUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, PEXUIPlugin.PLUGIN_ID, exception.getMessage(), exception));
					}
				} else
					return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_illegal_argument, new Object[] { selection,
							ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION }));
			} else
				return RefactoringStatus
						.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION));
		}
		final String references= extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_REFERENCES);
		if (references != null) {
			updateReferences= Boolean.valueOf(references).booleanValue();
		} else
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_REFERENCES));
		return new RefactoringStatus();
	}

	public String getCurrentElementName() {
		return currentName;
	}

	@Override
	public boolean canEnableUpdateReferences() {
		return true;
	}

	@Override
	public void setUpdateReferences(boolean update) {
		updateReferences = update;
	}

	@Override
	public boolean getUpdateReferences() {
		return updateReferences;
	}

	@Override
	protected RenameModifications computeRenameModifications()
			throws CoreException {
		RenameModifications result = new RenameModifications();
		if (modelElement instanceof ILocalVariable) {
			result.rename((ILocalVariable)modelElement, new RenameArguments(getNewElementName(), getUpdateReferences()));
		} else if (modelElement instanceof IField) {
			// TODO: add switching method in RenameModifications
			result.rename((IField)modelElement, new RenameArguments(getNewElementName(), getUpdateReferences()));
		}

		return result;
	}

	@Override
	protected IFile[] getChangedFiles() throws CoreException {
		return ResourceUtil.getFiles(changeManager.getAllSourceModules());
	}

	@Override
	protected RefactoringStatus doCheckFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		pm.beginTask("", 1); //$NON-NLS-1$

		try {
			RefactoringStatus result;

			result = checkNewElementName(getNewElementName());
			if (result.hasFatalError()) return result;

			result = renameDeclaration(pm);
			if (result.hasFatalError()) return result;

			if (updateReferences) {
				result = updateReferences(pm);
				if (result.hasFatalError()) return result;
			}

			return result;
		} finally {
			pm.done();
		}
	}

	protected RefactoringStatus renameDeclaration(IProgressMonitor pm) throws CoreException {
		ISourceRange sourceRange = null;
		if (modelElement instanceof IMember) {
			sourceRange = ((IMember) modelElement).getNameRange();
		}
		if (sourceRange != null) {
			addTextEdit(changeManager.get(cu), getProcessorName(), new ReplaceEdit(sourceRange.getOffset(), currentName.length(), getNewElementName()));
		}

		return new RefactoringStatus();
	}

	protected abstract RefactoringStatus updateReferences(IProgressMonitor pm) throws CoreException;

	protected static void addTextEdit(TextChange change, String name, TextEdit edit) throws MalformedTreeException {
		TextEdit root = change.getEdit();
		if (root == null) {
			root = new MultiTextEdit();
			change.setEdit(root);
		}

		root.addChild(edit);
		change.addTextEditGroup(new TextEditGroup(name, edit));
	}

	@Override
	protected String[] getAffectedProjectNatures() throws CoreException {
		return ScriptProcessors.computeAffectedNatures(cu);
	}

	@Override
	public Object[] getElements() {
		return new Object[]{ modelElement };
	}
	
	public String getNewElement() {
		return getNewElementName();
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		currentName = modelElement.getElementName();

		return new RefactoringStatus();
	}

	protected RefactoringDescriptor createRefactoringDescriptor() {
		RenamePHPElementDescriptor descriptor = new RenamePHPElementDescriptor(getRefactoringId());
		if (cu.getScriptProject() != null) {
			descriptor.setProject(cu.getScriptProject().getElementName());
		}

		descriptor.setFlags(RefactoringDescriptor.NONE);
		descriptor.setModelElement(modelElement);
		descriptor.setNewName(getNewElementName());
		descriptor.setUpdateReferences(updateReferences);

		return descriptor;
	}

	protected abstract String getRefactoringId();
}
