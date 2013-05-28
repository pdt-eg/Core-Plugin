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
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameScriptProjectProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceFolderProcessor;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceModuleProcessor;
import org.eclipse.dltk.internal.ui.actions.ActionUtil;
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
import org.pdtextensions.core.ui.refactoring.RenameSupport;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.internal.corext.refactoring.rename.RenameConstantProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenamePropertyProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameLocalVariableProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameMethodProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameStaticPropertyProcessor;
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
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof IModelElement) {
				setEnabled(ActionUtil.isEditable(getShell(), (IModelElement) element));
			} else if (element instanceof IImplForPhp) {
				element = ((IImplForPhp) element).getModelElement();
				setEnabled(element != null && ActionUtil.isEditable(getShell(), (IModelElement) element));
			} else {
				setEnabled(false);
			}
		} else {
			setEnabled(false);
		}
	}

	@Override
	public void run(IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			try {
				if (element instanceof IModelElement) {
					if (element != null && ActionUtil.isEditable(getShell(), (IModelElement) element) && isRenameAvailable((IModelElement) element)) {
						run((IModelElement) element);
						return;
					}
				} else if (element instanceof IImplForPhp) {
					element = ((IImplForPhp) element).getModelElement();
					if (element != null && ActionUtil.isEditable(getShell(), (IModelElement) element)) {
						if (element instanceof ISourceModule && selection instanceof ITextSelection) {
							IModelElement sourceElement = PDTModelUtils.getSourceElement((ISourceModule) element, ((ITextSelection) selection).getOffset(), ((ITextSelection) selection).getLength());
							if (sourceElement != null && ActionUtil.isEditable(getShell(), sourceElement) && isRenameAvailable(sourceElement)) {
								run(sourceElement);
								return;
							}
						}
					}
				}
			} catch (CoreException e) {
				ExceptionHandler.handle(e, RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_exception);
				return;
			}
		}

		MessageDialog.openInformation(getShell(), RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_not_available);
	}

	@Override
	public void selectionChanged(ITextSelection selection) {
		setEnabled(editor != null && ActionUtil.isEditable(editor));
	}

	@Override
	public void run(ITextSelection selection) {
		if (editor != null && ActionUtil.isEditable(editor)) {
			try {
				IModelElement[] elements = SelectionConverter.codeResolve(editor);
				if (elements != null && elements.length == 1) {
					IModelElement sourceElement = PDTModelUtils.getSourceElement(elements[0], selection.getOffset(), selection.getLength());
					if (sourceElement != null && ActionUtil.isEditable(getShell(), sourceElement) && isRenameAvailable(sourceElement)) {
						run(sourceElement);
						return;
					}
				}
			} catch (CoreException e) {
				ExceptionHandler.handle(e, RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_exception);
				return;
			}
		}

		MessageDialog.openInformation(getShell(), RefactoringMessages.RenameScriptElementAction_name, RefactoringMessages.RenameScriptElementAction_not_available);
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
			return RefactoringAvailabilityTester.isRenameAvailable((IType) element)
						&& RefactoringAvailabilityTester.isRenameAvailable(((IMember) element).getSourceModule());
		case IModelElement.METHOD:
			return RefactoringAvailabilityTester.isRenameAvailable((IMethod) element)
						&& RefactoringAvailabilityTester.isRenameAvailable(((IMember) element).getSourceModule());
		case IModelElement.FIELD:
			return RefactoringAvailabilityTester.isRenameFieldAvailable((IField) element)
						&& RefactoringAvailabilityTester.isRenameAvailable(((IField) element).getSourceModule());
		default:
			return false;
		}
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
			if (PHPFlags.isClass(((IType) element).getFlags())
					|| PHPFlags.isInterface(((IType) element).getFlags())
					|| PHPFlags.isTrait(((IType) element).getFlags())
					) {
				return new RenameSupport(new RenameTypeProcessor((IType) element), newName, flags);
			} else {
				return null;
			}
		case IModelElement.METHOD:
			return new RenameSupport(new RenameMethodProcessor((IMethod) element), newName, flags);
		case IModelElement.FIELD:
			if (((IField) element).getDeclaringType() == null) {
				return new RenameSupport(new RenameLocalVariableProcessor((IField) element), newName, flags);
			} else {
				if (PHPFlags.isConstant(((IField) element).getFlags())) {
					return new RenameSupport(new RenameConstantProcessor((IField) element), newName, flags);
				} else if (PHPFlags.isStatic(((IField) element).getFlags())) {
					return new RenameSupport(new RenameStaticPropertyProcessor((IField) element), newName, flags);
				} else {
					return new RenameSupport(new RenamePropertyProcessor((IField) element), newName, flags);
				}
			}
		default:
			return null;
		}
	}
}
