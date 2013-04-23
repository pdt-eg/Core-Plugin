/*******************************************************************************
 * Copyright (c) 2005, 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.core.ui.formatter.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.preferences.formatter.IProfileContributor;
import org.pdtextensions.core.ui.preferences.formatter.IProfileVersioner;
import org.pdtextensions.core.ui.preferences.formatter.ProfileManager.BuiltInProfile;
import org.pdtextensions.core.ui.preferences.formatter.ProfileVersioner;


/**
 * @since 0.17.0
 */
public class CoreProfileContributor implements IProfileContributor {

	public final static String PSR2_PROFILE = PEXUIPlugin.PLUGIN_ID + ".default.psr2_profile"; //$NON-NLS-1$

	public CoreProfileContributor() {
	}

	@Override
	public List<BuiltInProfile> getBuiltinProfiles(IProfileVersioner versioner) {
		
		List<BuiltInProfile> profiles = new ArrayList<BuiltInProfile>();
		
		final BuiltInProfile psr2Profile = new BuiltInProfile(PSR2_PROFILE,
				"PSR-2 [built in]",
				getPsr2Settings(), 2, versioner.getCurrentVersion(),
				versioner.getProfileKind());
		
		profiles.add(psr2Profile);
		
		return profiles;
		
	}

	@SuppressWarnings("rawtypes")
	private Map getPsr2Settings() {
		
		final Map options = PSR2FormatterOptions.getDefaultSettings().getMap();
		ProfileVersioner.setLatestCompliance(options);
		
		return options;
	}
}
