/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

/**
 * A configuration representing a location section in httpd.conf
 * @author mepeisen
 */
public interface ILHttpdLocationConfig {
	
	/**
	 * Returns the path of this location section
	 * @return path
	 */
	String getPath();
	
	/**
	 * Returns the htdocs path
	 * @return htdocs path
	 */
	String getHtdocs();
	
	/**
	 * Returns the module (if any module)
	 * @return the module
	 */
	ILHttpdModule getModule();
	
	/**
	 * Returns the allow override config
	 * @return allow ovveride directive content
	 */
	String getAllowOverride();

}
