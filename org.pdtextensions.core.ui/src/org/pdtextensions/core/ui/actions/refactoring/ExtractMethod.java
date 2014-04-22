package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.actions.ActionUtil;
import org.eclipse.dltk.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.core.documentModel.dom.IImplForPhp;
import org.eclipse.php.internal.ui.actions.SelectionConverter;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IWorkbenchSite;
import org.pdtextensions.core.ui.refactoring.RefactoringMessages;

@SuppressWarnings("restriction")
public class ExtractMethod extends SelectionDispatchAction {

	private PHPStructuredEditor editor;

	public ExtractMethod(IWorkbenchSite site)
	{
		super(site);
		setEnabled(true);
		setText(RefactoringMessages.ExtractMethod_name);
	}

	public ExtractMethod(PHPStructuredEditor editor) {
		this(editor.getEditorSite());
		setEnabled(true);
		this.editor = editor;
	}

	@Override
	public void run(IStructuredSelection selection) {

		ExtractMethodRefactoring refactoring = new ExtractMethodRefactoring(
				SelectionConverter.getInputAsCompilationUnit(editor),
				((ITextSelection) selection).getOffset(),
				((ITextSelection) selection).getLength());
		try {
			new RefactoringStarter().activate(refactoring,
					new ExtractMethodWizard(refactoring), getShell(),
					RefactoringMessages.ExtractMethod_name, false);
		} catch (ModelException e) {
			ExceptionHandler.handle(e, RefactoringMessages.ExtractMethod_name,
					RefactoringMessages.RenamePHPElementAction_exception);
			return;
		}

	}

	public void selectionChanged(IStructuredSelection selection)
	{
		setEnabled(true);
	}
}
