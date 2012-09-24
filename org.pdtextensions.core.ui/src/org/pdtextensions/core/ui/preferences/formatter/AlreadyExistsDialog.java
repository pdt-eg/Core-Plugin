/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Martin Monperrus <martin.monperrus@gmail.com> - AlreadyExistsDialog.initializeComposite clones Dialog.createDialogArea - https://bugs.eclipse.org/bugs/show_bug.cgi?id=296781
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.Messages;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pdtextensions.core.ui.preferences.formatter.ProfileManager.CustomProfile;


/**
 * The dialog to rename an imported profile.
 */
public class AlreadyExistsDialog extends StatusDialog {

	private Composite fComposite;
	protected Text fNameText;
	private Button fRenameRadio, fOverwriteRadio;

	private final int NUM_COLUMNS = 2;

	private final StatusInfo fOk;
	private final StatusInfo fEmpty;
	private final StatusInfo fDuplicate;

	private final CustomProfile fProfile;
	private final ProfileManager fProfileManager;

	public AlreadyExistsDialog(Shell parentShell, CustomProfile profile,
			ProfileManager profileManager) {
		super(parentShell);
		fProfile = profile;
		fProfileManager = profileManager;
		fOk = new StatusInfo();
		fDuplicate = new StatusInfo(
				IStatus.ERROR,
				FormatterMessages.AlreadyExistsDialog_message_profile_already_exists);
		fEmpty = new StatusInfo(
				IStatus.ERROR,
				FormatterMessages.AlreadyExistsDialog_message_profile_name_empty);

		setHelpAvailable(false);
	}

	public void create() {
		super.create();
		setTitle(FormatterMessages.AlreadyExistsDialog_dialog_title);
	}

	public Control createDialogArea(Composite parent) {
		fComposite = (Composite) super.createDialogArea(parent);
		((GridLayout) fComposite.getLayout()).numColumns = NUM_COLUMNS;

		createLabel(Messages.format(
				FormatterMessages.AlreadyExistsDialog_dialog_label,
				fProfile.getName()));

		fRenameRadio = createRadioButton(FormatterMessages.AlreadyExistsDialog_rename_radio_button_desc);
		fNameText = createTextField();

		fOverwriteRadio = createRadioButton(FormatterMessages.AlreadyExistsDialog_overwrite_radio_button_desc);

		fRenameRadio.setSelection(true);

		fNameText.setText(fProfile.getName());
		fNameText.setSelection(0, fProfile.getName().length());
		fNameText.setFocus();

		fNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doValidation();
			}
		});

		fRenameRadio.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fNameText.setEnabled(true);
				fNameText.setFocus();
				fNameText.setSelection(0, fNameText.getText().length());
				doValidation();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		fOverwriteRadio.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fNameText.setEnabled(false);
				doValidation();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		updateStatus(fDuplicate);

		applyDialogFont(fComposite);

		return fComposite;
	}

	private Label createLabel(String text) {
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = NUM_COLUMNS;
		gd.widthHint = convertWidthInCharsToPixels(60);
		final Label label = new Label(fComposite, SWT.WRAP);
		label.setText(text);
		label.setLayoutData(gd);
		return label;
	}

	private Button createRadioButton(String text) {
		final GridData gd = new GridData();
		gd.horizontalSpan = NUM_COLUMNS;
		gd.widthHint = convertWidthInCharsToPixels(60);
		final Button radio = new Button(fComposite, SWT.RADIO);
		radio.setLayoutData(gd);
		radio.setText(text);
		return radio;
	}

	private Text createTextField() {
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = NUM_COLUMNS;
		gd.horizontalIndent = 15;
		final Text text = new Text(fComposite, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(gd);
		return text;
	}

	/**
	 * Validate the current settings
	 */
	protected void doValidation() {

		if (fOverwriteRadio.getSelection()) {
			updateStatus(fOk);
			return;
		}

		final String name = fNameText.getText().trim();

		if (name.length() == 0) {
			updateStatus(fEmpty);
			return;
		}

		if (fProfileManager.containsName(name)) {
			updateStatus(fDuplicate);
			return;
		}

		updateStatus(fOk);
	}

	protected void okPressed() {
		if (!getStatus().isOK())
			return;
		if (fRenameRadio.getSelection())
			fProfile.rename(fNameText.getText().trim(), fProfileManager);
		super.okPressed();
	}

}
