package org.pdtextensions.server;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

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

}
