package org.pdtextensions.core.ui.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.pdtextensions.core.ui.wizards.NewClassWizard;

public class CreateNewClassHandler extends AbstractWizardHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		openWizard(event, new NewClassWizard());
		return null;
	}
}
