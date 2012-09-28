/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.preferences;

import org.pdtextensions.core.ui.preferences.PreferenceConstants;


public class PEXPreferenceNames {

	public static String[] getPHPCSFixerConfigLabels() {
		
		return new String[] {
				"Default",
				"Magento",
				"Symfony 2.0",
				"Symfony 2.1"
			};
	}
	
	
	public static String[] getPHPCSFixerConfig() {
		
		return new String[] {
			PreferenceConstants.PREF_PHPCS_CONFIG_DEFAULT,
			PreferenceConstants.PREF_PHPCS_CONFIG_MAGENTO,
			PreferenceConstants.PREF_PHPCS_CONFIG_SF20,
			PreferenceConstants.PREF_PHPCS_CONFIG_SF21
		};
	}
	public static String[] getPHPCSFixerOptions() {
		
		return new String[] {
				PreferenceConstants.PREF_PHPCS_OPTION_INDENTATION,
				PreferenceConstants.PREF_PHPCS_OPTION_LINEFEED,
				PreferenceConstants.PREF_PHPCS_OPTION_TRAILING_SPACES,
				PreferenceConstants.PREF_PHPCS_OPTION_UNUSED_USE,
				PreferenceConstants.PREF_PHPCS_OPTION_PHP_CLOSING_TAG,
				PreferenceConstants.PREF_PHPCS_OPTION_SHORT_TAG,
				PreferenceConstants.PREF_PHPCS_OPTION_RETURN,
				PreferenceConstants.PREF_PHPCS_OPTION_VISIBILITY,
				PreferenceConstants.PREF_PHPCS_OPTION_BRACES,
				PreferenceConstants.PREF_PHPCS_OPTION_PHPDOC_PARAMS,
				PreferenceConstants.PREF_PHPCS_OPTION_EOF_ENDING,
				PreferenceConstants.PREF_PHPCS_OPTION_EXTRA_EMPTY_LINES,
				PreferenceConstants.PREF_PHPCS_OPTION_INCLUDE,
				PreferenceConstants.PREF_PHPCS_OPTION_PSR0,
				PreferenceConstants.PREF_PHPCS_OPTION_CONTROLS_SPACE,
				PreferenceConstants.PREF_PHPCS_OPTION_ELSEIF
		};
	}
}
