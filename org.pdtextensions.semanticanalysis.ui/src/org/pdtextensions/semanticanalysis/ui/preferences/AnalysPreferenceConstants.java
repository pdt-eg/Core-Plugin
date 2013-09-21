/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.preferences;

import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;

public class AnalysPreferenceConstants {
	
	public interface Keys {
		public static final String PHP_EXECUTABLE = PEXAnalysisPlugin.PLUGIN_ID + "php_executable";
		public static final String FIXER_PHAR = PEXAnalysisPlugin.PLUGIN_ID + "fixer_phar";
		public static final String USE_PROJECT_PHAR = PEXAnalysisPlugin.PLUGIN_ID + "use_project_phar";
	}
}
