/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core;

import org.eclipse.osgi.util.NLS;

/**
 * @since 0.17.0
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.pdtextensions.core.messages"; //$NON-NLS-1$

	public static String Convention_illegalIdentifier;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
