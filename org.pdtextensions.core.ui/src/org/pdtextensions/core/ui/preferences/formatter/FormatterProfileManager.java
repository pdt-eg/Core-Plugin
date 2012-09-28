/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;
import org.pdtextensions.core.ui.preferences.PreferencesAccess;

public class FormatterProfileManager extends ProfileManager {

	private static final String PROFILE_CONTRIBUTOR_ID = "org.pdtextensions.core.ui.formatterProfileContributor";	
	
	public final static String ECLIPSE_PROFILE = PEXUIPlugin.PLUGIN_ID
			+ ".default.eclipse_profile"; //$NON-NLS-1$

	public final static String DEFAULT_PROFILE = ECLIPSE_PROFILE;

	private final static KeySet[] KEY_SETS = new KeySet[] { new KeySet(
			PEXUIPlugin.PLUGIN_ID, new ArrayList(CodeFormatterConstants
					.getDefaultSettings().keySet())) };

	private final static String PROFILE_KEY = PEXUIPlugin.FORMATTER_PROFILE;
	private final static String FORMATTER_SETTINGS_VERSION = PEXUIPlugin.PLUGIN_ID
			+ ".formatter_settings_version"; //$NON-NLS-1$

	public FormatterProfileManager(List profiles, IScopeContext context,
			PreferencesAccess preferencesAccess,
			IProfileVersioner profileVersioner) {
		super(addBuiltinProfiles(profiles, profileVersioner), context,
				preferencesAccess, profileVersioner, KEY_SETS, PROFILE_KEY,
				FORMATTER_SETTINGS_VERSION);
	}

	private static List addBuiltinProfiles(List profiles,
			IProfileVersioner profileVersioner) {
		final Profile eclipseProfile = new BuiltInProfile(ECLIPSE_PROFILE,
				FormatterMessages.ProfileManager_eclipse_profile_name,
				getEclipseSettings(), 2, profileVersioner.getCurrentVersion(),
				profileVersioner.getProfileKind());
		profiles.add(eclipseProfile);
		
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(PROFILE_CONTRIBUTOR_ID);				
		try {							
			
			for (IConfigurationElement element : config) {				
				final Object extension = element.createExecutableExtension("class");				
				if (extension instanceof IProfileContributor) {									
					IProfileContributor contributor = (IProfileContributor) extension;
					
					for (BuiltInProfile profile : contributor.getBuiltinProfiles(profileVersioner)) {
						profiles.add(profile);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return profiles;
	}

	/**
	 * @return Returns the settings for the new eclipse profile.
	 */
	public static Map getEclipseSettings() {
		final Map options = CodeFormatterConstants.getDefaultSettings();

		ProfileVersioner.setLatestCompliance(options);
		return options;
	}

	/**
	 * @return Returns the default settings.
	 */
	public static Map getDefaultSettings() {
		return getEclipseSettings();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager#getSelectedProfileId(org.eclipse.core.runtime.preferences.IScopeContext)
	 */
	protected String getSelectedProfileId(IScopeContext instanceScope) {
		String profileId = instanceScope.getNode(PEXUIPlugin.PLUGIN_ID)
				.get(PROFILE_KEY, null);
		if (profileId == null) {
			profileId = new DefaultScope().getNode(PEXUIPlugin.PLUGIN_ID)
					.get(PROFILE_KEY, null);
		}
		return profileId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager#getDefaultProfile()
	 */
	public Profile getDefaultProfile() {
		return getProfile(DEFAULT_PROFILE);
	}

}
