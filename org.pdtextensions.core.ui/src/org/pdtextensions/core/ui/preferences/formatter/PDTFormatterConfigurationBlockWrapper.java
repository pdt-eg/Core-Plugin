package org.pdtextensions.core.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.ui.preferences.IPHPFormatterConfigurationBlockWrapper;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;
import org.pdtextensions.core.ui.preferences.formatter.CodeFormatterConfigurationBlock;


@SuppressWarnings("restriction")
public class PDTFormatterConfigurationBlockWrapper implements
	IPHPFormatterConfigurationBlockWrapper {
	
	private CodeFormatterConfigurationBlock pConfigurationBlock;

	public void init(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {

		IWorkingCopyManager workingCopyManager;
		if (container instanceof IWorkbenchPreferenceContainer) {
			workingCopyManager = ((IWorkbenchPreferenceContainer) container)
					.getWorkingCopyManager();
		} else {
			workingCopyManager = new WorkingCopyManager(); // non shared
		}
		PreferencesAccess access = PreferencesAccess
				.getWorkingCopyPreferences(workingCopyManager);
		
		pConfigurationBlock = new CodeFormatterConfigurationBlock(project, access);
	}

	public Control createContents(Composite composite) {
		return pConfigurationBlock.createContents(composite);
	}

	public void dispose() {
		pConfigurationBlock.dispose();
	}

	public boolean hasProjectSpecificOptions(IProject project) {
		return pConfigurationBlock.hasProjectSpecificOptions(project);
	}

	public void performApply() {
		pConfigurationBlock.performApply();
	}

	public void performDefaults() {
		pConfigurationBlock.performDefaults();
	}

	public boolean performOk() {
		return pConfigurationBlock.performOk();
	}

	public void useProjectSpecificSettings(boolean useProjectSpecificSettings) {
		pConfigurationBlock
				.enableProjectSpecificSettings(useProjectSpecificSettings);
	}

	public String getDescription() {
		return null;
	}
}
