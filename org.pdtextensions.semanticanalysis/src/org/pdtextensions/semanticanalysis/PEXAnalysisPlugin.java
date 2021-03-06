/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class PEXAnalysisPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.pdtextensions.semanticanalysis"; //$NON-NLS-1$
	
	public static final String VALIDATORS_PREFERENCES_NODE_ID = PLUGIN_ID + "/validators"; //$NON-NLS-1$

	private static PEXAnalysisPlugin plugin;
	
	public static BundleContext getContext() {
		return FrameworkUtil.getBundle(PEXAnalysisPlugin.class).getBundleContext();
	}

	public void start(BundleContext bundleContext) throws Exception {
		plugin = this;
	}
	
	
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

	public static PEXAnalysisPlugin getDefault() {
		return plugin;
	}

	public static void error(String message) {
		plugin.getLog()
				.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message,
						null));
	}

	public static void error(Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e
						.getLocalizedMessage(), e));
	}

	public static void error(String message, Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e));
	}

	public static void warn(String message) {
		warn(message, null);
	}

	public static void warn(String message, Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, message, e));
	}

	public static void warn(Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, e
						.getLocalizedMessage(), e));
	}

	public static void info(String message) {
		info(message, null);
	}

	public static void info(String message, Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, message, e));
	}

	public static void info(Throwable e) {
		plugin.getLog().log(
				new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, e
						.getLocalizedMessage(), e));
	}
}
