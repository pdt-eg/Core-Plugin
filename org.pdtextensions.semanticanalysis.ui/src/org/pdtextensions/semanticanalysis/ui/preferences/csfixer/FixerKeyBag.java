/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.preferences.csfixer;

import org.eclipse.php.internal.ui.preferences.util.Key;
import org.pdtextensions.core.ui.preferences.launcher.LauncherKeyBag;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.ui.preferences.AnalysPreferenceConstants.Keys;

@SuppressWarnings("restriction")
public class FixerKeyBag implements LauncherKeyBag {

	private final Key exeKey = new Key(PEXAnalysisPlugin.PLUGIN_ID, Keys.PHP_EXECUTABLE); 
	private final Key pharKey = new Key(PEXAnalysisPlugin.PLUGIN_ID, Keys.FIXER_PHAR); 
	private final Key useKey = new Key(PEXAnalysisPlugin.PLUGIN_ID, Keys.USE_PROJECT_PHAR); 
	
	@Override
	public Key[] getAllKeys() {
		return new Key[]{exeKey, pharKey, useKey};		
	}

	@Override
	public Key getPHPExecutableKey() {
		return exeKey;
	}

	@Override
	public Key getScriptKey() {
		return pharKey;
	}

	@Override
	public Key getUseProjectKey() {
		return useKey;
	}
}
