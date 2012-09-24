/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

/**
 * Tab page for the on/off formatter tags.
 * 
 * @since 3.6
 */
public class OffOnTagsTabPage extends FormatterTabPage {

	public OffOnTagsTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
		createLabel(numColumns, composite,
				FormatterMessages.OffOnTagsTabPage_description);

		// Add some vertical space
		Label separator = new Label(composite, SWT.NONE);
		separator.setVisible(false);
		GridData data = new GridData(GridData.FILL, GridData.FILL, false,
				false, numColumns, 1);
		data.heightHint = fPixelConverter.convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

		final CheckboxPreference enablePref = createCheckboxPref(composite,
				numColumns, FormatterMessages.OffOnTagsTabPage_enableOffOnTags,
				CodeFormatterConstants.FORMATTER_USE_ON_OFF_TAGS, FALSE_TRUE);

		IInputValidator inputValidator = new IInputValidator() {
			/*
			 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage.StringPreference.Validator#isValid(java.lang.String)
			 * @since 3.6
			 */
			public String isValid(String input) {
				if (input.length() == 0)
					return null;

				if (Character.isWhitespace(input.charAt(0)))
					return FormatterMessages.OffOnTagsTabPage_error_startsWithWhitespace;

				if (Character.isWhitespace(input.charAt(input.length() - 1)))
					return FormatterMessages.OffOnTagsTabPage_error_endsWithWhitespace;

				return null;
			}
		};

		Composite tagComposite = new Composite(composite, SWT.NONE);
		final int indent = fPixelConverter.convertWidthInCharsToPixels(4);
		GridLayout layout = new GridLayout(numColumns, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = indent;
		tagComposite.setLayout(layout);

		final StringPreference disableTagPref = createStringPref(tagComposite,
				numColumns, FormatterMessages.OffOnTagsTabPage_disableTag,
				CodeFormatterConstants.FORMATTER_DISABLING_TAG, inputValidator);
		final StringPreference enableTagPref = createStringPref(tagComposite,
				numColumns, FormatterMessages.OffOnTagsTabPage_enableTag,
				CodeFormatterConstants.FORMATTER_ENABLING_TAG, inputValidator);

		enablePref.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				boolean enabled = enablePref.getChecked();
				enableTagPref.setEnabled(enabled);
				disableTagPref.setEnabled(enabled);
			}
		});

		boolean enabled = enablePref.getChecked();
		enableTagPref.setEnabled(enabled);
		disableTagPref.setEnabled(enabled);
	}

	public final Composite createContents(Composite parent) {
		if (fPixelConverter == null)
			fPixelConverter = new PixelConverter(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData());

		final int numColumns = 2;
		GridLayout layout = new GridLayout(numColumns, false);
		layout.verticalSpacing = (int) (1.5 * fPixelConverter
				.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		layout.horizontalSpacing = fPixelConverter
				.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight = fPixelConverter
				.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = fPixelConverter
				.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		composite.setLayout(layout);
		doCreatePreferences(composite, numColumns);

		return composite;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage() {
		// Nothing to do.
	}

	protected void doUpdatePreview() {
		// Nothing to do since this page has no preview.
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected PHPPreview doCreatePHPPreview(Composite parent) {
		return null; // This method won't be called.
	}

}
