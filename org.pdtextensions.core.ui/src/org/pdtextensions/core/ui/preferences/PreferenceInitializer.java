package org.pdtextensions.core.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.pdtextensions.core.CorePreferenceConstants;
import org.pdtextensions.core.PEXCorePlugin;
import org.pdtextensions.core.ui.PEXUIPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PEXUIPlugin.getDefault()
				.getPreferenceStore();
		
		store.setDefault(PreferenceConstants.ENABLED, false);
		store.setDefault(PreferenceConstants.LINE_ALPHA, 50);
		store.setDefault(PreferenceConstants.LINE_STYLE, SWT.LINE_SOLID);
		store.setDefault(PreferenceConstants.LINE_WIDTH, 1);
		store.setDefault(PreferenceConstants.LINE_SHIFT, 3);
		store.setDefault(PreferenceConstants.LINE_COLOR, "0,0,0"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.DRAW_LEFT_END, true); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.DRAW_BLANK_LINE, false); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.SKIP_COMMENT_BLOCK, false); //$NON-NLS-1$

		/**
		 * PHP cs fixer
		 * TODO: check how we can move this to the semanticanalysis plugin.
		 * when creating a separate initializer in the other plugin, it gets not called for some reason
		 */
		store.setDefault(PreferenceConstants.PREF_PHPCS_USE_DEFAULT_FIXERS, "yes");
		store.setDefault(PreferenceConstants.PREF_PHPCS_CONFIG, "phpcs_config_default");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_INDENTATION, "phpcs_option_indentation::indentation::Code must use 4 spaces for indenting, not tabs.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_LINEFEED, "phpcs_option_linefeed::linefeed::All PHP files must use the Unix LF (linefeed) line ending.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_TRAILING_SPACES, "phpcs_option_trailing_spaces::trailing_spaces::Remove trailing whitespace at the end of lines.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_UNUSED_USE, "phpcs_option_unused_use::unused_use::Unused use statements must be removed.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_PHP_CLOSING_TAG, "phpcs_option_php_closing_tag::php_closing_tag::The closing ?> tag MUST be omitted from files containing only PHP.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_SHORT_TAG, "phpcs_option_php_closing_tag::short_tag::PHP code must use the long tags or the short-echo tags; it must not use the other tag variations.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_RETURN, "phpcs_option_return::return::An empty line feed should precede a return statement.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_VISIBILITY, "phpcs_option_return::visibility::Visibility must be declared on all properties and methods; abstract and final must be declared before the visibility; static must be declared after the visibility.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_BRACES, "phpcs_option_braces::braces::Opening braces for classes and methods must go on the next line, and closing braces must go on the next line after the body.\nOpening braces for control structures must go on the same line, and closing braces must go on the next line after the body.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_PHPDOC_PARAMS, "phpcs_option_phpdoc_params::phpdoc_params::All items of the @param phpdoc tags must be aligned vertically.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_EOF_ENDING, "phpcs_option_eof_ending::eof_ending:: A file must always ends with an empty line feed.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_EXTRA_EMPTY_LINES, "phpcs_option_extra_empty_lines::extra_empty_lines::Removes extra empty lines.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_INCLUDE, "phpcs_option_include::include::Include and file path should be devided with single space. File path should not be placed under brackets.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_PSR0, "phpcs_option_psr0::psr0:: Classes must be in a path that matches their namespace, be at least one namespace deep, and the class name should match the file name.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_CONTROLS_SPACE, "phpcs_option_controls_space::controls_spaces::A single space should be between: the closing brace and the control, the control and the opening parenthese, the closing parenthese and the opening brace.::true");
		store.setDefault(PreferenceConstants.PREF_PHPCS_OPTION_ELSEIF, "phpcs_option_elseif::elseif::The keyword elseif should be used instead of else if so that all control keywords looks like single words.::true");
		
		
		/**
		 * Initialize core preferences
		 */
		ScopedPreferenceStore coreStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, PEXCorePlugin.PLUGIN_ID); 
			
		/**
		 * Semantic analysis page
		 */
		coreStore.setDefault(CorePreferenceConstants.PREF_SA_ENABLE, true);
		coreStore.setDefault(CorePreferenceConstants.PREF_SA_MISSING_METHOD_SEVERITY, CorePreferenceConstants.PREF_WARN);
		coreStore.setDefault(CorePreferenceConstants.PREF_SA_MISSING_USE_STMT_SEVERITY, CorePreferenceConstants.PREF_WARN);
		coreStore.setDefault(CorePreferenceConstants.PREF_SA_DUPLICATE_USE_SEVERITY, CorePreferenceConstants.PREF_ERROR);
		
	}
}
