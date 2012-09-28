/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.ui.internal.web;

import org.eclipse.osgi.util.NLS;

/**
 * @author mepeisen
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.pdtextensions.server.ui.internal.web.messages"; //$NON-NLS-1$
	public static String ProjectPropertyPage_Add;
	public static String ProjectPropertyPage_Browse;
	public static String ProjectPropertyPage_ErrorDuplicatePath;
	public static String ProjectPropertyPage_ErrorHtdocsDoesNotExist;
	public static String ProjectPropertyPage_ErrorPathMustStartWithSlash;
	public static String ProjectPropertyPage_LabelHtdocsFolder;
	public static String ProjectPropertyPage_Remove;
	public static String ProjectPropertyPage_SelectHtdocsFolderDescription;
	public static String ProjectPropertyPage_SelectHtdocsFolderTitle;
	public static String ProjectPropertyPage_TableHtdocsFolder;
	public static String ProjectPropertyPage_TablePathName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
