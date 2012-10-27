package org.pdtextensions.core.ui.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.pdtextensions.core.ui.wizards.NewInterfaceWizard;

public class CreateNewInterfaceHandler extends AbstractWizardHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		openWizard(event, new NewInterfaceWizard());
		return null;
	}
}
