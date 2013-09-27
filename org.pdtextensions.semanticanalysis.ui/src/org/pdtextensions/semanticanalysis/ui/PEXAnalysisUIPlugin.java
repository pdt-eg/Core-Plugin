/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class PEXAnalysisUIPlugin extends AbstractUIPlugin {
	/**
	 * Preference store for PEXAnalysis (fallback)
	 */
	IPreferenceStore parentPreferenceStore;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.pdtextensions.semanticanalysis.ui"; //$NON-NLS-1$

	// The shared instance
	private static PEXAnalysisUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public PEXAnalysisUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PEXAnalysisUIPlugin getDefault() {
		return plugin;
	}
	
	public IPreferenceStore getParentPreferenceStore() {
        // Create the preference store lazily.
        if (parentPreferenceStore == null) {
            parentPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, PEXAnalysisPlugin.getDefault().getBundle().getSymbolicName());

        }
        return parentPreferenceStore;
    }
	
}
