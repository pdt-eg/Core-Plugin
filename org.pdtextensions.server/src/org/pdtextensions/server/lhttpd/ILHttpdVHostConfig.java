/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

public interface ILHttpdVHostConfig {
	
	/**
	 * returns the server name
	 * @return server name
	 */
	String getServerName();
	
	/**
	 * Returns the htdocs folder
	 * @return htdocs folder
	 */
	String getHtdocs();
	
	/**
	 * Returns the module for htdocs (if any)
	 * @return
	 */
	ILHttpdModule getModule();
	
	/**
	 * Returns the location configurations
	 * @return
	 */
	ILHttpdLocationConfig[] getLocations();
	
	/**
	 * Returns the value of the allow override directive
	 * @return
	 */
	String getAllowOverride();

}
