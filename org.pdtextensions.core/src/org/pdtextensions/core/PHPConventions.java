/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelStatus;
import org.eclipse.php.internal.core.documentModel.parser.PHPTokenizer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public final class PHPConventions {
	private static final PHPTokenizer SCANNER = new PHPTokenizer();

	public static IStatus validateFieldName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateMethodName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateTypeName(String name) {
		return validateIdentifier(name);
	}

	public static IStatus validateIdentifier(String id) {
		if (scannedIdentifier(id) != null) {
			return IModelStatus.VERIFIED_OK;
		} else {
			return new Status(IStatus.ERROR, PEXCorePlugin.PLUGIN_ID, -1, org.eclipse.dltk.internal.core.util.Messages.bind(Messages.convention_illegalIdentifier, id), null);
		}
	}

	private static synchronized char[] scannedIdentifier(String id) {
		if (id == null) return null;

		ITextRegion token;
		SCANNER.reset(id.toCharArray());

		try {
			token = SCANNER.getNextToken();
		} catch (IOException e) {
			return null;
		}

		return token.toString().toCharArray();
	}
}
