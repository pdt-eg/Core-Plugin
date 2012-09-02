/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pex.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class PEXCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.pex.core";
	
	private static BundleContext context;

	private static PEXCorePlugin plugin;

	private static final String isDebugMode = "org.pex.core/debug";
	
	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		PEXCorePlugin.context = bundleContext;
		plugin = this;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		PEXCorePlugin.context = null;
		plugin = null;
	}
	
	public static PEXCorePlugin getDefault() {

		return plugin;
	}
	

	public static boolean debug() {
		String debugOption = Platform.getDebugOption(isDebugMode); //$NON-NLS-1$
		return getDefault().isDebugging() && "true".equalsIgnoreCase(debugOption); 
		
	}	
}
