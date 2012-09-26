/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

public class BlankLinesTabPage extends FormatterTabPage {

	private final String PREVIEW = createPreviewHeader(FormatterMessages.BlankLinesTabPage_preview_header)
			+ "include \"util/List\";\n" + //$NON-NLS-1$
			"require \"util/Vector\";\n\n" + //$NON-NLS-1$
			"include_once('net/Socket');\n" + //$NON-NLS-1$
			"class Another {}" + //$NON-NLS-1$
			"class Pair {" + //$NON-NLS-1$
			"  public $first;" + //$NON-NLS-1$
			"  public $second;\n" + //$NON-NLS-1$
			"  // Between here...\n" + //$NON-NLS-1$
			"\n\n\n\n\n\n\n\n\n\n" + //$NON-NLS-1$
			"  // ...and here are 10 blank lines\n" + //$NON-NLS-1$
			"}" + //$NON-NLS-1$
			"class Example {" + //$NON-NLS-1$
			"  private $fList;" + //$NON-NLS-1$
			"  public $counter;" + //$NON-NLS-1$
			"  public function Example(LinkedList $list) {" + //$NON-NLS-1$
			"    $fList= $list;" + //$NON-NLS-1$
			"    $counter= 0;" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"  public function push(Pair $p) {" + //$NON-NLS-1$
			"    $fList->add($p);" + //$NON-NLS-1$
			"    ++$counter;" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"  public function pop() {" + //$NON-NLS-1$
			"    --$counter;" + //$NON-NLS-1$
			"    return $fList->getLast();" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"}"; //$NON-NLS-1$

	private final static int MIN_NUMBER_LINES = 0;
	private final static int MAX_NUMBER_LINES = 99;

	private PHPSourcePreview fPreview;

	/**
	 * Create a new BlankLinesTabPage.
	 * @param modifyDialog The main configuration dialog
	 *
	 * @param workingValues The values wherein the options are stored.
	 */
	public BlankLinesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
		Group group;

		//		group = createGroup(
		//				numColumns,
		//				composite,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_group_title);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_before_package,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_PACKAGE);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_after_package,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_PACKAGE);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_before_import,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_IMPORTS);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_between_import_groups,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BETWEEN_IMPORT_GROUPS);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_after_import,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_compilation_unit_option_between_type_declarations,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BETWEEN_TYPE_DECLARATIONS);

		group = createGroup(numColumns, composite,
				FormatterMessages.BlankLinesTabPage_class_group_title);
		createBlankLineTextField(
				group,
				numColumns,
				FormatterMessages.BlankLinesTabPage_class_option_before_first_decl,
				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIRST_CLASS_BODY_DECLARATION);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_class_option_before_decls_of_same_kind,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_NEW_CHUNK);
		//		createBlankLineTextField(
		//				group,
		//				numColumns,
		//				FormatterMessages.BlankLinesTabPage_class_option_before_member_class_decls,
		//				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MEMBER_TYPE);
		createBlankLineTextField(
				group,
				numColumns,
				FormatterMessages.BlankLinesTabPage_class_option_before_field_decls,
				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_FIELD);
		createBlankLineTextField(
				group,
				numColumns,
				FormatterMessages.BlankLinesTabPage_class_option_before_method_decls,
				CodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD);
		createBlankLineTextField(
				group,
				numColumns,
				FormatterMessages.BlankLinesTabPage_class_option_at_beginning_of_method_body,
				CodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY);

		group = createGroup(numColumns, composite,
				FormatterMessages.BlankLinesTabPage_blank_lines_group_title);
		createBlankLineTextField(
				group,
				numColumns,
				FormatterMessages.BlankLinesTabPage_blank_lines_option_empty_lines_to_preserve,
				CodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE);
	}

	protected void initializePage() {
		fPreview.setPreviewText(PREVIEW);
	}

	/*
	 * A helper method to create a number preference for blank lines.
	 */
	private void createBlankLineTextField(Composite composite, int numColumns,
			String message, String key) {
		createNumberPref(composite, numColumns, message, key, MIN_NUMBER_LINES,
				MAX_NUMBER_LINES);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected PHPPreview doCreatePHPPreview(Composite parent) {
		fPreview = new PHPSourcePreview(fWorkingValues, parent);
		return fPreview;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doUpdatePreview()
	 */
	protected void doUpdatePreview() {
		super.doUpdatePreview();
		fPreview.update();
	}
}
