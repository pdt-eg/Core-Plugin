package org.pdtextensions.semanticanalysis.internal.validator;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.pdtextensions.semanticanalysis.IValidatorManager;

@Singleton
public class Manager implements IValidatorManager {
	@PostConstruct
	public void initialize(IExtensionRegistry registry) {
		final IConfigurationElement[] config = registry.getConfigurationElementsFor(Extension_ID);
		//try {
			for(IConfigurationElement el : config) {
				System.out.println(el.getName());
			}
		//} catch (CoreException e) {
		//	PEXAnalysisPlugin.error(e);
	    //}
		
	}

}
