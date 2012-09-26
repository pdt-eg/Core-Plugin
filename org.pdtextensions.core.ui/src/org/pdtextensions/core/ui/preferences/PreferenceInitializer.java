package org.pdtextensions.core.ui.preferences;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PEXUIPlugin.getDefault()
				.getPreferenceStore();
		Map<?, ?> map = CodeFormatterConstants.getDefaultSettings();
		Iterator<?> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			store.setDefault(key, (String) map.get(key));
		}
		
		store.setDefault(PreferenceConstants.ENABLED, false);
		store.setDefault(PreferenceConstants.LINE_ALPHA, 50);
		store.setDefault(PreferenceConstants.LINE_STYLE, SWT.LINE_SOLID);
		store.setDefault(PreferenceConstants.LINE_WIDTH, 1);
		store.setDefault(PreferenceConstants.LINE_SHIFT, 3);
		store.setDefault(PreferenceConstants.LINE_COLOR, "0,0,0"); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.DRAW_LEFT_END, true); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.DRAW_BLANK_LINE, false); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.SKIP_COMMENT_BLOCK, false); //$NON-NLS-1$
		
	}
}
