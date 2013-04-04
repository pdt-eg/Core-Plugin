package org.pdtextensions.core.launch;

import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.pdtextensions.core.PEXCorePlugin;
import org.pdtextensions.core.exception.ExecutableNotFoundException;
import org.pdtextensions.core.launch.environment.Environment;
import org.pdtextensions.core.launch.environment.EnvironmentFactory;
import org.pdtextensions.core.log.Logger;

@Creatable
public class ScriptLauncherManager implements ScriptLauncherInterface {
	
	private static final String LAUNCHER_ID = PEXCorePlugin.PLUGIN_ID + ".executableLauncher";
	private final HashMap<String, EnvironmentFactory> factories = new HashMap<String, EnvironmentFactory>();
	
	@Inject
	public ScriptLauncherManager(IExtensionRegistry registry) {
		evaluate(registry);
	}
	
	private void evaluate(IExtensionRegistry registry) {
		try {
			IConfigurationElement[] config = registry.getConfigurationElementsFor(LAUNCHER_ID);
			for (IConfigurationElement e : config) {
				final EnvironmentFactory factory = (EnvironmentFactory) e.createExecutableExtension("class");
				if (factory != null) {
					factories.put(e.getAttribute("id"), factory);
				}
			}
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	private Environment getEnvironment(String factoryId, IProject project) throws ExecutableNotFoundException {
		
		if (!factoryId.contains(factoryId)) {
			return null;
		}
		
		return factories.get(factoryId).getEnvironment(project);
	}
	
	/* (non-Javadoc)
	 * @see org.pdtextensions.core.launch.ScriptLauncherInterface#getLauncher(java.lang.String, org.eclipse.core.resources.IProject)
	 */
	@Override
	public ScriptLauncher getLauncher(String factoryId, IProject project) throws ScriptNotFoundException, ExecutableNotFoundException {
		Environment env = getEnvironment(factoryId, project);
		if (env == null) {
			throw new ScriptNotFoundException("Can't find any executable");
		}
		
		return new ScriptLauncher(env, project);
	}	
	
	/* (non-Javadoc)
	 * @see org.pdtextensions.core.launch.ScriptLauncherInterface#resetEnvironment()
	 */
	@Override
	public void resetEnvironment() {
		//TODO: ?
		/*
		if (env != null) {
			synchronized (env) {
				env = null;
			}	
		}
		*/
	}
}
