package org.pdtextensions.core.ui.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.pdtextensions.core.CorePreferenceConstants;
import org.pdtextensions.core.PEXCorePlugin;

@SuppressWarnings("restriction")
public class SemanticAnalysisConfigurationBlock extends
		AbstractOptionsConfigurationBlock  {

	private static final Key PREF_SA_MISSING_METHOD_SEVERITY = getCoreKey(CorePreferenceConstants.PREF_SA_MISSING_METHOD_SEVERITY);
	private static final Key PREF_SA_MISSING_USE_STMT_SEVERITY = getCoreKey(CorePreferenceConstants.PREF_SA_MISSING_USE_STMT_SEVERITY);
	
	private Composite fieldEditorParent;
	private List<FieldEditor> fields;
	private BooleanFieldEditor analysisEnabled;
	private IPreferenceStore prefStore;
	
	public SemanticAnalysisConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {

		super(context, project, getKeys(), container);
		
		fields = new ArrayList<FieldEditor>();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_SA_MISSING_METHOD_SEVERITY, PREF_SA_MISSING_USE_STMT_SEVERITY };
	}

	@Override
	public Control createBlockContents(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		content.setLayout(layout);
		
		createFields(content);
		initialize();
		updateFieldVisibility();

		return content;
	}
	
	protected String[][] getSeverityOptions() {

		String[][] labelsAndValues = new String[3][2];
		labelsAndValues[0][0] = "Error";
		labelsAndValues[0][1] = CorePreferenceConstants.PREF_ERROR;
		labelsAndValues[1][0] = "Warning";
		labelsAndValues[1][1] = CorePreferenceConstants.PREF_WARN;
		labelsAndValues[2][0] = "Ignore";
		labelsAndValues[2][1] = CorePreferenceConstants.PREF_IGNORE;

		return labelsAndValues;

	}
	
	private void addField(String pref, String label) {
		
		ComboFieldEditor field = new ComboFieldEditor(pref, label, getSeverityOptions(), fieldEditorParent);
		fields.add(field);
	}
	
	private Composite createFields(Composite parent) {
		
        fieldEditorParent = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        fieldEditorParent.setLayout(layout);
        fieldEditorParent.setFont(parent.getFont());
		
        analysisEnabled = new BooleanFieldEditor(CorePreferenceConstants.PREF_SA_ENABLE, "Enable semantic analysis", fieldEditorParent);
        analysisEnabled.setPreferenceStore(getPreferenceStore());
        analysisEnabled.setPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateFieldVisibility();
			}
		});
        
        analysisEnabled.fillIntoGrid(fieldEditorParent, 2);
        
		addField(CorePreferenceConstants.PREF_SA_MISSING_METHOD_SEVERITY, "Missing method implementations");
		addField(CorePreferenceConstants.PREF_SA_MISSING_USE_STMT_SEVERITY, "Missing use statements");

		return fieldEditorParent;
	}
	
	protected IPreferenceStore getPreferenceStore() {
		
		if (prefStore == null) {
			prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, PEXCorePlugin.PLUGIN_ID); 
		}
		
		return prefStore;
	}
	
	protected void updateFieldVisibility() {
		
		if (analysisEnabled.getBooleanValue()) {
			for (FieldEditor editor : fields) {
				editor.setEnabled(true, fieldEditorParent);
			}
		} else {
			for (FieldEditor editor : fields) {
				editor.setEnabled(false, fieldEditorParent);
			}
		}
	}
	
    @SuppressWarnings("rawtypes")
	protected void initialize() {
        if (fields != null) {
            Iterator e = fields.iterator();
            while (e.hasNext()) {
                FieldEditor pe = (FieldEditor) e.next();
                pe.setPreferenceStore(getPreferenceStore());
                pe.load();
            }
        }
        
        analysisEnabled.load();
        
    }

    @SuppressWarnings("rawtypes")
	public void performDefaults() {
        if (fields != null) {
            Iterator e = fields.iterator();
            while (e.hasNext()) {
                FieldEditor pe = (FieldEditor) e.next();
                pe.loadDefault();
            }
        }
        
        analysisEnabled.loadDefault();
        updateFieldVisibility();
        super.performDefaults();
    }

    @SuppressWarnings("rawtypes")
	public boolean performOk() {
        if (fields != null) {
            Iterator e = fields.iterator();
            while (e.hasNext()) {
                FieldEditor pe = (FieldEditor) e.next();
                pe.store();
            }
        }
        analysisEnabled.store();
        return true;
    }
}
