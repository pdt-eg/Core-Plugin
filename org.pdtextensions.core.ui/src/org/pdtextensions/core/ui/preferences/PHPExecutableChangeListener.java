package org.pdtextensions.core.ui.preferences;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.pdtextensions.core.log.Logger;

/**
 * Ensures that the Launcher knows that the user has set a PHP executable and avoid additional error dialogs.
 *
 */
@SuppressWarnings({ "deprecation", "restriction" })
public class PHPExecutableChangeListener implements IPropertyChangeListener {

	private final String bundleId;
	private final String executableKey;

	public PHPExecutableChangeListener(String bundleID, String executableKey) {
		this.bundleId = bundleID;
		this.executableKey = executableKey;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (!"org.eclipse.php.debug.coreinstalledPHPDefaults".equals(event.getProperty())) {
			return;
		}

		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(bundleId);
		String executable = preferences.get(executableKey, null);

		if (executable != null && executable.length() > 0) {
			return;
		}

		try {
			PHPexeItem[] exes = PHPexes.getInstance().getAllItems();
			if (exes.length == 1) {
				Logger.debug("PHP executable changed, setting store value for " + bundleId + " to " + executableKey + " (" + exes[0].getExecutable().toString() + ")" );
				preferences.put(executableKey, exes[0].getExecutable().toString());
				preferences.flush();
				
				// we don't need it anymore now
				PHPDebugPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
			}

		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}
