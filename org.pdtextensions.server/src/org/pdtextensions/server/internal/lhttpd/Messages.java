/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.internal.lhttpd;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.pdtextensions.server.internal.lhttpd.messages"; //$NON-NLS-1$
	public static String InstallableRuntime2_DownloadKnownSize;
	public static String InstallableRuntime2_DownloadUnknownSize;
	public static String InstallableRuntime2_ErrorInstallingServer;
	public static String InstallableRuntime2_TaskInstallRuntime;
	public static String InstallableRuntime2_TaskUncompressing;
	public static String LHttpdServerRuntime_HttpdExecutableNotFound;
	public static String LinuxInstallable_Xampp180LinuxName;
	public static String Win32Installable_Xampp180WindowsName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
