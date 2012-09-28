/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.php.internal.ui.preferences.PHPTemplateStore;

@SuppressWarnings("restriction")
public class PDTTemplateStore extends PHPTemplateStore {

	public PDTTemplateStore(ContextTypeRegistry registry,
			IPreferenceStore store, String key) {
		super(registry, store, key);
	}

}
