/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

public interface ILHttpdVHostConfigWorkingCopy extends ILHttpdVHostConfig {
	
	/**
	 * Sets the server name
	 * @param serverName server name
	 */
	void setServerName(String serverName);
	
	/**
	 * Sets the module for htdocs
	 * @param module module
	 */
	void setModule(ILHttpdModule module);
	
	/**
	 * Creates a new location config for given path
	 * @param path
	 * @return working copy
	 */
	ILHttpdLocationConfigWorkingCopy createLocation(String path);
	
	/**
	 * Removes an existing location configuration
	 * @param location
	 */
	void removeLocation(ILHttpdLocationConfig location);
	
	/**
	 * Sets the allow override flag
	 * @param value allow override flag
	 */
	void setAllowOverride(String value);

}
