package com.pex.ui.preferences.csfixer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IListAdapter;
import org.eclipse.php.internal.ui.wizards.fields.ListDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.pex.ui.preferences.AbstractOptionsConfigurationBlock;
import com.pex.ui.preferences.PEXPreferenceNames;

@SuppressWarnings("restriction")
public class PHPCSFixerConfigurationBlock extends
		AbstractOptionsConfigurationBlock {

	private static final Key PREF_PHPCS_PHAR_LOCATION = getPEXKey(PEXPreferenceNames.PREF_PHPCS_PHAR_LOCATION);
	private static final Key PREF_PHPCS_PHAR_NAME = getPEXKey(PEXPreferenceNames.PREF_PHPCS_PHAR_NAME);

	private static final Key PREF_PHPCS_CUSTOM_PHAR_LOCATIONS = getPEXKey(PEXPreferenceNames.PREF_PHPCS_CUSTOM_PHAR_LOCATIONS);
	private static final Key PREF_PHPCS_CUSTOM_PHAR_NAMES = getPEXKey(PEXPreferenceNames.PREF_PHPCS_CUSTOM_PHAR_NAMES);

	private static final Key PREF_PHPCS_USE_DEFAULT_FIXERS = getPEXKey(PEXPreferenceNames.PREF_PHPCS_USE_DEFAULT_FIXERS);

	private static final Key PREF_PHPCS_CONFIG = getPEXKey(PEXPreferenceNames.PREF_PHPCS_CONFIG);

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;
	private static final int IDX_DEFAULT = 4;

	private ListDialogField<FixerPhar> pharField;
	private CheckboxTableViewer fixerList;

	private List<FixerOption> fixerOptions = new ArrayList<FixerOption>();

	public PHPCSFixerConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {

		super(context, project, getKeys(), container);

		initPharList();
		initFixerOptions();
	}

	private void initFixerOptions() {

		unpackFixerOptions();
	}

	protected void initPharList() {

		PharListAdapter adapter = new PharListAdapter();
		PharLabelProvider provider = new PharLabelProvider();

		String[] buttons = new String[] { "New...", "Edit...", "Remove", null,
				"Default", null };

		pharField = new ListDialogField<PHPCSFixerConfigurationBlock.FixerPhar>(
				adapter, buttons, provider);
		pharField.setDialogFieldListener(adapter);
		pharField.setRemoveButtonIndex(IDX_REMOVE);

		String[] columnsHeaders = new String[] { "Name", "Path" };

		pharField.setTableColumns(new ListDialogField.ColumnsDescription(
				columnsHeaders, true));
		pharField.setViewerSorter(new ViewerSorter());

		unpackPhars();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHPCS_PHAR_LOCATION, PREF_PHPCS_PHAR_NAME,
				PREF_PHPCS_CUSTOM_PHAR_LOCATIONS, PREF_PHPCS_CUSTOM_PHAR_NAMES,
				PREF_PHPCS_USE_DEFAULT_FIXERS };
	}

	@Override
	public Control createBlockContents(Composite parent) {

		Composite content = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		content.setLayout(layout);

		createFixerOptions(content);
		createPharOptions(content);

		return content;
	}

	protected void createPharOptions(Composite parent) {

		Composite pharContent = new Composite(parent, SWT.NONE);
		
//		pharContent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));

		PixelConverter conv = new PixelConverter(pharContent);

		GridLayout pharListLayout = new GridLayout();
		pharListLayout.marginHeight = 2;
		pharListLayout.marginWidth = 0;
		pharListLayout.numColumns = 3;
		
		pharContent.setLayout(pharListLayout);
		
		Label pharLabel = new Label(pharContent, SWT.NONE);
		pharLabel.setText("Phar used for running the cs-fixer");
		GridData labelData = new GridData(GridData.VERTICAL_ALIGN_END);
		labelData.horizontalSpan = 3;
		pharLabel.setLayoutData(labelData);

		GridData listData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		listData.widthHint = conv.convertWidthInCharsToPixels(50);
		listData.heightHint = conv.convertHeightInCharsToPixels(12);
		Control listControl = pharField.getListControl(pharContent);
		listControl.setLayoutData(listData);

		Control buttonsControl = pharField.getButtonBox(pharContent);
		buttonsControl.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_BEGINNING));

	}

	protected void createFixerOptions(Composite parent) {

		Composite fixerContent = new Composite(parent, SWT.NONE);
		GridLayout fixerLayout = new GridLayout();
		fixerLayout.marginHeight = 10;
		fixerLayout.marginWidth = 0;
		fixerContent.setLayout(fixerLayout);

		Group fixerGroup = new Group(fixerContent, SWT.NONE);
		fixerGroup.setLayout(new GridLayout());
		fixerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fixerGroup.setText("CodingStandard Fixer Configuration");

		addComboBox(fixerGroup, "Configuration", PREF_PHPCS_CONFIG,
				PEXPreferenceNames.getPHPCSFixerConfig(),
				PEXPreferenceNames.getPHPCSFixerConfigLabels());
		
		final Button defaultFixers = addCheckBox(fixerGroup,
				"Use default PHP-CS fixer options",
				PREF_PHPCS_USE_DEFAULT_FIXERS, new String[] { "yes", "no" }, 0);

		defaultFixers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				fixerList.getTable().setEnabled(
						defaultFixers.getSelection() == false);
			}
		});

		fixerList = CheckboxTableViewer.newCheckList(fixerGroup, SWT.BORDER);
		fixerList.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		fixerList.setContentProvider(new ArrayContentProvider());
		fixerList.setLabelProvider(new FixerOptionLabelProvider());

		fixerList.setCheckStateProvider(new ICheckStateProvider() {

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				FixerOption option = (FixerOption) element;
				return "true".equals(option.active);
			}
		});

		fixerList.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				FixerOption option = (FixerOption) event.getElement();
				Boolean bool = new Boolean(event.getChecked());
				option.active = bool.toString();
				setValue(getPEXKey(option.internal), packOption(option));
			}
		});

		fixerList.setInput(fixerOptions.toArray());

		if ("yes".equals(getValue(PREF_PHPCS_USE_DEFAULT_FIXERS))) {
			fixerList.getTable().setEnabled(false);
		}


	}

	private class PharListAdapter implements IListAdapter<Object>,
			IDialogFieldListener {

		@Override
		public void customButtonPressed(ListDialogField<Object> field, int index) {
			doStandardButtonPressed(index);
		}

		private boolean canEdit(List<Object> selectedElements) {
			return selectedElements.size() == 1
					&& !isInternalPhar((FixerPhar) selectedElements.get(0));
		}

		private boolean canSetToDefault(List<Object> selectedElements) {
			return selectedElements.size() == 1
					&& !isDefaultPhar((FixerPhar) selectedElements.get(0));
		}

		private boolean canRemove(List<Object> selectedElements) {
			int count = selectedElements.size();

			if (count == 0)
				return false;

			for (int i = 0; i < count; i++) {
				if (isInternalPhar((FixerPhar) selectedElements.get(i)))
					return false;
			}

			return true;
		}

		@Override
		public void selectionChanged(ListDialogField<Object> field) {
			List<Object> selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
			field.enableButton(IDX_DEFAULT, canSetToDefault(selectedElements));
			field.enableButton(IDX_REMOVE, canRemove(selectedElements));
		}

		@Override
		public void doubleClicked(ListDialogField<Object> field) {
			if (canEdit(field.getSelectedElements())) {
				doStandardButtonPressed(IDX_EDIT);
			}
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
			updatePharModel(field);
		}
	}

	final boolean isDefaultPhar(FixerPhar lib) {
		return pharField.getIndexOfElement(lib) == 0;
	}

	private class PharLabelProvider extends LabelProvider implements
			ITableLabelProvider, IFontProvider {

		@Override
		public Font getFont(Object element) {
			if (isDefaultPhar((FixerPhar) element)) {
				return JFaceResources.getFontRegistry().getBold(
						JFaceResources.DIALOG_FONT);
			}
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			FixerPhar lib = (FixerPhar) element;
			if (columnIndex == 0) {
				String name = lib.name;
				if (isDefaultPhar(lib)) {
					name = MessageFormat.format("{0} (default)",
							new Object[] { name });
				}
				return name;
			} else if (columnIndex == 1) {
				return lib.path;
			} else {
				return "";
			}
		}
	}

	public static class FixerPhar {

		public String name;
		public boolean custom;
		public String path;

		public String toString() {
			return name;
		}
	}

	final boolean isInternalPhar(FixerPhar phar) {
		return phar.path == "";
	}

	protected final void updatePharModel(DialogField field) {
		if (field == pharField) {
			StringBuffer customLibs = new StringBuffer();
			StringBuffer customPaths = new StringBuffer();

			List<FixerPhar> list = pharField.getElements();
			for (int i = 0; i < list.size(); i++) {
				FixerPhar elem = list.get(i);
				if (elem.custom) {
					if (customLibs.length() > 0) {
						customLibs.append(';');
						customPaths.append(';');
					}

					customLibs.append(elem.name);
					customPaths.append(elem.path);
				}
			}

			FixerPhar defaultLibrary = (FixerPhar) pharField.getElement(0);
			if (defaultLibrary != null) {
				setValue(PREF_PHPCS_PHAR_NAME, defaultLibrary.name);
				setValue(PREF_PHPCS_PHAR_LOCATION, defaultLibrary.path);
			}
			setValue(PREF_PHPCS_CUSTOM_PHAR_NAMES, customLibs.toString());
			setValue(PREF_PHPCS_CUSTOM_PHAR_LOCATIONS, customPaths.toString());

			// validateSettings(PREF_CUSTOM_LIBRARY_NAMES, null, null);
		}
	}

	private FixerOption unpackOption(String option) {

		String[] values = option.split("::");
		FixerOption opt = new FixerOption();
		opt.internal = values[0];
		opt.option = values[1];
		opt.name = values[2];
		opt.active = values[3];
		return opt;

	}

	private String packOption(FixerOption option) {

		return String.format("%s::%s::%s::%s", option.internal, option.option,
				option.name, option.active);
	}

	private void unpackFixerOptions() {

		for (String key : PEXPreferenceNames.getPHPCSFixerOptions()) {
			fixerOptions.add(unpackOption(getValue(getPEXKey(key))));
		}

	}

	private void unpackPhars() {
		String defaultName = getValue(PREF_PHPCS_PHAR_NAME);
		FixerPhar defaultLibrary = null;

		String customStandardPrefs = getValue(PREF_PHPCS_CUSTOM_PHAR_NAMES);

		String[] customStandards = {};
		String[] customPaths = {};

		if (customStandardPrefs != null) {
			customStandards = getTokens(customStandardPrefs, ";"); //$NON-NLS-1$

			String customPathPrefs = getValue(PREF_PHPCS_CUSTOM_PHAR_LOCATIONS);
			customPaths = getTokens(customPathPrefs, ";"); //$NON-NLS-1$
		}

		ArrayList<FixerPhar> elements = new ArrayList<FixerPhar>(
				customStandards.length + 1);

		// internal lib
		FixerPhar lib = new FixerPhar();
		lib.name = "<Internal>";
		lib.custom = false;
		lib.path = "";

		elements.add(lib);

		if (lib.name.equals(defaultName))
			defaultLibrary = lib;

		// Custom libs
		for (int i = 0; i < customStandards.length; i++) {
			lib = new FixerPhar();
			lib.name = customStandards[i].trim();
			lib.custom = true;
			lib.path = customPaths[i].trim();

			elements.add(lib);

			if (lib.name.equals(defaultName))
				defaultLibrary = lib;
		}

		pharField.setElements(elements);

		if (defaultLibrary != null)
			setToDefaultPhar(defaultLibrary);
	}

	private void setToDefaultPhar(FixerPhar lib) {
		List<FixerPhar> elements = pharField.getElements();
		elements.remove(lib);
		elements.add(0, lib);
		pharField.setElements(elements);
		pharField.enableButton(IDX_DEFAULT, false);
	}

	private void doStandardButtonPressed(int index) {
		FixerPhar edited = null;
		if (index != IDX_ADD) {
			edited = pharField.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			LibraryInputDialog dialog = new LibraryInputDialog(getShell(),
					edited, pharField.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					pharField.replaceElement(edited, dialog.getResult());
				} else {
					pharField.addElement(dialog.getResult());
				}
			}
		} else if (index == IDX_DEFAULT) {
			setToDefaultPhar(edited);
		}
	}

	class FixerOptionLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return ((FixerOption) element).option;
		}
	}

	static class FixerOption {

		public String name;
		public String internal;
		public String option;
		public String active;

		public String toString() {
			return option;
		}
	}
}
