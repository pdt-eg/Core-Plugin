package org.pdtextensions.semanticanalysis.ui.preferences.validation;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.preferences.AbstractPropertyAndPreferencePage;

@SuppressWarnings("restriction")
public class SemanticAnalysisPreferencePage extends
		AbstractPropertyAndPreferencePage {

	public static final String PREF_ID = "org.pdtextensions.semanticanalysis.ui.preferences.SemanticAnalysisPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.pdtextensions.semanticanalysis.ui.propertyPages.SemanticAnalysisPropertyPage"; //$NON-NLS-1$
	
	public SemanticAnalysisPreferencePage() {
		setPreferenceStore(PEXUIPlugin.getDefault().getPreferenceStore());
		setDescription("Configure how PDT handles semantic analysis problems");
	}

	@Override
	
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new SemanticAnalysisConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}
}
