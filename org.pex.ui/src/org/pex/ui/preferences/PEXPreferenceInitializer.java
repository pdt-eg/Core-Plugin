/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pex.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.pex.ui.PEXUIPlugin;


public class PEXPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		
		IPreferenceStore store = PEXUIPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_USE_DEFAULT_FIXERS, "yes");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_CONFIG, "phpcs_config_default");
		
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_INDENTATION, "phpcs_option_indentation::indentation::Code must use 4 spaces for indenting, not tabs.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_LINEFEED, "phpcs_option_linefeed::linefeed::All PHP files must use the Unix LF (linefeed) line ending.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_TRAILING_SPACES, "phpcs_option_trailing_spaces::trailing_spaces::Remove trailing whitespace at the end of lines.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_UNUSED_USE, "phpcs_option_unused_use::unused_use::Unused use statements must be removed.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_PHP_CLOSING_TAG, "phpcs_option_php_closing_tag::php_closing_tag::The closing ?> tag MUST be omitted from files containing only PHP.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_SHORT_TAG, "phpcs_option_php_closing_tag::short_tag::PHP code must use the long tags or the short-echo tags; it must not use the other tag variations.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_RETURN, "phpcs_option_return::return::An empty line feed should precede a return statement.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_VISIBILITY, "phpcs_option_return::visibility::Visibility must be declared on all properties and methods; abstract and final must be declared before the visibility; static must be declared after the visibility.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_BRACES, "phpcs_option_braces::braces::Opening braces for classes and methods must go on the next line, and closing braces must go on the next line after the body.\nOpening braces for control structures must go on the same line, and closing braces must go on the next line after the body.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_PHPDOC_PARAMS, "phpcs_option_phpdoc_params::phpdoc_params::All items of the @param phpdoc tags must be aligned vertically.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_EOF_ENDING, "phpcs_option_eof_ending::eof_ending:: A file must always ends with an empty line feed.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_EXTRA_EMPTY_LINES, "phpcs_option_extra_empty_lines::extra_empty_lines::Removes extra empty lines.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_INCLUDE, "phpcs_option_include::include::Include and file path should be devided with single space. File path should not be placed under brackets.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_PSR0, "phpcs_option_psr0::psr0:: Classes must be in a path that matches their namespace, be at least one namespace deep, and the class name should match the file name.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_CONTROLS_SPACE, "phpcs_option_controls_space::controls_spaces::A single space should be between: the closing brace and the control, the control and the opening parenthese, the closing parenthese and the opening brace.::true");
		store.setDefault(PEXPreferenceNames.PREF_PHPCS_OPTION_ELSEIF, "phpcs_option_elseif::elseif::The keyword elseif should be used instead of else if so that all control keywords looks like single words.::true");
		
	}
}
