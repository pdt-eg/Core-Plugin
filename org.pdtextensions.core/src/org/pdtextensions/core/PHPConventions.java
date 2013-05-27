/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelStatus;

/**
 * @since 0.17.0
 */
public final class PHPConventions {
    private static final Pattern PATTERN_FIELD_NAME = Pattern.compile("^\\$[a-zA-Z_\\x7f-\\xff\\u0100-\\uffff][a-zA-Z0-9_\\x7f-\\xff\\u0100-\\uffff]*$"); //$NON-NLS-1$
    private static final Pattern PATTERN_LABEL = Pattern.compile("^[a-zA-Z_\\x7f-\\xff\\u0100-\\uffff][a-zA-Z0-9_\\x7f-\\xff\\u0100-\\uffff]*$"); //$NON-NLS-1$

	public static IStatus validateConstantName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateFieldName(String name) {
		return validateIdentifier(name, PATTERN_FIELD_NAME);
	}

	public static IStatus validateMethodName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateTypeName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateIdentifier(String id, Pattern pattern) {
		if (pattern.matcher(id).matches()) {
			return IModelStatus.VERIFIED_OK;
		} else {
			return new Status(IStatus.ERROR, PEXCorePlugin.PLUGIN_ID, -1, Messages.bind(Messages.Convention_illegalIdentifier, id), null);
		}
	}

	public static IStatus validateIdentifier(String id) {
		return validateIdentifier(id, PATTERN_LABEL);
	}
}
