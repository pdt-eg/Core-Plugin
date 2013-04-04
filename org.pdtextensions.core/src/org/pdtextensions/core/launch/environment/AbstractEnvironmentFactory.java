package org.pdtextensions.core.launch.environment;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.ui.preferences.util.PreferencesSupport;

@SuppressWarnings("restriction")
public abstract class AbstractEnvironmentFactory implements EnvironmentFactory {

	@Override
	public Environment getEnvironment(IProject project) {

		IPreferenceStore store = getPreferenceStore();
		System.err.println("plugin id " + getPluginId());
		System.err.println("exec key " + getExecutableKey());
		PreferencesSupport prefSupport = new PreferencesSupport(getPluginId(), store);
		String executable = prefSupport.getPreferencesValue(getExecutableKey(), null, project);
		String useProjectPhar = prefSupport.getPreferencesValue(getUseProjectKey(), null, project);
		String systemPhar = prefSupport.getPreferencesValue(getScriptKey(), null, project);
		
		if (executable != null && executable.length() > 0) {
			if (useProjectPhar != null && "true".equals(useProjectPhar) || (systemPhar == null || systemPhar.length() == 0) ) {
				return getProjectEnvironment(executable);
			}
			
			return new SysPhpSysPhar(executable, systemPhar);
		}
		
		return null;		
	}
	
	protected abstract IPreferenceStore getPreferenceStore();
	protected abstract String getPluginId();
	protected abstract PrjPharEnvironment getProjectEnvironment(String executable);
	protected abstract String getExecutableKey();
	protected abstract String getUseProjectKey();
	protected abstract String getScriptKey();

}
