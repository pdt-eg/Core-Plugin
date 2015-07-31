/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PEXAnalysisTestPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.pdtextensions.semanticanalysis.tests"; //$NON-NLS-1$

	// The shared instance
	private static PEXAnalysisTestPlugin plugin;
	
	/**
	 * The constructor
	 */
	public PEXAnalysisTestPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PEXAnalysisTestPlugin getDefault() {
		return plugin;
	}

}
