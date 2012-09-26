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

import java.util.Map;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

public class BracesTabPage extends FormatterTabPage {

	private final String PREVIEW = createPreviewHeader(FormatterMessages.BracesTabPage_preview_header)
			+ "interface TheEmpty {}\n" + //$NON-NLS-1$
			"\n" + //$NON-NLS-1$
			"class Example {" + //$NON-NLS-1$
			"  var $fField;" + //$NON-NLS-1$
			"  var $myArray= array(1,2,3,4,5,6);" + //$NON-NLS-1$
			"  var $emptyArray= array();" + //$NON-NLS-1$
			"  function Example() {" + //$NON-NLS-1$
			"   $this->fField= new SomeClass();" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"  function bar($p) {" + //$NON-NLS-1$
			"    for ($i= 0; $i<10; $i++) {" + //$NON-NLS-1$
			"    }" + //$NON-NLS-1$
			"    switch($p) {" + //$NON-NLS-1$
			"      case 0:" + //$NON-NLS-1$
			"        $fField->set(0);" + //$NON-NLS-1$
			"        break;" + //$NON-NLS-1$
			"      case 1: {" + //$NON-NLS-1$
			"        break;" + //$NON-NLS-1$
			"        }" + //$NON-NLS-1$
			"      default:" + //$NON-NLS-1$
			"        $fField->reset();" + //$NON-NLS-1$
			"    }" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"}"; //$NON-NLS-1$

	private PHPSourcePreview fPreview;

	private final String[] fBracePositions = {
			CodeFormatterConstants.END_OF_LINE,
			CodeFormatterConstants.NEXT_LINE,
			CodeFormatterConstants.NEXT_LINE_SHIFTED };

	private final String[] fExtendedBracePositions = {
			CodeFormatterConstants.END_OF_LINE,
			CodeFormatterConstants.NEXT_LINE,
			CodeFormatterConstants.NEXT_LINE_SHIFTED,
	//			CodeFormatterConstants.NEXT_LINE_ON_WRAP
	};

	private final String[] fBracePositionNames = {
			FormatterMessages.BracesTabPage_position_same_line,
			FormatterMessages.BracesTabPage_position_next_line,
			FormatterMessages.BracesTabPage_position_next_line_indented };

	private final String[] fExtendedBracePositionNames = {
			FormatterMessages.BracesTabPage_position_same_line,
			FormatterMessages.BracesTabPage_position_next_line,
			FormatterMessages.BracesTabPage_position_next_line_indented,
	//			FormatterMessages.BracesTabPage_position_next_line_on_wrap
	};

	/**
	 * Creates a new BracesTabPage.
	 * 
	 * @param modifyDialog the modify dialog
	 * @param workingValues the working values
	 */
	public BracesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
		final Group group = createGroup(numColumns, composite,
				FormatterMessages.BracesTabPage_group_brace_positions_title);
		createExtendedBracesCombo(
				group,
				numColumns,
				FormatterMessages.BracesTabPage_option_namespace_declaration,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_NAMESPACE_DECLARATION);
		createExtendedBracesCombo(
				group,
				numColumns,
				FormatterMessages.BracesTabPage_option_class_declaration,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION);
		//		createExtendedBracesCombo(
		//				group,
		//				numColumns,
		//				FormatterMessages.BracesTabPage_option_anonymous_class_declaration,
		//				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE_DECLARATION);
		//		createExtendedBracesCombo(
		//				group,
		//				numColumns,
		//				FormatterMessages.BracesTabPage_option_constructor_declaration,
		//				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONSTRUCTOR_DECLARATION);
		createExtendedBracesCombo(
				group,
				numColumns,
				FormatterMessages.BracesTabPage_option_method_declaration,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION);
		createExtendedBracesCombo(group, numColumns,
				FormatterMessages.BracesTabPage_option_blocks,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
		createExtendedBracesCombo(
				group,
				numColumns,
				FormatterMessages.BracesTabPage_option_blocks_in_case,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE);
		createBracesCombo(group, numColumns,
				FormatterMessages.BracesTabPage_option_switch_case,
				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH);

		//		ComboPreference arrayInitOption = createBracesCombo(
		//				group,
		//				numColumns,
		//				FormatterMessages.BracesTabPage_option_array_initializer,
		//				CodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER);
		//		final CheckboxPreference arrayInitCheckBox = createIndentedCheckboxPref(
		//				group,
		//				numColumns,
		//				FormatterMessages.BracesTabPage_option_keep_empty_array_initializer_on_one_line,
		//				CodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE,
		//				FALSE_TRUE);
		//
		//		arrayInitOption.addObserver(new Observer() {
		//			public void update(Observable o, Object arg) {
		//				updateOptionEnablement((ComboPreference) o, arrayInitCheckBox);
		//			}
		//		});
		//		updateOptionEnablement(arrayInitOption, arrayInitCheckBox);
	}

	protected final void updateOptionEnablement(
			ComboPreference arrayInitOption,
			CheckboxPreference arrayInitCheckBox) {
		arrayInitCheckBox.setEnabled(!arrayInitOption
				.hasValue(CodeFormatterConstants.END_OF_LINE));
	}

	protected void initializePage() {
		fPreview.setPreviewText(PREVIEW);
	}

	protected PHPPreview doCreatePHPPreview(Composite parent) {
		fPreview = new PHPSourcePreview(fWorkingValues, parent);
		return fPreview;
	}

	private ComboPreference createBracesCombo(Composite composite,
			int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key,
				fBracePositions, fBracePositionNames);
	}

	private ComboPreference createExtendedBracesCombo(Composite composite,
			int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key,
				fExtendedBracePositions, fExtendedBracePositionNames);
	}

	@SuppressWarnings("unused")
	private CheckboxPreference createIndentedCheckboxPref(Composite composite,
			int numColumns, String message, String key, String[] values) {
		CheckboxPreference pref = createCheckboxPref(composite, numColumns,
				message, key, values);
		GridData data = (GridData) pref.getControl().getLayoutData();
		data.horizontalIndent = fPixelConverter.convertWidthInCharsToPixels(1);
		return pref;
	}

	protected void doUpdatePreview() {
		super.doUpdatePreview();
		fPreview.update();
	}
}
