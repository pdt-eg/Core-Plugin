/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.pdtextensions.core.ui.extension.INamespaceResolver;


public class ExtensionManager {
	
	private static final String NAMESPACE_RESOLVER_ID = "org.pdtextensions.core.ui.namespaceresolver";	
	private static ExtensionManager instance;
	
	private List<INamespaceResolver> resolvers;	

	private ExtensionManager() {
		
		
	}
	public static ExtensionManager getDefault() {
		
		if (instance == null)
			return instance = new ExtensionManager();
		
		return instance;
		
	}
	
	public List<INamespaceResolver> getNamespaceResolvers() {
		
		if (resolvers != null) {			
			return resolvers;			
		}
		
		resolvers = new ArrayList<INamespaceResolver>();
		
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(NAMESPACE_RESOLVER_ID);		
		
		try {							
			
			for (IConfigurationElement element : config) {
				
				final Object extension = element.createExecutableExtension("class");
				
				if (extension instanceof INamespaceResolver) {					
					resolvers.add((INamespaceResolver) extension);
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return resolvers;		
		
	}

}
