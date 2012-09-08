/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.csfixer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IStringButtonAdapter;
import org.eclipse.php.internal.ui.wizards.fields.LayoutUtil;
import org.eclipse.php.internal.ui.wizards.fields.StringButtonDialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.pdtextensions.core.ui.preferences.csfixer.PHPCSFixerConfigurationBlock.FixerPhar;


/**
 * Dialog to enter a na new task tag
 */
@SuppressWarnings("restriction")
public class LibraryInputDialog extends StatusDialog {

	
	private class LibraryStandardInputAdapter implements IDialogFieldListener {
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}
	}

	public static final boolean WINDOWS = java.io.File.separatorChar == '\\'; //$NON-NLS-1$

	private final StringDialogField fNameDialogField;
	private final StringButtonDialogField fPathDialogField;

	private final List<String> fExistingNames;

	public LibraryInputDialog(Shell parent, FixerPhar lib, List<FixerPhar> existingEntries) {
		super(parent);

		fExistingNames = new ArrayList<String>(existingEntries.size());
		for (int i = 0; i < existingEntries.size(); i++) {
			FixerPhar curr = existingEntries.get(i);
			if (!curr.equals(lib)) {
				fExistingNames.add(curr.name);
			}
		}

		if (lib == null) {
			setTitle("New Library Path");
		} else {
			setTitle("Edit Library Path");
		}

		fPathDialogField = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setFilterPath(fPathDialogField.getText());
				dialog.setText("Select the library path");
				dialog.setMessage("Please select the path which represent the PEAR library.");
				String newPath = dialog.open();
				if (newPath != null) {
					fPathDialogField.setText(newPath);
					doValidation();
				}
			}
		});
		fPathDialogField.setLabelText("Path:");
		fPathDialogField.setButtonLabel("Browse...");
		fPathDialogField.setText((lib != null) ? lib.path : ""); //$NON-NLS-1$

		fNameDialogField = new StringDialogField();
		fNameDialogField.setLabelText("Name:");
		LibraryStandardInputAdapter adapter = new LibraryStandardInputAdapter();
		fNameDialogField.setDialogFieldListener(adapter);
		fNameDialogField.setText((lib != null) ? lib.name : ""); //$NON-NLS-1$
	}

	public FixerPhar getResult() {
		FixerPhar lib = new FixerPhar();
		lib.name = fNameDialogField.getText().trim();
		lib.custom = true;
		lib.path = fPathDialogField.getText().trim();
		return lib;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		inner.setLayout(layout);

		fNameDialogField.doFillIntoGrid(inner, 3);

		LayoutUtil.setHorizontalGrabbing(fNameDialogField.getTextControl(null));
		LayoutUtil.setWidthHint(fNameDialogField.getTextControl(null), convertWidthInCharsToPixels(60));

		fNameDialogField.postSetFocusOnDialogField(parent.getDisplay());

		fPathDialogField.doFillIntoGrid(inner, 3);

		applyDialogFont(composite);
		return composite;
	}

	private void doValidation() {
		StatusInfo status = new StatusInfo();

		String newName = fNameDialogField.getText();
		String newPath = fPathDialogField.getText();

		if (newPath.length() == 0) {
			status.setError("Enter PEAR path.");
		}

		if (newName.length() == 0 && newPath.length() > 0) {
			int lastIndex = WINDOWS ? newPath.lastIndexOf("\\") : newPath.lastIndexOf("/");
			if (lastIndex > 0) {
				newName = newPath.substring(lastIndex + 1).replace(' ', '_');
				fNameDialogField.setText(newName);
			}
		}

		if (newName.length() == 0) {
			status.setError("Enter library name.");
		} else {
			if (!Pattern.matches("^[a-zA-Z0-9_]+$", newName)) {
				status.setError("Name can only contain letters, numbers and underscores");
			} else if (fExistingNames.contains(newName)) {
				status.setError("An entry with the same name already exists");
			}
		}

		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// TODO - Add the Help contex id
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IPHPHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
}
