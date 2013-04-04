package org.pdtextensions.semanticanalysis.launch;

import org.eclipse.jface.preference.IPreferenceStore;
import org.pdtextensions.core.launch.environment.AbstractEnvironmentFactory;
import org.pdtextensions.core.launch.environment.PrjPharEnvironment;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.preferences.AnalysPreferenceConstants.Keys;

public class FixerEnvironmentFactory extends AbstractEnvironmentFactory {

	public static final String LAUNCHER_KEY = "org.pdtextensions.semanticanalysis.fixerLauncherFactory";
	
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return PEXAnalysisPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getPluginId() {
		return PEXAnalysisPlugin.PLUGIN_ID;
	}

	@Override
	protected PrjPharEnvironment getProjectEnvironment(String executable) {
		return new SysPhpPrjPhar(executable);
	}

	@Override
	protected String getExecutableKey() {
		return Keys.PHP_EXECUTABLE;
	}

	@Override
	protected String getUseProjectKey() {
		return Keys.USE_PROJECT_PHAR;
	}

	@Override
	protected String getScriptKey() {
		return Keys.FIXER_PHAR;
	}
}
