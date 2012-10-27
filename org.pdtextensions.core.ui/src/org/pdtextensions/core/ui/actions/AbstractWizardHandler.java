package org.pdtextensions.core.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractWizardHandler extends AbstractHandler {

	public void openWizard(ExecutionEvent event, Wizard wizard)
	{
		Shell shell = HandlerUtil.getActiveShell(event);
		
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        int res = dialog.open();
        if (res == Window.OK && wizard instanceof NewElementWizard) {
        	
        }		
	}
}
