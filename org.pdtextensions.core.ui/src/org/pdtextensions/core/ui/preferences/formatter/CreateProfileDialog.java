/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.internal.ui.util.SWTUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.preferences.formatter.ProfileManager.CustomProfile;
import org.pdtextensions.core.ui.preferences.formatter.ProfileManager.Profile;

/**
 * The dialog to create a new profile.
 */
public class CreateProfileDialog extends StatusDialog {

	private static final String PREF_OPEN_EDIT_DIALOG = PEXUIPlugin.PLUGIN_ID
			+ ".codeformatter.create_profile_dialog.open_edit"; //$NON-NLS-1$

	private Text fNameText;
	private Combo fProfileCombo;
	private Button fEditCheckbox;

	private final static StatusInfo fOk = new StatusInfo();
	private final static StatusInfo fEmpty = new StatusInfo(
			IStatus.ERROR,
			FormatterMessages.CreateProfileDialog_status_message_profile_name_is_empty);
	private final static StatusInfo fDuplicate = new StatusInfo(
			IStatus.ERROR,
			FormatterMessages.CreateProfileDialog_status_message_profile_with_this_name_already_exists);

	private final ProfileManager fProfileManager;
	private final List fSortedProfiles;
	private final String[] fSortedNames;

	private CustomProfile fCreatedProfile;
	protected boolean fOpenEditDialog;

	private final IProfileVersioner fProfileVersioner;

	public CreateProfileDialog(Shell parentShell,
			ProfileManager profileManager, IProfileVersioner profileVersioner) {
		super(parentShell);
		fProfileManager = profileManager;
		fProfileVersioner = profileVersioner;
		fSortedProfiles = fProfileManager.getSortedProfiles();
		fSortedNames = fProfileManager.getSortedDisplayNames();
	}

	public void create() {
		super.create();
		setTitle(FormatterMessages.CreateProfileDialog_dialog_title);
	}

	public Control createDialogArea(Composite parent) {

		final int numColumns = 2;

		final Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout) composite.getLayout()).numColumns = numColumns;

		// Create "Profile name:" label
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		gd.widthHint = convertWidthInCharsToPixels(60);
		final Label nameLabel = new Label(composite, SWT.WRAP);
		nameLabel
				.setText(FormatterMessages.CreateProfileDialog_profile_name_label_text);
		nameLabel.setLayoutData(gd);

		// Create text field to enter name
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		fNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fNameText.setLayoutData(gd);
		fNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doValidation();
			}
		});

		// Create "Initialize settings ..." label
		gd = new GridData();
		gd.horizontalSpan = numColumns;
		Label profileLabel = new Label(composite, SWT.WRAP);
		profileLabel
				.setText(FormatterMessages.CreateProfileDialog_base_profile_label_text);
		profileLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		fProfileCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fProfileCombo.setLayoutData(gd);
		SWTUtil.setDefaultVisibleItemCount(fProfileCombo);

		// "Open the edit dialog now" checkbox
		gd = new GridData();
		gd.horizontalSpan = numColumns;
		fEditCheckbox = new Button(composite, SWT.CHECK);
		fEditCheckbox
				.setText(FormatterMessages.CreateProfileDialog_open_edit_dialog_checkbox_text);
		fEditCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				fOpenEditDialog = ((Button) e.widget).getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final IDialogSettings dialogSettings = PEXUIPlugin.getDefault()
				.getDialogSettings();//.get(PREF_OPEN_EDIT_DIALOG);
		if (dialogSettings.get(PREF_OPEN_EDIT_DIALOG) != null) {
			fOpenEditDialog = dialogSettings.getBoolean(PREF_OPEN_EDIT_DIALOG);
		} else {
			fOpenEditDialog = true;
		}
		fEditCheckbox.setSelection(fOpenEditDialog);

		fProfileCombo.setItems(fSortedNames);
		fProfileCombo.setText(fProfileManager.getDefaultProfile().getName());
		updateStatus(fEmpty);

		applyDialogFont(composite);

		fNameText.setFocus();

		return composite;
	}

	/**
	 * Validate the current settings
	 */
	protected void doValidation() {
		final String name = fNameText.getText().trim();

		if (fProfileManager.containsName(name)) {
			updateStatus(fDuplicate);
			return;
		}
		if (name.length() == 0) {
			updateStatus(fEmpty);
			return;
		}
		updateStatus(fOk);
	}

	protected void okPressed() {
		if (!getStatus().isOK())
			return;

		PEXUIPlugin.getDefault().getDialogSettings()
				.put(PREF_OPEN_EDIT_DIALOG, fOpenEditDialog);

		final Map baseSettings = new HashMap(
				((Profile) fSortedProfiles.get(fProfileCombo
						.getSelectionIndex())).getSettings());
		final String profileName = fNameText.getText();

		fCreatedProfile = new CustomProfile(profileName, baseSettings,
				fProfileVersioner.getCurrentVersion(),
				fProfileVersioner.getProfileKind());
		fProfileManager.addProfile(fCreatedProfile);
		super.okPressed();
	}

	public final CustomProfile getCreatedProfile() {
		return fCreatedProfile;
	}

	public final boolean openEditDialog() {
		return fOpenEditDialog;
	}
}
