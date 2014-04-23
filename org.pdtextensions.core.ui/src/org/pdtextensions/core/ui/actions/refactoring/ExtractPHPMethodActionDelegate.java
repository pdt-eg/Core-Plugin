package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.php.internal.ui.actions.IPHPActionDelegator;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class ExtractPHPMethodActionDelegate implements IPHPActionDelegator {

	private ExtractMethod extractAction;

	@Override
	public void dispose() {
		extractAction = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		extractAction = null;

		if (window != null) {
			IWorkbenchPart activePart = window.getPartService().getActivePart();
			if (activePart != null) {
				if (activePart instanceof PHPStructuredEditor) {
					extractAction = new ExtractMethod((PHPStructuredEditor) activePart);
				} else {
					extractAction = new ExtractMethod(activePart.getSite());
				}
			}
		}
	}

	private void init(PHPStructuredEditor editor) {
		extractAction = new ExtractMethod(editor);
	}

	@Override
	public void run(IAction action) {
		if (extractAction != null) {
			extractAction.run();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (extractAction == null) {
			init(activeWindow);
			if (extractAction != null) {
				extractAction.update(selection);
			}
		} else {
			if (activeWindow != null) {
				IWorkbenchPage activePage = activeWindow.getActivePage();
				if (activePage != null) {
					IWorkbenchPart activePart = activePage.getActivePart();
					if (activePart != null) {
						IWorkbenchPartSite activeSite = activePart.getSite();
						if (activeSite != null) {
							if (extractAction.getSite() != activeSite) {
								init(activeWindow);
							}

							if (extractAction != null) {
								extractAction.update(selection);
							}
						}
					}
				};
			}
		}
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (extractAction == null && targetEditor != null && targetEditor instanceof PHPStructuredEditor) {
			init((PHPStructuredEditor) targetEditor);
		}
	}
}
