/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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

/**
 * @since 0.17.0
 */
public class RenamePHPElementActionDelegate implements IPHPActionDelegator {
	private RenameAction renameAction;

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		renameAction = null;

		if (window != null) {
			IWorkbenchPart activePart = window.getPartService().getActivePart();
			if (activePart != null) {
				if (activePart instanceof PHPStructuredEditor) {
					renameAction = new RenameAction((PHPStructuredEditor) activePart);
				} else {
					renameAction = new RenameAction(activePart.getSite());
				}
			}
		}
	}

	private void init(PHPStructuredEditor editor) {
		renameAction = new RenameAction(editor);
	}

	@Override
	public void run(IAction action) {
		if (renameAction != null) {
			renameAction.run();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (renameAction == null) {
			init(activeWindow);
			if (renameAction != null) {
				renameAction.update(selection);
			}
		} else {
			if (activeWindow != null) {
				IWorkbenchPage activePage = activeWindow.getActivePage();
				if (activePage != null) {
					IWorkbenchPart activePart = activePage.getActivePart();
					if (activePart != null) {
						IWorkbenchPartSite activeSite = activePart.getSite();
						if (activeSite != null) {
							if (renameAction.getSite() != activeSite) {
								init(activeWindow);
							}

							if (renameAction != null) {
								renameAction.update(selection);
							}
						}
					}
				};
			}
		}
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (renameAction == null && targetEditor != null && targetEditor instanceof PHPStructuredEditor) {
			init((PHPStructuredEditor) targetEditor);
		}
	}
}
