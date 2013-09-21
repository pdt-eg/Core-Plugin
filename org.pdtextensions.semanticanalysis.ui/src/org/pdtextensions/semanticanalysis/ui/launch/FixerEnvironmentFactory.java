/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.launch;

import org.eclipse.jface.preference.IPreferenceStore;
import org.pdtextensions.core.launch.environment.AbstractEnvironmentFactory;
import org.pdtextensions.core.launch.environment.PrjPharEnvironment;
import org.pdtextensions.semanticanalysis.ui.PEXAnalysisUIPlugin;
import org.pdtextensions.semanticanalysis.ui.preferences.AnalysPreferenceConstants.Keys;

public class FixerEnvironmentFactory extends AbstractEnvironmentFactory {

	public static final String LAUNCHER_KEY = "org.pdtextensions.semanticanalysis.fixerLauncherFactory";
	
	@Override
	protected IPreferenceStore getPreferenceStore() {
		return PEXAnalysisUIPlugin.getDefault().getParentPreferenceStore();
	}

	@Override
	protected String getPluginId() {
		return PEXAnalysisUIPlugin.PLUGIN_ID;
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
