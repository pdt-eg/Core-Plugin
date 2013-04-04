package org.pdtextensions.core.ui.preferences.launcher;

import org.eclipse.php.internal.ui.preferences.util.Key;

@SuppressWarnings("restriction")
public interface LauncherKeyBag {
	
	public Key[] getAllKeys();
	public Key getPHPExecutableKey();
	public Key getScriptKey();
	public Key getUseProjectKey();

}
