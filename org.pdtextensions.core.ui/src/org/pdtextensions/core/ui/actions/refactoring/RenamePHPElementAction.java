/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptProjectProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceModuleProcessor;
import org.eclipse.dltk.internal.ui.actions.ActionUtil;
import org.eclipse.dltk.internal.ui.editor.ModelTextSelection;
import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.documentModel.dom.IImplForPhp;
import org.eclipse.php.internal.ui.actions.SelectionConverter;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IWorkbenchSite;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.refactoring.RenameSupport;
import org.pdtextensions.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameLocalVariableProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameMethodProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameTypeProcessor;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenamePHPElementAction extends SelectionDispatchAction {
	private PHPStructuredEditor editor;

	public RenamePHPElementAction(IWorkbenchSite site) {
		super(site);
	}

	public RenamePHPElementAction(PHPStructuredEditor editor) {
		this(editor.getEditorSite());
		this.editor = editor;
		setEnabled(SelectionConverter.canOperateOn(this.editor));
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		try {
			if (selection.size() == 1) {
				setEnabled(canEnable(selection));
			}
		} catch (ModelException e) {
			// http://bugs.eclipse.org/bugs/show_bug.cgi?id=19253
			if (ScriptModelUtil.isExceptionToBeLogged(e)){
				PEXUIPlugin.log(e);
			}
			setEnabled(false);
		} catch (CoreException e) {
			PEXUIPlugin.log(e);
			setEnabled(false);
		}
	}

	private static boolean canEnable(IStructuredSelection selection) throws CoreException {
		IModelElement element = getModelElement(selection);
		if (element == null) {
			return false;
		} else {
			return isRenameAvailable(element);
		}
	} 

	private static IModelElement getModelElement(IStructuredSelection selection) throws ModelException {
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof IModelElement) {
				return (IModelElement) element;
			} else if (element instanceof IImplForPhp) {
				IModelElement modelElement = ((IImplForPhp) element).getModelElement();
				if (modelElement instanceof ISourceModule && selection instanceof ITextSelection) {
					return ((ISourceModule) modelElement).getElementAt(((ITextSelection) selection).getOffset());
				}
			}
		}

		return null;
	}

	@Override
	public void run(IStructuredSelection selection) {
		try {
			IModelElement element = getModelElement(selection);
			if (element != null && ActionUtil.isEditable(getShell(), element)) {
				run(element);
			} else {
			    return;
			}
		} catch (CoreException e) {
			ExceptionHandler.handle(e, RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_exception);
		}
	}

	@Override
	public void selectionChanged(ITextSelection selection) {
		if (selection instanceof ModelTextSelection) {
			try {
				IModelElement[] elements = ((ModelTextSelection)selection).resolveElementAtOffset();
				if (elements.length == 1) {
					setEnabled(isRenameAvailable(elements[0]));
				} else {
					setEnabled(false);
				}
			} catch (CoreException e) {
				setEnabled(false);
			}
		} else {
			setEnabled(true);
		}
	}

	@Override
	public void run(ITextSelection selection) {
		if (ActionUtil.isEditable(editor)) return;

		if (canRunInEditor()) {
			try {
				IModelElement element = getScriptElementFromEditor();
				if (element != null) {
					run(element);
					return;
				}
			} catch (CoreException e) {
				ExceptionHandler.handle(e, RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_exception);
			}
		}

		MessageDialog.openInformation(getShell(), RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_not_available);
	}

	private boolean canRunInEditor() {
		try {
			IModelElement element = getScriptElementFromEditor();
			if (element != null) {
				return isRenameAvailable(element);
			}
		} catch (ModelException e) {
			if (ScriptModelUtil.isExceptionToBeLogged(e)) {
				PEXUIPlugin.log(e);
			}
		} catch (CoreException e) {
			PEXUIPlugin.log(e);
		}

		return false;
	}

	private IModelElement getScriptElementFromEditor() throws ModelException {
		IModelElement[] elements = SelectionConverter.codeResolve(editor);
		if (elements != null && elements.length == 1) {
			return elements[0];
		} else {
			return null;
		}
	}

	private void run(IModelElement element) throws CoreException {
		// Work around for http://dev.eclipse.org/bugs/show_bug.cgi?id=19104		
		if (!ActionUtil.isProcessable(getShell(), element)) return;
		//XXX workaround bug 31998
		if (ActionUtil.mustDisableScriptModelAction(getShell(), element)) return;

		final RenameSupport support = createRenameSupport(element, null, RenameSupport.UPDATE_REFERENCES);
		if (support != null && support.preCheck().isOK()) {
			support.openDialog(getShell());
		}
	}

	private static boolean isRenameAvailable(IModelElement element) throws CoreException {
		switch (element.getElementType()) {
		case IModelElement.SCRIPT_PROJECT:
			return RefactoringAvailabilityTester.isRenameAvailable((IScriptProject) element);
		case IModelElement.PROJECT_FRAGMENT:
			return RefactoringAvailabilityTester.isRenameAvailable((IProjectFragment) element);
		case IModelElement.SCRIPT_FOLDER:
			// TODO Add namespace support like JDT
			return RefactoringAvailabilityTester.isRenameAvailable((IScriptFolder) element);
		case IModelElement.SOURCE_MODULE:
			return RefactoringAvailabilityTester.isRenameAvailable((ISourceModule) element);
		case IModelElement.TYPE:
			return RefactoringAvailabilityTester.isRenameAvailable((IType) element);
		case IModelElement.METHOD:
			return RefactoringAvailabilityTester.isRenameAvailable((IMethod) element);
		case IModelElement.FIELD:
			return RefactoringAvailabilityTester.isRenameFieldAvailable((IField) element);
		}

		return false;
	}

	private static RenameSupport createRenameSupport(IModelElement element, String newName, int flags) throws CoreException {
		switch (element.getElementType()) {
		case IModelElement.SCRIPT_PROJECT:
			return new RenameSupport(new RenameScriptProjectProcessor((IScriptProject) element), newName, flags);
		case IModelElement.PROJECT_FRAGMENT:
			return new RenameSupport(new RenameSourceFolderProcessor((IProjectFragment) element), newName);
		case IModelElement.SCRIPT_FOLDER:
			// TODO Add namespace support like JDT
			return new RenameSupport(new RenameScriptFolderProcessor((IScriptFolder) element), newName, flags);
		case IModelElement.SOURCE_MODULE:
			return new RenameSupport(new RenameSourceModuleProcessor((ISourceModule) element), newName, flags);
		case IModelElement.TYPE:
			try {
				if (PHPFlags.isClass(((IType) element).getFlags())
					|| PHPFlags.isInterface(((IType) element).getFlags())
					|| PHPFlags.isTrait(((IType) element).getFlags())
					) {
					return new RenameSupport(new RenameTypeProcessor((IType) element), newName, flags);
				}
			} catch (ModelException e) {
			}
			break;
		case IModelElement.METHOD:
			return new RenameSupport(new RenameMethodProcessor((IMethod) element), newName, flags);
		case IModelElement.FIELD:
			IField field = (IField) element;
			if (field.getDeclaringType() == null) {
				if (field.getSource().startsWith("$this->")) {
					// if the selection is on $this->someField we need to get the declaring field in the parent type
					// otherwise we get text conflicts
					if (field.getParent().getElementType() == IModelElement.METHOD) {
						IMethod method = (IMethod) field.getParent();
						if (method.getParent().getElementType() == IModelElement.TYPE) {
							IType type = (IType) method.getParent();
							IField enclosingField = type.getField(field.getElementName());
							return new RenameSupport(new RenameFieldProcessor(enclosingField), newName, flags); 
						}
					}
				} else if (field.getSource().startsWith(field.getElementName())) {
					return new RenameSupport(new RenameLocalVariableProcessor(field), newName, flags);
				}
			} else {
				return new RenameSupport(new RenameFieldProcessor(field), newName, flags);
			}
		}
		return null;
	}
}
