package org.pdtextensions.core.ui.preferences.formatter;

import java.util.List;

import org.pdtextensions.core.ui.preferences.formatter.ProfileManager.BuiltInProfile;


public interface IProfileContributor {
	
	
	List<BuiltInProfile> getBuiltinProfiles(IProfileVersioner versioner);

}
