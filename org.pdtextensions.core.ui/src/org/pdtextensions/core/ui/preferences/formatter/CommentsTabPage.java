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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

/**
 * Tab page for the comment formatter settings.
 */
public class CommentsTabPage extends FormatterTabPage {

	private static abstract class Controller implements Observer {

		private final Collection fMasters;
		private final Collection fSlaves;

		public Controller(Collection masters, Collection slaves) {
			fMasters = masters;
			fSlaves = slaves;
			for (final Iterator iter = fMasters.iterator(); iter.hasNext();) {
				((CheckboxPreference) iter.next()).addObserver(this);
			}
		}

		public void update(Observable o, Object arg) {
			boolean enabled = areSlavesEnabled();

			for (final Iterator iter = fSlaves.iterator(); iter.hasNext();) {
				final Object obj = iter.next();
				if (obj instanceof Preference) {
					((Preference) obj).setEnabled(enabled);
				} else if (obj instanceof Control) {
					((Group) obj).setEnabled(enabled);
				}
			}
		}

		public Collection getMasters() {
			return fMasters;
		}

		protected abstract boolean areSlavesEnabled();
	}

	private final static class OrController extends Controller {

		public OrController(Collection masters, Collection slaves) {
			super(masters, slaves);
			update(null, null);
		}

		/**
		 * {@inheritDoc}
		 */
		protected boolean areSlavesEnabled() {
			for (final Iterator iter = getMasters().iterator(); iter.hasNext();) {
				if (((CheckboxPreference) iter.next()).getChecked())
					return true;
			}
			return false;
		}
	}

	private final String PREVIEW = createPreviewHeader("An example for comment formatting. This example is meant to illustrate the various possibilities offered by <i>Eclipse</i> in order to format comments.")
			+ "/**\n"
			+ " * This is the comment for the example interface.\n"
			+ " */\n"
			+ " interface Example {\n"
			+ "// This is a long comment    with\twhitespace     that should be split in multiple line comments in case the line comment formatting is enabled\n"
			+ "function foo3();\n"
			+ " \n"
			+ "//\tfunction commented() {\n"
			+ "//\t\t\techo \"indented\";\n"
			+ "//\t}\n"
			+ "\n"
			+ "\t//\tfunction indentedCommented() {\n"
			+ "\t//\t\t\techo \"indented\";\n"
			+ "\t//\t}\n"
			+ "\n"
			+ "/* block comment          on first column*/\n"
			+ " function bar();\n"
			+ "\t/*\n"
			+ "\t*\n"
			+ "\t* These possibilities include:\n"
			+ "\t* <ul><li>Formatting of header comments.</li><li>Formatting of Javadoc tags</li></ul>\n"
			+ "\t*/\n"
			+ " function bar2(); // This is a long comment that should be split in multiple line comments in case the line comment formatting is enabled\n"
			+ " /**\n"
			+ " * The following is some sample code which illustrates source formatting within javadoc comments:\n"
			+ " * <pre>public class Example {final int a= 1;final boolean b= true;}</pre>\n"
			+ " * Descriptions of parameters and return values are best appended at end of the javadoc comment.\n"
			+ " * @param a The first parameter. For an optimum result, this should be an odd number\n"
			+ " * between 0 and 100.\n"
			+ " * @param b The second parameter.\n"
			+ " * @return The result of the foo operation, usually within 0 and 1000.\n"
			+ " */\n" + " function foo($a, $b);\n" + "}";

	private PHPSourcePreview fPreview;

	public CommentsTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
		final int indent = fPixelConverter.convertWidthInCharsToPixels(4);

		// global group
		final Group globalGroup = createGroup(numColumns, composite,
				FormatterMessages.CommentsTabPage_group1_title);
		final CheckboxPreference javadoc = createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.commentsTabPage_enable_javadoc_comment_formatting,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT,
				false);
		final CheckboxPreference blockComment = createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_enable_block_comment_formatting,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_BLOCK_COMMENT,
				false);
		final CheckboxPreference singleLineComments = createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_enable_line_comment_formatting,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_LINE_COMMENT,
				false);
		final CheckboxPreference singleLineCommentsOnFirstColumn = createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_format_line_comments_on_first_column,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_LINE_COMMENT_STARTING_ON_FIRST_COLUMN,
				false);
		((GridData) singleLineCommentsOnFirstColumn.getControl()
				.getLayoutData()).horizontalIndent = indent;
		final CheckboxPreference header = createPrefFalseTrue(globalGroup,
				numColumns, FormatterMessages.CommentsTabPage_format_header,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HEADER, false);
		GridData spacerData = new GridData(0, 0);
		spacerData.horizontalSpan = numColumns;
		new Composite(globalGroup, SWT.NONE).setLayoutData(spacerData);
		createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_never_indent_block_comments_on_first_column,
				CodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN,
				false);
		createPrefFalseTrue(
				globalGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_never_indent_line_comments_on_first_column,
				CodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN,
				false);
		createPrefFalseTrue(globalGroup, numColumns,
				FormatterMessages.CommentsTabPage_do_not_join_lines,
				CodeFormatterConstants.FORMATTER_JOIN_LINES_IN_COMMENTS, true);

		// javadoc comment formatting settings
		final Group settingsGroup = createGroup(numColumns, composite,
				FormatterMessages.CommentsTabPage_group2_title);
		final CheckboxPreference html = createPrefFalseTrue(settingsGroup,
				numColumns, FormatterMessages.CommentsTabPage_format_html,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_HTML, false);
		final CheckboxPreference code = createPrefFalseTrue(settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_format_code_snippets,
				CodeFormatterConstants.FORMATTER_COMMENT_FORMAT_SOURCE, false);
		final CheckboxPreference blankJavadoc = createPrefInsert(
				settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_blank_line_before_javadoc_tags,
				CodeFormatterConstants.FORMATTER_COMMENT_INSERT_EMPTY_LINE_BEFORE_ROOT_TAGS);
		final CheckboxPreference indentJavadoc = createPrefFalseTrue(
				settingsGroup, numColumns,
				FormatterMessages.CommentsTabPage_indent_javadoc_tags,
				CodeFormatterConstants.FORMATTER_COMMENT_INDENT_ROOT_TAGS,
				false);
		final CheckboxPreference indentDesc = createPrefFalseTrue(
				settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_indent_description_after_param,
				CodeFormatterConstants.FORMATTER_COMMENT_INDENT_PARAMETER_DESCRIPTION,
				false);
		((GridData) indentDesc.getControl().getLayoutData()).horizontalIndent = indent;
		final CheckboxPreference nlParam = createPrefInsert(
				settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_new_line_after_param_tags,
				CodeFormatterConstants.FORMATTER_COMMENT_INSERT_NEW_LINE_FOR_PARAMETER);
		final CheckboxPreference nlBoundariesJavadoc = createPrefFalseTrue(
				settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_new_lines_at_javadoc_boundaries,
				CodeFormatterConstants.FORMATTER_COMMENT_NEW_LINES_AT_JAVADOC_BOUNDARIES,
				false);
		final CheckboxPreference blankLinesJavadoc = createPrefFalseTrue(
				settingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_clear_blank_lines,
				CodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_JAVADOC_COMMENT,
				false);

		// block comment settings
		final Group blockSettingsGroup = createGroup(numColumns, composite,
				FormatterMessages.CommentsTabPage_group4_title);
		final CheckboxPreference nlBoundariesBlock = createPrefFalseTrue(
				blockSettingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_new_lines_at_comment_boundaries,
				CodeFormatterConstants.FORMATTER_COMMENT_NEW_LINES_AT_BLOCK_BOUNDARIES,
				false);
		final CheckboxPreference blankLinesBlock = createPrefFalseTrue(
				blockSettingsGroup,
				numColumns,
				FormatterMessages.CommentsTabPage_remove_blank_block_comment_lines,
				CodeFormatterConstants.FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT,
				false);

		// line width settings
		final Group widthGroup = createGroup(numColumns, composite,
				FormatterMessages.CommentsTabPage_group3_title);
		final NumberPreference lineWidth = createNumberPref(widthGroup,
				numColumns, FormatterMessages.CommentsTabPage_line_width,
				CodeFormatterConstants.FORMATTER_COMMENT_LINE_LENGTH, 0, 9999);

		ArrayList lineFirstColumnMasters = new ArrayList();
		lineFirstColumnMasters.add(singleLineComments);

		ArrayList lineFirstColumnSlaves = new ArrayList();
		lineFirstColumnSlaves.add(singleLineCommentsOnFirstColumn);

		new Controller(lineFirstColumnMasters, lineFirstColumnSlaves) {
			protected boolean areSlavesEnabled() {
				return singleLineComments.getChecked();
			}
		}.update(null, null);

		ArrayList javaDocMaster = new ArrayList();
		javaDocMaster.add(javadoc);
		javaDocMaster.add(header);

		ArrayList javaDocSlaves = new ArrayList();
		javaDocSlaves.add(settingsGroup);
		javaDocSlaves.add(html);
		javaDocSlaves.add(code);
		javaDocSlaves.add(blankJavadoc);
		javaDocSlaves.add(indentJavadoc);
		javaDocSlaves.add(nlParam);
		javaDocSlaves.add(nlBoundariesJavadoc);
		javaDocSlaves.add(blankLinesJavadoc);

		new OrController(javaDocMaster, javaDocSlaves);

		ArrayList indentMasters = new ArrayList();
		indentMasters.add(javadoc);
		indentMasters.add(header);
		indentMasters.add(indentJavadoc);

		ArrayList indentSlaves = new ArrayList();
		indentSlaves.add(indentDesc);

		new Controller(indentMasters, indentSlaves) {
			protected boolean areSlavesEnabled() {
				return (javadoc.getChecked() || header.getChecked())
						&& indentJavadoc.getChecked();
			}
		}.update(null, null);

		ArrayList blockMasters = new ArrayList();
		blockMasters.add(blockComment);
		blockMasters.add(header);

		ArrayList blockSlaves = new ArrayList();
		blockSlaves.add(blockSettingsGroup);
		blockSlaves.add(nlBoundariesBlock);
		blockSlaves.add(blankLinesBlock);

		new OrController(blockMasters, blockSlaves);

		ArrayList lineWidthMasters = new ArrayList();
		lineWidthMasters.add(javadoc);
		lineWidthMasters.add(blockComment);
		lineWidthMasters.add(singleLineComments);
		lineWidthMasters.add(header);

		ArrayList lineWidthSlaves = new ArrayList();
		lineWidthSlaves.add(widthGroup);
		lineWidthSlaves.add(lineWidth);

		new OrController(lineWidthMasters, lineWidthSlaves);
	}

	protected void initializePage() {
		fPreview.setPreviewText(PREVIEW);
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

	private CheckboxPreference createPrefFalseTrue(Composite composite,
			int numColumns, String text, String key, boolean invertPreference) {
		if (invertPreference)
			return createCheckboxPref(composite, numColumns, text, key,
					TRUE_FALSE);
		return createCheckboxPref(composite, numColumns, text, key, FALSE_TRUE);
	}

	private CheckboxPreference createPrefInsert(Composite composite,
			int numColumns, String text, String key) {
		return createCheckboxPref(composite, numColumns, text, key,
				DO_NOT_INSERT_INSERT);
	}
}
