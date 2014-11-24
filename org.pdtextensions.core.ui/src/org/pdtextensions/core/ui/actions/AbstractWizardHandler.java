package org.pdtextensions.core.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractWizardHandler extends AbstractHandler {

	public void openWizard(ExecutionEvent event, Wizard wizard)
	{
		Shell shell = HandlerUtil.getActiveShell(event);
		
		if (wizard instanceof NewElementWizard) {
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				((NewElementWizard) wizard).init(HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench(), (IStructuredSelection) selection);
			}
		}
		
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        int res = dialog.open();
        if (res == Window.OK && wizard instanceof NewElementWizard) {
        	
        }		
	}
}
