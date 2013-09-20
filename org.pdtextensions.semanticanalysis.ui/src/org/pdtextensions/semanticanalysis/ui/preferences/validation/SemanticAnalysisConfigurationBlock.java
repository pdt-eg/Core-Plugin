package org.pdtextensions.semanticanalysis.ui.preferences.validation;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.pdtextensions.core.ui.preferences.AbstractOptionsConfigurationBlock;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;

@SuppressWarnings("restriction")
public class SemanticAnalysisConfigurationBlock extends AbstractOptionsConfigurationBlock {

	@Inject
	private IValidatorManager manager;

	private Composite fieldEditorParent;
	private FieldEditor[] fields;
	private Composite[] fieldParents;
	private BooleanFieldEditor analysisEnabled;
	private IPreferenceStore prefStore;
	private PixelConverter pixelConverter;

	protected final static Key getSKey(String key) {
		return getKey(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID, key);
	}

	public SemanticAnalysisConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	private static Key[] getKeys() {
		Validator[] validators = PEXAnalysisPlugin.getDefault().getValidatorManager().getValidators();
		Key[] keys = new Key[validators.length];
		for (int i = 0; i < validators.length; i++) {
			keys[i] = getSKey(validators[i].getId());
		}
		return keys;
	}

	@Override
	public Control createBlockContents(Composite parent) {
		if (pixelConverter == null) {
			pixelConverter = new PixelConverter(parent);
			ContextInjectionFactory.inject(this, PEXAnalysisPlugin.getEclipseContext());
		}

		Composite content = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		content.setLayout(layout);

		createFields(content);
		initialize();
		updateFieldVisibility();

		return content;
	}

	protected String[][] getSeverityOptions() {
		return new String[][] {
			new String[] {"Error", ProblemSeverity.ERROR.toString()},
			new String[] {"Warning", ProblemSeverity.WARNING.toString()},
			new String[] {"Info", ProblemSeverity.INFO.toString()},
			new String[] {"Ignore", ProblemSeverity.IGNORE.toString()}
		};
	}

	private Composite createFields(Composite parent) {

		fieldEditorParent = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		fieldEditorParent.setLayout(layout);
		fieldEditorParent.setFont(parent.getFont());

		analysisEnabled = new BooleanFieldEditor(PreferenceConstants.ENABLED, "Enable semantic analysis", fieldEditorParent);
		analysisEnabled.setPreferenceStore(getPreferenceStore());
		analysisEnabled
				.setPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						updateFieldVisibility();
					}
				});
		analysisEnabled.fillIntoGrid(fieldEditorParent, 2);

		final Label horizontalLine = new Label(fieldEditorParent, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, 2, 1));
		horizontalLine.setFont(fieldEditorParent.getFont());

		fields = new FieldEditor[manager.getValidators().length];
		fieldParents = new Composite[fields.length];
		int i = 0;
		boolean first = true;
		for (Category category : manager.getCategories()) {
			ExpandableComposite group = createGroup(1, fieldEditorParent, category.getLabel());
			group.setExpanded(first);
			first = false;
			Composite inner = new Composite(group, SWT.NONE);
			inner.setFont(parent.getFont());
			inner.setLayout(new GridLayout(1, true));
			inner.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
			group.setClient(inner);

			for (Validator v : category.getValidators()) {
				createCombo(i, v, inner);
			}
		}

		return fieldEditorParent;
	}

	private void createCombo(int pos, Validator v, Composite parent) {
		ComboFieldEditor field = new ComboFieldEditor(v.getId(), v.getLabel(), getSeverityOptions(), parent);
		field.setPreferenceStore(getPreferenceStore());
		fields[pos] = field;
		fieldParents[pos++] = parent;
	}

	protected ExpandableComposite createGroup(int numColumns, Composite parent, String label) {
		ExpandableComposite excomposite= new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, numColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});

		return excomposite;
	}

	protected static GridData createGridData(int numColumns, int style,
			int widthHint) {
		final GridData gd = new GridData(style);
		gd.horizontalSpan = numColumns;
		gd.widthHint = widthHint;
		return gd;
	}

	protected IPreferenceStore getPreferenceStore() {
		if (prefStore == null) {
			prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		}
		return prefStore;
	}

	protected void updateFieldVisibility() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].setEnabled(analysisEnabled.getBooleanValue(), fieldParents[i]);
		}
	}

	protected void initialize() {
		if (fields != null) {
			for (FieldEditor f : fields) {
				f.setPreferenceStore(getPreferenceStore());
				f.load();
			}
		}

		analysisEnabled.load();

	}

	public void performDefaults() {
		if (fields != null) {
			for (FieldEditor f : fields) {
				f.loadDefault();
			}
		}

		analysisEnabled.loadDefault();
		updateFieldVisibility();
		super.performDefaults();
	}

	public boolean performOk() {
		if (fields != null) {
			for (FieldEditor f : fields) {
				f.store();
			}
		}
		analysisEnabled.store();

		return true;
	}
}
