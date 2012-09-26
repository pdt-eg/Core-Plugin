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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.internal.ui.util.SWTUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.php.internal.ui.util.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

/**
 * The line wrapping tab page.
 */
@SuppressWarnings({ "unused", "restriction" })
public class LineWrappingTabPage extends FormatterTabPage {
	/**
	 * Represents a line wrapping category. All members are final.
	 */
	private final static class Category {
		public final String key;
		public final String name;
		public final String previewText;
		public final List children;
		public final List preferences;

		public int index;

		public Category(String _key, String _previewText, String _name) {
			this.key = _key;
			this.name = _name;
			this.previewText = _previewText != null ? createPreviewHeader(_name)
					+ _previewText
					: null;
			children = new ArrayList();
			preferences = new ArrayList();
		}

		/**
		 * @param _name Category name
		 */
		public Category(String _name) {
			this(null, null, _name);
		}

		public String toString() {
			return name;
		}

		public void addPreference(Preference specificPreference) {
			preferences.add(specificPreference);
		}

		public Preference[] getSpecificPreferences() {
			return (Preference[]) preferences
					.toArray(new Preference[preferences.size()]);
		}
	}

	private final static String PREF_CATEGORY_INDEX = PEXUIPlugin.PLUGIN_ID
			+ ".formatter_page.line_wrapping_tab_page.last_category_index"; //$NON-NLS-1$

	private final class CategoryListener implements ISelectionChangedListener,
			IDoubleClickListener {

		private final List fCategoriesList;

		private int fIndex = 0;

		public CategoryListener(List categoriesTree) {
			fCategoriesList = new ArrayList();
			flatten(fCategoriesList, categoriesTree);
		}

		private void flatten(List categoriesList, List categoriesTree) {
			for (final Iterator iter = categoriesTree.iterator(); iter
					.hasNext();) {
				final Category category = (Category) iter.next();
				category.index = fIndex++;
				categoriesList.add(category);
				flatten(categoriesList, category.children);
			}
		}

		public void selectionChanged(SelectionChangedEvent event) {
			if (event != null)
				fSelection = (IStructuredSelection) event.getSelection();

			if (fSelection.size() == 0) {
				disableAll();
				return;
			}

			if (!fOptionsGroup.isEnabled())
				enableDefaultComponents(true);

			fSelectionState.refreshState(fSelection);

			final Category category = (Category) fSelection.getFirstElement();
			fDialogSettings.put(PREF_CATEGORY_INDEX, category.index);

			fOptionsGroup.setText(getGroupLabel(category));
		}

		private String getGroupLabel(Category category) {
			if (fSelection.size() == 1) {
				if (fSelectionState.getElements().size() == 1)
					return Messages.format(
							FormatterMessages.LineWrappingTabPage_group,
							category.name.toLowerCase());
				return Messages.format(
						FormatterMessages.LineWrappingTabPage_multi_group,
						new String[] {
								category.name.toLowerCase(),
								Integer.toString(fSelectionState.getElements()
										.size()) });
			}
			return Messages.format(
					FormatterMessages.LineWrappingTabPage_multiple_selections,
					new String[] { Integer.toString(fSelectionState
							.getElements().size()) });
		}

		private void disableAll() {
			enableDefaultComponents(false);
			fIndentStyleCombo.setEnabled(false);
			fForceSplit.setEnabled(false);
		}

		private void enableDefaultComponents(boolean enabled) {
			fOptionsGroup.setEnabled(enabled);
			fWrappingStyleCombo.setEnabled(enabled);
			fWrappingStylePolicy.setEnabled(enabled);
		}

		public void restoreSelection() {
			int index;
			try {
				index = fDialogSettings.getInt(PREF_CATEGORY_INDEX);
			} catch (NumberFormatException ex) {
				index = -1;
			}
			if (index < 0 || index > fCategoriesList.size() - 1) {
				index = 1; // In order to select a category with preview initially
			}
			final Category category = (Category) fCategoriesList.get(index);
			fCategoriesViewer.setSelection(new StructuredSelection(
					new Category[] { category }));
		}

		public void doubleClick(DoubleClickEvent event) {
			final ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection) {
				final Category node = (Category) ((IStructuredSelection) selection)
						.getFirstElement();
				fCategoriesViewer.setExpandedState(node,
						!fCategoriesViewer.getExpandedState(node));
			}
		}
	}

	private class SelectionState {
		private List fElements = new ArrayList();
		private boolean fRequiresRelayout;

		public void refreshState(IStructuredSelection selection) {
			Map wrappingStyleMap = new HashMap();
			Map indentStyleMap = new HashMap();
			Map forceWrappingMap = new HashMap();
			fRequiresRelayout = false;
			showSpecificControls(false);
			fElements.clear();
			evaluateElements(selection.iterator());
			evaluateMaps(wrappingStyleMap, indentStyleMap, forceWrappingMap);
			setPreviewText(getPreviewText());
			refreshControls(wrappingStyleMap, indentStyleMap, forceWrappingMap);
		}

		public List getElements() {
			return fElements;
		}

		private void evaluateElements(Iterator iterator) {
			Category category;
			String value;
			while (iterator.hasNext()) {
				category = (Category) iterator.next();
				value = (String) fWorkingValues.get(category.key);
				if (value != null) {
					if (!fElements.contains(category))
						fElements.add(category);
				} else {
					evaluateElements(category.children.iterator());
				}
			}
		}

		private void evaluateMaps(Map wrappingStyleMap, Map indentStyleMap,
				Map forceWrappingMap) {
			Iterator iterator = fElements.iterator();
			while (iterator.hasNext()) {
				insertIntoMap(wrappingStyleMap, indentStyleMap,
						forceWrappingMap, (Category) iterator.next());
			}
		}

		private String getPreviewText() {
			Iterator iterator = fElements.iterator();
			String previewText = ""; //$NON-NLS-1$
			while (iterator.hasNext()) {
				Category category = (Category) iterator.next();
				previewText = previewText + category.previewText + "\n\n"; //$NON-NLS-1$
			}
			return previewText;
		}

		private void insertIntoMap(Map wrappingMap, Map indentMap,
				Map forceMap, Category category) {
			final String value = (String) fWorkingValues.get(category.key);
			Integer wrappingStyle;
			Integer indentStyle;
			Boolean forceWrapping;

			try {
				wrappingStyle = new Integer(
						CodeFormatterConstants.getWrappingStyle(value));
				indentStyle = new Integer(
						CodeFormatterConstants.getIndentStyle(value));
				forceWrapping = new Boolean(
						CodeFormatterConstants.getForceWrapping(value));
			} catch (IllegalArgumentException e) {
				forceWrapping = new Boolean(false);
				indentStyle = new Integer(CodeFormatterConstants.INDENT_DEFAULT);
				wrappingStyle = new Integer(
						CodeFormatterConstants.WRAP_NO_SPLIT);
			}

			increaseMapEntry(wrappingMap, wrappingStyle);
			increaseMapEntry(indentMap, indentStyle);
			increaseMapEntry(forceMap, forceWrapping);
		}

		private void increaseMapEntry(Map map, Object type) {
			Integer count = (Integer) map.get(type);
			if (count == null) // not in map yet -> count == 0
				map.put(type, new Integer(1));
			else
				map.put(type, new Integer(count.intValue() + 1));
		}

		private void refreshControls(Map wrappingStyleMap, Map indentStyleMap,
				Map forceWrappingMap) {
			updateCombos(wrappingStyleMap, indentStyleMap);
			updateButton(forceWrappingMap);
			Integer wrappingStyleMax = getWrappingStyleMax(wrappingStyleMap);
			boolean isInhomogeneous = (fElements.size() != ((Integer) wrappingStyleMap
					.get(wrappingStyleMax)).intValue());
			updateControlEnablement(isInhomogeneous,
					wrappingStyleMax.intValue());
			showSpecificControls(true);
			updateSpecificControlsEnablement(wrappingStyleMax.intValue());
			if (fRequiresRelayout) {
				fOptionsComposite.layout(true, true);
			}
			doUpdatePreview();
			notifyValuesModified();
		}

		private void showSpecificControls(boolean show) {
			if (fElements.size() != 1)
				return;

			Preference[] preferences = ((Category) fElements.get(0))
					.getSpecificPreferences();
			if (preferences.length == 0)
				return;

			fRequiresRelayout = true;
			for (int i = 0; i < preferences.length; i++) {
				Preference preference = preferences[i];
				Control control = preference.getControl();
				control.setVisible(show);
				((GridData) control.getLayoutData()).exclude = !show;
			}
		}

		private void updateSpecificControlsEnablement(int wrappingStyle) {
			if (fElements.size() != 1)
				return;

			Preference[] preferences = ((Category) fElements.get(0))
					.getSpecificPreferences();
			if (preferences.length == 0)
				return;

			boolean doSplit = wrappingStyle != CodeFormatterConstants.WRAP_NO_SPLIT;
			for (int i = 0; i < preferences.length; i++) {
				Preference preference = preferences[i];
				Control control = preference.getControl();
				control.setEnabled(doSplit);
			}
		}

		private Integer getWrappingStyleMax(Map wrappingStyleMap) {
			int maxCount = 0, maxStyle = 0;
			for (int i = 0; i < WRAPPING_NAMES.length; i++) {
				Integer count = (Integer) wrappingStyleMap.get(new Integer(i));
				if (count == null)
					continue;
				if (count.intValue() > maxCount) {
					maxCount = count.intValue();
					maxStyle = i;
				}
			}
			return new Integer(maxStyle);
		}

		private void updateButton(Map forceWrappingMap) {
			Integer nrOfTrue = (Integer) forceWrappingMap.get(Boolean.TRUE);
			Integer nrOfFalse = (Integer) forceWrappingMap.get(Boolean.FALSE);

			if (nrOfTrue == null || nrOfFalse == null)
				fForceSplit.setSelection(nrOfTrue != null);
			else
				fForceSplit.setSelection(nrOfTrue.intValue() > nrOfFalse
						.intValue());

			int max = getMax(nrOfTrue, nrOfFalse);
			String label = FormatterMessages.LineWrappingTabPage_force_split_checkbox_text;
			fForceSplit.setText(getLabelText(label, max, fElements.size()));
		}

		private String getLabelText(String label, int count, int nElements) {
			if (nElements == 1 || count == 0)
				return label;
			return Messages.format(
					FormatterMessages.LineWrappingTabPage_occurences,
					new String[] { label, Integer.toString(count),
							Integer.toString(nElements) });
		}

		private int getMax(Integer nrOfTrue, Integer nrOfFalse) {
			if (nrOfTrue == null)
				return nrOfFalse.intValue();
			if (nrOfFalse == null)
				return nrOfTrue.intValue();
			if (nrOfTrue.compareTo(nrOfFalse) >= 0)
				return nrOfTrue.intValue();
			return nrOfFalse.intValue();
		}

		private void updateCombos(Map wrappingStyleMap, Map indentStyleMap) {
			updateCombo2(fWrappingStyleCombo, wrappingStyleMap,
					WRAPPING_NAMES_REDUCED, WRAPPING_CODES_REDUCED);
			updateCombo(fIndentStyleCombo, indentStyleMap, INDENT_NAMES);
		}

		private void updateCombo(Combo combo, Map map, final String[] items) {
			String[] newItems = new String[items.length];
			int maxCount = 0, maxStyle = 0;

			for (int i = 0; i < items.length; i++) {
				Integer count = (Integer) map.get(new Integer(i));
				int val = (count == null) ? 0 : count.intValue();
				if (val > maxCount) {
					maxCount = val;
					maxStyle = i;
				}
				newItems[i] = getLabelText(items[i], val, fElements.size());
			}
			combo.setItems(newItems);
			combo.setText(newItems[maxStyle]);
		}

		private void updateCombo2(Combo combo, Map map, final String[] items,
				final int[] codes) {
			String[] newItems = new String[items.length];
			int maxCount = 0, maxStyle = 0;

			for (int i = 0; i < items.length; i++) {
				Integer count = (Integer) map.get(new Integer(codes[i]));
				int val = (count == null) ? 0 : count.intValue();
				if (val > maxCount) {
					maxCount = val;
					maxStyle = i;
				}
				newItems[i] = getLabelText(items[i], val, fElements.size());
			}
			combo.setItems(newItems);
			combo.setText(newItems[maxStyle]);
		}
	}

	protected static final String[] INDENT_NAMES = {
			FormatterMessages.LineWrappingTabPage_indentation_default,
			FormatterMessages.LineWrappingTabPage_indentation_on_column,
			FormatterMessages.LineWrappingTabPage_indentation_by_one };

	protected static final String[] WRAPPING_NAMES = {
			FormatterMessages.LineWrappingTabPage_splitting_do_not_split,
			FormatterMessages.LineWrappingTabPage_splitting_wrap_when_necessary, // COMPACT_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_always_wrap_first_others_when_necessary, // COMPACT_FIRST_BREAK_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always, // ONE_PER_LINE_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always_indent_all_but_first, // NEXT_SHIFTED_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always_except_first_only_if_necessary };

	protected static final String[] WRAPPING_NAMES_REDUCED = {
			FormatterMessages.LineWrappingTabPage_splitting_do_not_split,
			FormatterMessages.LineWrappingTabPage_splitting_wrap_when_necessary, // COMPACT_SPLIT
			// FormatterMessages.LineWrappingTabPage_splitting_always_wrap_first_others_when_necessary, // COMPACT_FIRST_BREAK_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always, // ONE_PER_LINE_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always_indent_all_but_first, // NEXT_SHIFTED_SPLIT
			FormatterMessages.LineWrappingTabPage_splitting_wrap_always_except_first_only_if_necessary // 
	};

	protected static final int[] WRAPPING_CODES_REDUCED = {
			CodeFormatterConstants.WRAP_NO_SPLIT, // 0
			CodeFormatterConstants.WRAP_COMPACT, // COMPACT_SPLIT
			// CodeFormatterConstants.WRAP_COMPACT_FIRST_BREAK, // COMPACT_FIRST_BREAK_SPLIT
			CodeFormatterConstants.WRAP_ONE_PER_LINE, // ONE_PER_LINE_SPLIT
			CodeFormatterConstants.WRAP_NEXT_SHIFTED, // NEXT_SHIFTED_SPLIT
			CodeFormatterConstants.WRAP_NEXT_PER_LINE //
	};

	private final Category fCompactIfCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_COMPACT_IF,
			"class Example {" + //$NON-NLS-1$
					"function foo($argument) {" + //$NON-NLS-1$
					"  if ($argument==0) return 0;" + //$NON-NLS-1$
					"  if ($argument==1) return 42; else return 43;" + //$NON-NLS-1$
					"}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_compact_if_else);

	private final Category fTypeDeclarationSuperclassCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SUPERCLASS_IN_TYPE_DECLARATION,
			"class Example extends OtherClass {}\n" + //$NON-NLS-1$
					"interface IExample extends IOtherInterface {}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_extends_clause);

	private final Category fTypeDeclarationSuperinterfacesCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SUPERINTERFACES_IN_TYPE_DECLARATION,
			"class Example implements I1, I2, I3 {}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_implements_clause);

	private final Category fConstructorDeclarationsParametersCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_CONSTRUCTOR_DECLARATION,
			"class Example {function __construct($arg1, $arg2, $arg3, $arg4, $arg5, $arg6) { $this();}" + //$NON-NLS-1$
					"function Example() {}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_parameters);

	private final Category fMethodDeclarationsCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_METHOD_DECLARATION,
			"class Example {public final function a_method_with_a_long_name() {}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_declaration);

	private final Category fMethodDeclarationsParametersCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_PARAMETERS_IN_METHOD_DECLARATION,
			"class Example {function foo($arg1, $arg2, $arg3, $arg4, $arg5, $arg6) {}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_parameters);

	private final Category fMessageSendArgumentsCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_METHOD_INVOCATION,
			"class Example {function foo() {Other::bar( 100,\nnested(200,\n300,\n400,\n500,\n600,\n700,\n800,\n900 ));}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_arguments);

	private final Category fMessageSendSelectorCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SELECTOR_IN_METHOD_INVOCATION,
			"class Example {function foo(Some $a) {return $a->getFirst()->getSecond()->getThird()->getLast();}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_qualified_invocations);

	private final Category fMethodThrowsClauseCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_METHOD_DECLARATION,
			"class Example {" + //$NON-NLS-1$
					"int foo() throws FirstException, SecondException, ThirdException {"
					+ //$NON-NLS-1$
					"  return Other.doSomething();}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_throws_clause);

	private final Category fConstructorThrowsClauseCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_THROWS_CLAUSE_IN_CONSTRUCTOR_DECLARATION,
			"class Example {" + //$NON-NLS-1$
					"Example() throws FirstException, SecondException, ThirdException {"
					+ //$NON-NLS-1$
					"  return Other.doSomething();}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_throws_clause);

	private final Category fAllocationExpressionArgumentsCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_ALLOCATION_EXPRESSION,
			"class Example {function foo() {return new SomeClass(100,\n200,\n300,\n400,\n500,\n600,\n700,\n800,\n900 );}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_object_allocation);

	private final Category fQualifiedAllocationExpressionCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_QUALIFIED_ALLOCATION_EXPRESSION,
			"class Example {SomeClass foo() {return SomeOtherClass.new SomeClass(100,\n200,\n300,\n400,\n500 );}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_qualified_object_allocation);

	private final Category fArrayInitializerExpressionsCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_EXPRESSIONS_IN_ARRAY_INITIALIZER,
			"class Example {var $fArray= array(1,\n2,\n3,\n4,\n5,\n6,\n7,\n8,\n9,\n10,\n11,\n12);}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_array_init);

	private final Category fExplicitConstructorArgumentsCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARGUMENTS_IN_EXPLICIT_CONSTRUCTOR_CALL,
			"class Example extends AnotherClass {Example() {super(100,\n200,\n300,\n400,\n500,\n600,\n700);}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_explicit_constructor_invocations);

	private final Category fConditionalExpressionCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_CONDITIONAL_EXPRESSION,
			"class Example extends AnotherClass {function Example($argument) {return $argument ? 100000 : 200000;}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_conditionals);

	private final Category fBinaryExpressionCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_BINARY_EXPRESSION,
			"class Example extends AnotherClass {" + //$NON-NLS-1$
					"function foo() {"
					+ //$NON-NLS-1$
					"  $sum= 100\n + 200\n + 300\n + 400\n + 500\n + 600\n + 700\n + 800;"
					+ //$NON-NLS-1$
					"  $product= 1\n * 2\n * 3\n * 4\n * 5\n * 6\n * 7\n * 8\n * 9\n * 10;"
					+ //$NON-NLS-1$
					"  $val= true && false && true && false && true;" + //$NON-NLS-1$
					"  return $product / $sum;}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_binary_exprs);

	// since 1.2
	private final Category fConcatExpressionCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_CONCAT_EXPRESSION,
			"class Example extends AnotherClass {" + //$NON-NLS-1$
					"function foo() {" + //$NON-NLS-1$
					"  $ef=$mn.\"\\r\".$nm;" + //$NON-NLS-1$
					"  return $ef.\"\\n\";}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_concat_exprs);

	private final Category fAssignmentCategory = new Category(
			CodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ASSIGNMENT,
			"class Example {" + //$NON-NLS-1$
					"private static final $string = \"TextTextText\";" + //$NON-NLS-1$
					"function foo() {" + //$NON-NLS-1$
					"for ($i = 0; $i < 10; $i++) {}" + //$NON-NLS-1$
					"$s = \"Text Text Text...\";}}", //$NON-NLS-1$
			FormatterMessages.LineWrappingTabPage_assignment_alignment);

	/**
	 * The default preview line width.
	 */
	private static int DEFAULT_PREVIEW_WINDOW_LINE_WIDTH = 40;

	/**
	 * The key to save the user's preview window width in the dialog settings.
	 */
	private static final String PREF_PREVIEW_LINE_WIDTH = PEXUIPlugin.PLUGIN_ID
			+ ".codeformatter.line_wrapping_tab_page.preview_line_width"; //$NON-NLS-1$

	/**
	 * The dialog settings.
	 */
	protected final IDialogSettings fDialogSettings;

	protected TreeViewer fCategoriesViewer;
	protected Label fWrappingStylePolicy;
	protected Combo fWrappingStyleCombo;
	protected Label fIndentStylePolicy;
	protected Combo fIndentStyleCombo;
	protected Button fForceSplit;

	protected PHPSourcePreview fPreview;

	protected Group fOptionsGroup;

	/**
	 * A collection containing the categories tree. This is used as model for the tree viewer.
	 * @see TreeViewer
	 */
	private final List fCategories;

	/**
	 * The category listener which makes the selection persistent.
	 */
	protected final CategoryListener fCategoryListener;

	/**
	 * The current selection of elements.
	 */
	protected IStructuredSelection fSelection;

	/**
	 * An object containing the state for the UI.
	 */
	SelectionState fSelectionState;

	/**
	 * A special options store wherein the preview line width is kept.
	 */
	protected final Map fPreviewPreferences;

	/**
	 * The key for the preview line width.
	 */
	private final String LINE_SPLIT = CodeFormatterConstants.FORMATTER_LINE_SPLIT;

	private Composite fOptionsComposite;

	/**
	 * Create a new line wrapping tab page.
	 * 
	 * @param modifyDialog the modify dialog
	 * @param workingValues the values
	 */
	public LineWrappingTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);

		fDialogSettings = PEXUIPlugin.getDefault().getDialogSettings();

		final String previewLineWidth = fDialogSettings
				.get(PREF_PREVIEW_LINE_WIDTH);

		fPreviewPreferences = new HashMap();
		fPreviewPreferences.put(
				LINE_SPLIT,
				previewLineWidth != null ? previewLineWidth : Integer
						.toString(DEFAULT_PREVIEW_WINDOW_LINE_WIDTH));

		fCategories = createCategories();
		fCategoryListener = new CategoryListener(fCategories);
	}

	/**
	 * @return Create the categories tree.
	 */
	protected List createCategories() {

		final Category classDeclarations = new Category(
				FormatterMessages.LineWrappingTabPage_class_decls);
		classDeclarations.children.add(fTypeDeclarationSuperclassCategory);
		classDeclarations.children.add(fTypeDeclarationSuperinterfacesCategory);

		//		final Category constructorDeclarations = new Category(null, null,
		//				FormatterMessages.LineWrappingTabPage_constructor_decls);
		//		constructorDeclarations.children
		//				.add(fConstructorDeclarationsParametersCategory);
		//		constructorDeclarations.children.add(fConstructorThrowsClauseCategory);

		final Category methodDeclarations = new Category(null, null,
				FormatterMessages.LineWrappingTabPage_method_decls);
		methodDeclarations.children.add(fMethodDeclarationsCategory);
		methodDeclarations.children.add(fMethodDeclarationsParametersCategory);
		//		methodDeclarations.children.add(fMethodThrowsClauseCategory);

		final Category functionCalls = new Category(
				FormatterMessages.LineWrappingTabPage_function_calls);
		functionCalls.children.add(fMessageSendArgumentsCategory);
		functionCalls.children.add(fMessageSendSelectorCategory);
		//		functionCalls.children.add(fExplicitConstructorArgumentsCategory);
		functionCalls.children.add(fAllocationExpressionArgumentsCategory);
		//		functionCalls.children.add(fQualifiedAllocationExpressionCategory);

		final Category expressions = new Category(
				FormatterMessages.LineWrappingTabPage_expressions);
		expressions.children.add(fBinaryExpressionCategory);
		expressions.children.add(fConcatExpressionCategory);
		expressions.children.add(fConditionalExpressionCategory);
		expressions.children.add(fArrayInitializerExpressionsCategory);
		expressions.children.add(fAssignmentCategory);

		final Category statements = new Category(
				FormatterMessages.LineWrappingTabPage_statements);
		statements.children.add(fCompactIfCategory);

		final List root = new ArrayList();
		root.add(classDeclarations);
		//		root.add(constructorDeclarations);
		root.add(methodDeclarations);
		root.add(functionCalls);
		root.add(expressions);
		//		root.add(statements);

		return root;
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {

		fOptionsComposite = composite;

		final Group lineWidthGroup = createGroup(numColumns, composite,
				FormatterMessages.LineWrappingTabPage_general_settings);

		createNumberPref(
				lineWidthGroup,
				numColumns,
				FormatterMessages.LineWrappingTabPage_width_indent_option_max_line_width,
				CodeFormatterConstants.FORMATTER_LINE_SPLIT, 0, 9999);
		createNumberPref(
				lineWidthGroup,
				numColumns,
				FormatterMessages.LineWrappingTabPage_width_indent_option_default_indent_wrapped,
				CodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, 0,
				9999);
		createNumberPref(
				lineWidthGroup,
				numColumns,
				FormatterMessages.LineWrappingTabPage_width_indent_option_default_indent_array,
				CodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION_FOR_ARRAY_INITIALIZER,
				0, 9999);
		//		createCheckboxPref(lineWidthGroup, numColumns,
		//				FormatterMessages.LineWrappingTabPage_do_not_join_lines,
		//				CodeFormatterConstants.FORMATTER_JOIN_WRAPPED_LINES, TRUE_FALSE);
		//		createCheckboxPref(
		//				lineWidthGroup,
		//				numColumns,
		//				FormatterMessages.LineWrappingTabPage_wrap_outer_expressions_when_nested,
		//				CodeFormatterConstants.FORMATTER_WRAP_OUTER_EXPRESSIONS_WHEN_NESTED,
		//				FALSE_TRUE);

		fCategoriesViewer = new TreeViewer(composite /*categoryGroup*/,
				SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		fCategoriesViewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((Collection) inputElement).toArray();
			}

			public Object[] getChildren(Object parentElement) {
				return ((Category) parentElement).children.toArray();
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return !((Category) element).children.isEmpty();
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public void dispose() {
			}
		});
		fCategoriesViewer.setLabelProvider(new LabelProvider());
		fCategoriesViewer.setInput(fCategories);

		fCategoriesViewer.setExpandedElements(fCategories.toArray());

		final GridData gd = createGridData(numColumns, GridData.FILL_BOTH,
				SWT.DEFAULT);
		gd.heightHint = fPixelConverter.convertHeightInCharsToPixels(5);
		fCategoriesViewer.getControl().setLayoutData(gd);

		fOptionsGroup = createGroup(numColumns, composite, ""); //$NON-NLS-1$

		// label "Select split style:"
		fWrappingStylePolicy = createLabel(
				numColumns,
				fOptionsGroup,
				FormatterMessages.LineWrappingTabPage_wrapping_policy_label_text);

		// combo SplitStyleCombo
		fWrappingStyleCombo = new Combo(fOptionsGroup, SWT.SINGLE
				| SWT.READ_ONLY);
		SWTUtil.setDefaultVisibleItemCount(fWrappingStyleCombo);
		fWrappingStyleCombo.setItems(WRAPPING_NAMES_REDUCED);
		fWrappingStyleCombo.setLayoutData(createGridData(numColumns,
				GridData.HORIZONTAL_ALIGN_FILL, 0));

		// button "Force split"
		fForceSplit = new Button(fOptionsGroup, SWT.CHECK);
		String label = FormatterMessages.LineWrappingTabPage_force_split_checkbox_text;
		fForceSplit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, numColumns - 1, 1));
		fForceSplit.setText(label);

		fForceSplit.setVisible(true);

		// button "Wrap before operator"
		Preference expressionWrapPositionPreference = createCheckboxPref(
				fOptionsGroup,
				1,
				FormatterMessages.LineWrappingTabPage_binary_expression_wrap_operator,
				CodeFormatterConstants.FORMATTER_WRAP_BEFORE_BINARY_OPERATOR,
				FALSE_TRUE);
		Control control = expressionWrapPositionPreference.getControl();
		control.setVisible(false);
		GridData layoutData = (GridData) control.getLayoutData();
		layoutData.exclude = true;
		layoutData.horizontalAlignment = SWT.BEGINNING;
		layoutData.horizontalSpan = numColumns - 1;
		layoutData.grabExcessHorizontalSpace = false;
		fBinaryExpressionCategory
				.addPreference(expressionWrapPositionPreference);

		// since 1.2
		// button "Wrap before concat operator"
		Preference concatWrapPositionPreference = createCheckboxPref(
				fOptionsGroup,
				1,
				FormatterMessages.LineWrappingTabPage_concat_expression_wrap_operator,
				CodeFormatterConstants.FORMATTER_WRAP_BEFORE_CONCAT_OPERATOR,
				FALSE_TRUE);
		Control control3 = concatWrapPositionPreference.getControl();
		control3.setVisible(false);
		GridData layoutData3 = (GridData) control3.getLayoutData();
		layoutData3.exclude = true;
		layoutData3.horizontalAlignment = SWT.BEGINNING;
		layoutData3.horizontalSpan = numColumns - 1;
		layoutData3.grabExcessHorizontalSpace = false;
		fConcatExpressionCategory.addPreference(concatWrapPositionPreference);

		// button "Wrap array in arguments"
		Preference arrayWrapPositionPreference = createCheckboxPref(
				fOptionsGroup, 1,
				FormatterMessages.LineWrappingTabPage_wrap_array_in_arguments,
				CodeFormatterConstants.FORMATTER_WRAP_ARRAY_IN_ARGUMENTS,
				FALSE_TRUE);
		Control control2 = arrayWrapPositionPreference.getControl();
		control2.setVisible(false);
		GridData layoutData2 = (GridData) control2.getLayoutData();
		layoutData2.exclude = true;
		layoutData2.horizontalAlignment = SWT.BEGINNING;
		layoutData2.horizontalSpan = numColumns - 1;
		layoutData2.grabExcessHorizontalSpace = false;
		fArrayInitializerExpressionsCategory
				.addPreference(arrayWrapPositionPreference);

		// label "Select indentation style:"
		fIndentStylePolicy = createLabel(
				numColumns,
				fOptionsGroup,
				FormatterMessages.LineWrappingTabPage_indentation_policy_label_text);

		// combo IndentStyleCombo
		fIndentStyleCombo = new Combo(fOptionsGroup, SWT.SINGLE | SWT.READ_ONLY);
		SWTUtil.setDefaultVisibleItemCount(fIndentStyleCombo);
		fIndentStyleCombo.setItems(INDENT_NAMES);
		fIndentStyleCombo.setLayoutData(createGridData(numColumns,
				GridData.HORIZONTAL_ALIGN_FILL, 0));

		// selection state object
		fSelectionState = new SelectionState();

	}

	protected Composite doCreatePreviewPane(Composite composite, int numColumns) {

		super.doCreatePreviewPane(composite, numColumns);

		Composite previewLineWidthContainer = new Composite(composite, SWT.NONE);
		previewLineWidthContainer.setLayout(createGridLayout(2, false));

		final NumberPreference previewLineWidth = new NumberPreference(
				previewLineWidthContainer,
				2,
				fPreviewPreferences,
				LINE_SPLIT,
				0,
				9999,
				FormatterMessages.LineWrappingTabPage_line_width_for_preview_label_text);
		fDefaultFocusManager.add(previewLineWidth);
		previewLineWidth.addObserver(fUpdater);
		previewLineWidth.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				fDialogSettings.put(PREF_PREVIEW_LINE_WIDTH,
						(String) fPreviewPreferences.get(LINE_SPLIT));
			}
		});

		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected PHPPreview doCreatePHPPreview(Composite parent) {
		fPreview = new PHPSourcePreview(fWorkingValues, parent);
		return fPreview;
	}

	protected void initializePage() {

		fCategoriesViewer.addSelectionChangedListener(fCategoryListener);
		fCategoriesViewer.addDoubleClickListener(fCategoryListener);

		fForceSplit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				forceSplitChanged(fForceSplit.getSelection());
			}
		});
		fIndentStyleCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				indentStyleChanged(((Combo) e.widget).getSelectionIndex());
			}
		});
		fWrappingStyleCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = ((Combo) e.widget).getSelectionIndex();
				wrappingStyleChanged(WRAPPING_CODES_REDUCED[index]);
			}
		});

		fCategoryListener.restoreSelection();

		fDefaultFocusManager.add(fCategoriesViewer.getControl());
		fDefaultFocusManager.add(fWrappingStyleCombo);
		fDefaultFocusManager.add(fIndentStyleCombo);
		fDefaultFocusManager.add(fForceSplit);
	}

	protected void doUpdatePreview() {
		super.doUpdatePreview();
		final Object normalSetting = fWorkingValues.get(LINE_SPLIT);
		fWorkingValues.put(LINE_SPLIT, fPreviewPreferences.get(LINE_SPLIT));
		fPreview.update();
		fWorkingValues.put(LINE_SPLIT, normalSetting);
	}

	protected void setPreviewText(String text) {
		final Object normalSetting = fWorkingValues.get(LINE_SPLIT);
		fWorkingValues.put(LINE_SPLIT, fPreviewPreferences.get(LINE_SPLIT));
		fPreview.setPreviewText(text);
		fWorkingValues.put(LINE_SPLIT, normalSetting);
	}

	protected void forceSplitChanged(boolean forceSplit) {
		Iterator iterator = fSelectionState.fElements.iterator();
		String currentKey;
		while (iterator.hasNext()) {
			currentKey = ((Category) iterator.next()).key;
			try {
				changeForceSplit(currentKey, forceSplit);
			} catch (IllegalArgumentException e) {
				fWorkingValues.put(currentKey, CodeFormatterConstants
						.createAlignmentValue(forceSplit,
								CodeFormatterConstants.WRAP_NO_SPLIT,
								CodeFormatterConstants.INDENT_DEFAULT));
				PEXUIPlugin
						.log(new Status(
								IStatus.ERROR,
								PEXUIPlugin.PLUGIN_ID,
								IStatus.OK,
								Messages.format(
										FormatterMessages.LineWrappingTabPage_error_invalid_value,
										currentKey), e));
			}
		}
		fSelectionState.refreshState(fSelection);
	}

	private void changeForceSplit(String currentKey, boolean forceSplit)
			throws IllegalArgumentException {
		String value = (String) fWorkingValues.get(currentKey);
		value = CodeFormatterConstants.setForceWrapping(value, forceSplit);
		if (value == null)
			throw new IllegalArgumentException();
		fWorkingValues.put(currentKey, value);
	}

	protected void wrappingStyleChanged(int wrappingStyle) {
		Iterator iterator = fSelectionState.fElements.iterator();
		String currentKey;
		while (iterator.hasNext()) {
			currentKey = ((Category) iterator.next()).key;
			try {
				changeWrappingStyle(currentKey, wrappingStyle);
			} catch (IllegalArgumentException e) {
				fWorkingValues.put(currentKey, CodeFormatterConstants
						.createAlignmentValue(false, wrappingStyle,
								CodeFormatterConstants.INDENT_DEFAULT));
				PEXUIPlugin
						.log(new Status(
								IStatus.ERROR,
								PEXUIPlugin.PLUGIN_ID,
								IStatus.OK,
								Messages.format(
										FormatterMessages.LineWrappingTabPage_error_invalid_value,
										currentKey), e));
			}
		}
		fSelectionState.refreshState(fSelection);
	}

	private void changeWrappingStyle(String currentKey, int wrappingStyle)
			throws IllegalArgumentException {
		String value = (String) fWorkingValues.get(currentKey);
		value = CodeFormatterConstants.setWrappingStyle(value, wrappingStyle);
		if (value == null)
			throw new IllegalArgumentException();
		fWorkingValues.put(currentKey, value);
	}

	protected void indentStyleChanged(int indentStyle) {
		Iterator iterator = fSelectionState.fElements.iterator();
		String currentKey;
		while (iterator.hasNext()) {
			currentKey = ((Category) iterator.next()).key;
			try {
				changeIndentStyle(currentKey, indentStyle);
			} catch (IllegalArgumentException e) {
				fWorkingValues.put(currentKey, CodeFormatterConstants
						.createAlignmentValue(false,
								CodeFormatterConstants.WRAP_NO_SPLIT,
								indentStyle));
				PEXUIPlugin
						.log(new Status(
								IStatus.ERROR,
								PEXUIPlugin.PLUGIN_ID,
								IStatus.OK,
								Messages.format(
										FormatterMessages.LineWrappingTabPage_error_invalid_value,
										currentKey), e));
			}
		}
		fSelectionState.refreshState(fSelection);
	}

	private void changeIndentStyle(String currentKey, int indentStyle)
			throws IllegalArgumentException {
		String value = (String) fWorkingValues.get(currentKey);
		value = CodeFormatterConstants.setIndentStyle(value, indentStyle);
		if (value == null)
			throw new IllegalArgumentException();
		fWorkingValues.put(currentKey, value);
	}

	protected void updateControlEnablement(boolean inhomogenous,
			int wrappingStyle) {
		boolean doSplit = wrappingStyle != CodeFormatterConstants.WRAP_NO_SPLIT;
		fIndentStylePolicy.setEnabled(true);
		fIndentStyleCombo.setEnabled(inhomogenous || doSplit);
		fForceSplit.setEnabled(inhomogenous || doSplit);
	}
}
