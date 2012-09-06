/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.util;

public class ArrayUtil {

	public static String implode(String glue, String[] inputArray) {

		StringBuilder sb = new StringBuilder();
		sb.append(inputArray[0]);

		for (int i = 1; i < inputArray.length; i++) {
			sb.append(glue);
			sb.append(inputArray[i]);
		}

		return sb.toString();
	}
}
