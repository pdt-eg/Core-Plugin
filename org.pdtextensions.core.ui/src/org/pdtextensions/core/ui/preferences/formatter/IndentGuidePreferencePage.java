package org.pdtextensions.core.ui.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.pdtextensions.core.ui.PEXUIPlugin;

public class IndentGuidePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button enabled;
	private Composite attributes;
	private Spinner lineAlpha;
	private Combo lineStyle;
	private static final String[] styles = {
			PreferencesMessages.IndentGuidePreferencePage_style_solid,
			PreferencesMessages.IndentGuidePreferencePage_style_dash,
			PreferencesMessages.IndentGuidePreferencePage_style_dot,
			PreferencesMessages.IndentGuidePreferencePage_style_dash_dot,
			PreferencesMessages.IndentGuidePreferencePage_style_dash_dot_dot };
	private Spinner lineWidth;
	private Spinner lineShift;
	private ColorFieldEditor colorFieldEditor;
	private Composite drawing;
	private Button drawLeftEnd;
	private Button drawBlankLine;
	private Button skipCommentBlock;

	public IndentGuidePreferencePage() {
		setPreferenceStore(PEXUIPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.IndentGuidePreferencePage_description);
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));

		enabled = new Button(composite, SWT.CHECK);
		enabled.setText(PreferencesMessages.IndentGuidePreferencePage_enabled_label);
		enabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				enableControls(enabled.getSelection());
			}
		});

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group.setLayout(new GridLayout(1, true));
		group.setText(PreferencesMessages.IndentGuidePreferencePage_group_label);
		attributes = new Composite(group, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalIndent = 5;
		attributes.setLayoutData(gridData);
		attributes.setLayout(new GridLayout(2, false));

		new Label(attributes, SWT.NONE)
				.setText(PreferencesMessages.IndentGuidePreferencePage_alpha_label);
		lineAlpha = new Spinner(attributes, SWT.BORDER);
		lineAlpha.setMinimum(0);
		lineAlpha.setMaximum(255);
		new Label(attributes, SWT.NONE)
				.setText(PreferencesMessages.IndentGuidePreferencePage_style_label);
		lineStyle = new Combo(attributes, SWT.READ_ONLY);
		lineStyle.setItems(styles);
		new Label(attributes, SWT.NONE)
				.setText(PreferencesMessages.IndentGuidePreferencePage_width_label);
		lineWidth = new Spinner(attributes, SWT.BORDER);
		lineWidth.setMinimum(1);
		lineWidth.setMaximum(8);
		new Label(attributes, SWT.NONE)
				.setText(PreferencesMessages.IndentGuidePreferencePage_shift_label);
		lineShift = new Spinner(attributes, SWT.BORDER);
		lineShift.setMinimum(0);
		lineShift.setMaximum(8);
		colorFieldEditor = new ColorFieldEditor(PreferenceConstants.LINE_COLOR,
				PreferencesMessages.IndentGuidePreferencePage_color_label, attributes);
		colorFieldEditor.setPreferenceStore(getPreferenceStore());

		Group group2 = new Group(composite, SWT.NONE);
		group2.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		group2.setLayout(new GridLayout(1, true));
		group2.setText(PreferencesMessages.IndentGuidePreferencePage_group2_label);
		drawing = new Composite(group2, SWT.NONE);
		drawing.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		drawing.setLayout(new GridLayout(1, false));

		drawLeftEnd = new Button(drawing, SWT.CHECK);
		drawLeftEnd
				.setText(PreferencesMessages.IndentGuidePreferencePage_draw_left_end_label);
		drawBlankLine = new Button(drawing, SWT.CHECK);
		drawBlankLine
				.setText(PreferencesMessages.IndentGuidePreferencePage_draw_blank_line_label);
		skipCommentBlock = new Button(drawing, SWT.CHECK);
		skipCommentBlock
				.setText(PreferencesMessages.IndentGuidePreferencePage_skip_comment_block_label);

		loadPreferences();
		return composite;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		IPreferenceStore store = getPreferenceStore();
		enabled.setSelection(store
				.getDefaultBoolean(PreferenceConstants.ENABLED));
		lineAlpha.setSelection(store
				.getDefaultInt(PreferenceConstants.LINE_ALPHA));
		int index = store.getDefaultInt(PreferenceConstants.LINE_STYLE) - 1;
		if (index < 0 || index >= styles.length) {
			index = 0;
		}
		lineStyle.setText(styles[index]);
		lineWidth.setSelection(store
				.getDefaultInt(PreferenceConstants.LINE_WIDTH));
		lineShift.setSelection(store
				.getDefaultInt(PreferenceConstants.LINE_SHIFT));
		colorFieldEditor.loadDefault();
		drawLeftEnd.setSelection(store
				.getDefaultBoolean(PreferenceConstants.DRAW_LEFT_END));
		drawBlankLine.setSelection(store
				.getDefaultBoolean(PreferenceConstants.DRAW_BLANK_LINE));
		skipCommentBlock.setSelection(store
				.getDefaultBoolean(PreferenceConstants.SKIP_COMMENT_BLOCK));
		enableControls(enabled.getSelection());
	}

	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		store.setValue(PreferenceConstants.ENABLED, enabled.getSelection());
		store.setValue(PreferenceConstants.LINE_ALPHA, lineAlpha.getSelection());
		store.setValue(PreferenceConstants.LINE_STYLE,
				lineStyle.getSelectionIndex() + 1);
		store.setValue(PreferenceConstants.LINE_WIDTH, lineWidth.getSelection());
		store.setValue(PreferenceConstants.LINE_SHIFT, lineShift.getSelection());
		colorFieldEditor.store();
		RGB rgb = colorFieldEditor.getColorSelector().getColorValue();
		Color color = new Color(PlatformUI.getWorkbench().getDisplay(), rgb);
		PEXUIPlugin.getDefault().setColor(color);
		store.setValue(PreferenceConstants.DRAW_LEFT_END,
				drawLeftEnd.getSelection());
		store.setValue(PreferenceConstants.DRAW_BLANK_LINE,
				drawBlankLine.getSelection());
		store.setValue(PreferenceConstants.SKIP_COMMENT_BLOCK,
				skipCommentBlock.getSelection());
		return super.performOk();
	}

	private void loadPreferences() {
		IPreferenceStore store = getPreferenceStore();
		enabled.setSelection(store.getBoolean(PreferenceConstants.ENABLED));
		lineAlpha.setSelection(store.getInt(PreferenceConstants.LINE_ALPHA));
		int index = store.getInt(PreferenceConstants.LINE_STYLE) - 1;
		if (index < 0 || index >= styles.length) {
			index = 0;
		}
		lineStyle.setText(styles[index]);
		lineWidth.setSelection(store.getInt(PreferenceConstants.LINE_WIDTH));
		lineShift.setSelection(store.getInt(PreferenceConstants.LINE_SHIFT));
		colorFieldEditor.load();
		drawLeftEnd.setSelection(store
				.getBoolean(PreferenceConstants.DRAW_LEFT_END));
		drawBlankLine.setSelection(store
				.getBoolean(PreferenceConstants.DRAW_BLANK_LINE));
		skipCommentBlock.setSelection(store
				.getBoolean(PreferenceConstants.SKIP_COMMENT_BLOCK));
		enableControls(enabled.getSelection());
	}

	private void enableControls(boolean enabled) {
		for (Control control : attributes.getChildren()) {
			control.setEnabled(enabled);
		}
		for (Control control : drawing.getChildren()) {
			control.setEnabled(enabled);
		}
	}

}
