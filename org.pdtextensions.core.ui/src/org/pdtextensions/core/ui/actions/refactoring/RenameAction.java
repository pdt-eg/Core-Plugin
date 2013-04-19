/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.actions.refactoring;

import org.eclipse.dltk.internal.ui.actions.ActionUtil;
import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.internal.ui.refactoring.actions.RenameResourceAction;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IWorkbenchSite;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameAction extends SelectionDispatchAction {
	private RenamePHPElementAction renamePHPElementAction;
	private RenameResourceAction renameResourceAction;
	private PHPStructuredEditor editor;

	public RenameAction(IWorkbenchSite site) {
		super(site);

		setText(RefactoringMessages.RenameAction_text); 
		renamePHPElementAction = new RenamePHPElementAction(site);
		renamePHPElementAction.setText(getText());
		renameResourceAction = new RenameResourceAction(site);
		renameResourceAction.setText(getText());
	}

	public RenameAction(PHPStructuredEditor editor) {
		this(editor.getEditorSite());
		this.editor = editor;
		renamePHPElementAction = new RenamePHPElementAction(editor);
	}

	private boolean computeEnabledState(){
		return renamePHPElementAction.isEnabled() || renameResourceAction.isEnabled();
	}

	@Override
	public void run(IStructuredSelection selection) {
		if (renamePHPElementAction.isEnabled()) {
			renamePHPElementAction.run(selection);
		}

		if (renameResourceAction.isEnabled()) {
			renameResourceAction.run(selection);
		}
	}

	@Override
	public void run(ITextSelection selection) {
		if (ActionUtil.isProcessable(getShell(), editor) && renamePHPElementAction.isEnabled()) {
			renamePHPElementAction.run(selection);
		}
	}

	@Override
	public void selectionChanged(ISelection selection) {
		renamePHPElementAction.update(selection);
		renameResourceAction.update(selection);

		setEnabled(computeEnabledState());
	}
}
