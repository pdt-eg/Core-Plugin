package org.pdtextensions.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.core.facet.PHPFacets;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.pdtextensions.server.internal.web.PhpWebProject;
import org.pdtextensions.server.web.IPhpWebProject;

public class PEXServerPlugin extends Plugin implements BundleActivator {

	private static BundleContext context;
	
	private static PEXServerPlugin plugin;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.pdtextensions.server"; //$NON-NLS-1$

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PEXServerPlugin.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		PEXServerPlugin.context = null;
		plugin = null;
		webProjects.clear();
		if (projectListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectListener);
			projectListener = null;
		}
	}

	/**
	 * Return the install location preference.
	 * 
	 * @param id a runtime type id
	 * @return the install location
	 */
	public static String getPreference(String id) {
		return InstanceScope.INSTANCE.getNode(PLUGIN_ID).get(id, null);
	}
	
	/**
	 * Set the install location preference.
	 * 
	 * @param id the runtime type id
	 * @param value the location
	 */
	public static void setPreference(String id, String value) {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		prefs.put(id, value);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			logError(e);
		}
	}
	
	public static void logError(Throwable t) {
		if (plugin != null) {
			final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, "Exception occurred", t); //$NON-NLS-1$
			plugin.getLog().log(status);
		}
	}
	
	public static void logError(String message, Throwable t) {
		if (plugin != null) {
			final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, t);
			plugin.getLog().log(status);
		}
	}
	
	private static Map<IProject, PhpWebProject> webProjects = new HashMap<IProject, PhpWebProject>();
	
	private static IResourceChangeListener projectListener;
	
	/**
	 * Creates a php web project from given eclipse project
	 * @param project eclipse project
	 * @return php web project
	 * @throws CoreException thrown if the given project is invalid (not a php/ faceted project)
	 */
	public static synchronized IPhpWebProject create(IProject project) throws CoreException {
		if (projectListener == null) {
			projectListener = new IResourceChangeListener() {
				
				@Override
				public void resourceChanged(IResourceChangeEvent event) {
					if (event.getType() == IResourceChangeEvent.PRE_CLOSE && event.getResource() instanceof IResource) {
						final PhpWebProject webProject = webProjects.get((IProject) event.getResource());
						if (webProject != null) {
							webProject.notifyProjectClosed();
							webProjects.remove((IProject) event.getResource());
						}
					}
				}
			};
			ResourcesPlugin.getWorkspace().addResourceChangeListener(projectListener, IResourceChangeEvent.PRE_CLOSE);
		}
		PhpWebProject webProject = webProjects.get(project);
		if (webProject == null) {
			if (PHPFacets.isFacetedProject(project)) {
				webProject = new PhpWebProject(project);
				webProjects.put(project, webProject);
			} else {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "Given project is not a php faceted project")); //$NON-NLS-1$
			}
		}
		return webProject;
	}

}
