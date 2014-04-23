package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.pdtextensions.core.ui.refactoring.RefactoringMessages;

public class ExtractMethodWizard extends RefactoringWizard {

	static final String DIALOG_SETTING_SECTION= "ExtractMethodWizard";
	
	public ExtractMethodWizard(ExtractMethodRefactoring refactoring) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE);
		setDefaultPageTitle(RefactoringMessages.ExtractMethod_name);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
	}

	@Override
	protected void addUserInputPages() {
		addPage(new ExtractMethodInputPage());
	}
}
