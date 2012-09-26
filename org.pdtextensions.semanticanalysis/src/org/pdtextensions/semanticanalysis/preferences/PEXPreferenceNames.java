/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.preferences;


public class PEXPreferenceNames {

	/* PHP-CS-Fixer */
	public static final String PREF_PHPCS_OPT_INDENTATION = "phpcs_indentation";
	public static final String PREF_PHPCS_USE_DEFAULT_FIXERS = "phpcs_use_default_fixers";
	public static final String PREF_PHPCS_PHAR_LOCATION = "phpcs_phar_location";
	public static final String PREF_PHPCS_PHAR_NAME = "phpcs_phar_name";
	public static final String PREF_PHPCS_CUSTOM_PHAR_LOCATIONS =  "phpcs_custom_phar_locations";
	public static final String PREF_PHPCS_CUSTOM_PHAR_NAMES = "phpcs_custom_custom_phar_names";
	public static final String PREF_PHPCS_CONFIG = "phpcs_config";
	
	public static final String PREF_PHPCS_OPTION_INDENTATION = "phpcs_option_indentation";
	public static final String PREF_PHPCS_OPTION_LINEFEED = "phpcs_option_linefeed";
	public static final String PREF_PHPCS_OPTION_TRAILING_SPACES= "phpcs_option_trailing_spaces";
	public static final String PREF_PHPCS_OPTION_UNUSED_USE = "phpcs_option_unused_use";
	public static final String PREF_PHPCS_OPTION_PHP_CLOSING_TAG = "phpcs_option_php_closing_tag";
	public static final String PREF_PHPCS_OPTION_SHORT_TAG = "phpcs_option_short_tag";
	public static final String PREF_PHPCS_OPTION_RETURN = "phpcs_option_return";
	public static final String PREF_PHPCS_OPTION_VISIBILITY = "phpcs_option_visibility";
	public static final String PREF_PHPCS_OPTION_BRACES = "phpcs_option_braces";
	public static final String PREF_PHPCS_OPTION_PHPDOC_PARAMS = "phpcs_option_phpdoc_params";
	public static final String PREF_PHPCS_OPTION_EOF_ENDING = "phpcs_option_eof_ending";
	public static final String PREF_PHPCS_OPTION_EXTRA_EMPTY_LINES = "phpcs_option_extra_empty_lines";
	public static final String PREF_PHPCS_OPTION_INCLUDE = "phpcs_option_include";
	public static final String PREF_PHPCS_OPTION_PSR0 = "phpcs_option_psr0";
	public static final String PREF_PHPCS_OPTION_CONTROLS_SPACE = "phpcs_option_controls_space";
	public static final String PREF_PHPCS_OPTION_ELSEIF = "phpcs_option_elseif";
	
	public static final String PREF_PHPCS_CONFIG_DEFAULT = "phpcs_config_default";
	public static final String PREF_PHPCS_CONFIG_MAGENTO = "phpcs_config_magento";
	public static final String PREF_PHPCS_CONFIG_SF20 = "phpcs_config_sf20";
	public static final String PREF_PHPCS_CONFIG_SF21 = "phpcs_config_sf21";
	
	/* End PHP-CS-Fixer*/
	
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
			PREF_PHPCS_CONFIG_DEFAULT,
			PREF_PHPCS_CONFIG_MAGENTO,
			PREF_PHPCS_CONFIG_SF20,
			PREF_PHPCS_CONFIG_SF21
		};
	}
	public static String[] getPHPCSFixerOptions() {
		
		return new String[] {
				PREF_PHPCS_OPTION_INDENTATION,
				PREF_PHPCS_OPTION_LINEFEED,
				PREF_PHPCS_OPTION_TRAILING_SPACES,
				PREF_PHPCS_OPTION_UNUSED_USE,
				PREF_PHPCS_OPTION_PHP_CLOSING_TAG,
				PREF_PHPCS_OPTION_SHORT_TAG,
				PREF_PHPCS_OPTION_RETURN,
				PREF_PHPCS_OPTION_VISIBILITY,
				PREF_PHPCS_OPTION_BRACES,
				PREF_PHPCS_OPTION_PHPDOC_PARAMS,
				PREF_PHPCS_OPTION_EOF_ENDING,
				PREF_PHPCS_OPTION_EXTRA_EMPTY_LINES,
				PREF_PHPCS_OPTION_INCLUDE,
				PREF_PHPCS_OPTION_PSR0,
				PREF_PHPCS_OPTION_CONTROLS_SPACE,
				PREF_PHPCS_OPTION_ELSEIF
		};
	}
}
