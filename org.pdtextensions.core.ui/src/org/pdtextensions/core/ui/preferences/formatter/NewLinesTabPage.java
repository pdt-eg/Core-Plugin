/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brock Janiczak <brockj@tpg.com.au> - [formatter] Add  option: "add new line after label" - https://bugs.eclipse.org/bugs/show_bug.cgi?id=150741
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

public class NewLinesTabPage extends FormatterTabPage {

	private final String PREVIEW = createPreviewHeader(FormatterMessages.NewLinesTabPage_preview_header)
			+ "namespace Acme\\Demo;\n"
			+ "use Acme\\Foobar\\SomeClass;\n"
			+ "use Acme\\Foobar\\SomeOtherClass;\n"
			+ "class EmptyClass {}\n"
			+ "class Example {"
			+ "  var $fArray= array(1, 2, 3, 4, 5 );"
			+ "  var $fListener;"
			+ "  \n"
			+ "  public function\nbar\n(\n$p)\n {}\n"
			+ "  function foo() {"
			+ "    $arr=array();"
			+ "    label:"
			+ "    do {} while (false);" + "    for (;;) {}" + "  }" + "}";

	private CheckboxPreference fArrayNewLineAfterParenPref;
	private CheckboxPreference fArrayNewLineBeforeParenPref;

	private PHPSourcePreview fPreview;

	public NewLinesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {

		final Group newlinesGroup = createGroup(numColumns, composite,
				FormatterMessages.NewLinesTabPage_newlines_group_title);
		
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_after_namespace_declaration,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_NAMESPACE_DECLARATION,
				DO_NOT_INSERT_INSERT);
		
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_class_body,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_TYPE_DECLARATION,
				DO_NOT_INSERT_INSERT);
		//		createPref(
		//				newlinesGroup,
		//				numColumns,
		//				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_anonymous_class_body,
		//				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_ANONYMOUS_TYPE_DECLARATION,
		//				DO_NOT_INSERT_INSERT);
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_method_body,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY,
				DO_NOT_INSERT_INSERT);
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_block,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK,
				DO_NOT_INSERT_INSERT);
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_label,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_LABEL,
				DO_NOT_INSERT_INSERT);
		createPref(
				newlinesGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_newlines_group_option_empty_end_of_file,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING,
				DO_NOT_INSERT_INSERT);

		final Group arrayInitializerGroup = createGroup(numColumns, composite,
				FormatterMessages.NewLinesTabPage_arrayInitializer_group_title);
		//		createPref(
		//				arrayInitializerGroup,
		//				numColumns,
		//				FormatterMessages.NewLinesTabPage_array_group_option_after_opening_brace_of_array_initializer,
		//				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER,
		//				DO_NOT_INSERT_INSERT);
		// added
		fArrayNewLineAfterParenPref = createPref(
				arrayInitializerGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_array_group_option_after_opening_brace_of_array_initializer,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER,
				DO_NOT_INSERT_INSERT);
		Label lbl = new Label(arrayInitializerGroup, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = fPixelConverter.convertWidthInCharsToPixels(4);
		lbl.setLayoutData(gd);
		fArrayNewLineBeforeParenPref = createPref(
				arrayInitializerGroup,
				numColumns - 1,
				FormatterMessages.NewLinesTabPage_array_group_option_after_opening_brace_of_array_initializer_in_arguments,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER_IN_ARGUMENTS,
				DO_NOT_INSERT_INSERT);
		fArrayNewLineAfterParenPref.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				fArrayNewLineBeforeParenPref
						.setEnabled(fArrayNewLineAfterParenPref.getChecked());
			}
		});
		fArrayNewLineBeforeParenPref.setEnabled(fArrayNewLineAfterParenPref
				.getChecked());
		//end

		createPref(
				arrayInitializerGroup,
				numColumns,
				FormatterMessages.NewLinesTabPage_array_group_option_before_closing_brace_of_array_initializer,
				CodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER,
				DO_NOT_INSERT_INSERT);

		//		final Group emptyStatementsGroup = createGroup(numColumns, composite,
		//				FormatterMessages.NewLinesTabPage_empty_statement_group_title);
		//		createPref(
		//				emptyStatementsGroup,
		//				numColumns,
		//				FormatterMessages.NewLinesTabPage_emtpy_statement_group_option_empty_statement_on_new_line,
		//				CodeFormatterConstants.FORMATTER_PUT_EMPTY_STATEMENT_ON_NEW_LINE,
		//				FALSE_TRUE);

	}

	protected void initializePage() {
		fPreview.setPreviewText(PREVIEW);
	}

	protected PHPPreview doCreatePHPPreview(Composite parent) {
		fPreview = new PHPSourcePreview(fWorkingValues, parent);
		return fPreview;
	}

	protected void doUpdatePreview() {
		super.doUpdatePreview();
		fPreview.update();
	}

	private CheckboxPreference createPref(Composite composite, int numColumns,
			String message, String key, String[] values) {
		return createCheckboxPref(composite, numColumns, message, key, values);
	}
}
