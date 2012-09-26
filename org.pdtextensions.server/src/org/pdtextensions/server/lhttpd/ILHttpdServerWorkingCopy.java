/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

public interface ILHttpdServerWorkingCopy extends ILHttpdServer {
	
	/**
	 * Creates a new port config
	 * @param port port number
	 * @return config working copy
	 */
	ILHttpdPortConfigWorkingCopy createPortConfig(int port);
	
	/**
	 * Removes an existing port config
	 * @param config config
	 */
	void removePortConfig(ILHttpdPortConfig config);
	
	/**
	 * Sets the flag for using the port configuration from runtime
	 * @param flgUsingRuntimePorts
	 */
	void setUsingRuntimePorts(boolean flgUsingRuntimePorts);
	
	/**
	 * Sets the flag for using the htdocs configuration from runtime
	 * @param flgUsingRuntimeHtdocs
	 */
	void setUsingRuntimeHtdocs(boolean flgUsingRuntimeHtdocs);
	
	/**
	 * Sets the htdocs module (results in setUsingRuntimeHtdocs(false))
	 * @param module
	 */
	void setHtdocsModule(ILHttpdModule module);
	
	/**
	 * Creates a new location configuration
	 * @param path path of the location configuration
	 * @return working copy
	 */
	ILHttpdLocationConfigWorkingCopy createLocationConfig(String path);
	
	/**
	 * Removes an existing location configuration
	 * @param config config
	 */
	void removeLocationConfig(ILHttpdLocationConfig config);
	
	/**
	 * Sets the flag for using the location configuration from runtime
	 * @param flgUsingRuntimeLocations
	 */
	void setUsingRuntimeLocations(boolean flgUsingRuntimeLocations);
	
	/**
	 * Creates a working copy for an existing port config
	 * @param config port config
	 * @return
	 */
	ILHttpdPortConfigWorkingCopy createWorkingCopy(ILHttpdPortConfig config);
	
	/**
	 * Creates a working copy for an existing location config
	 * @param config
	 * @return
	 */
	ILHttpdLocationConfigWorkingCopy createWorkingCopy(ILHttpdLocationConfig config);
	
	/**
	 * Creates a working copy for an existing vhost config
	 * @param config
	 * @return
	 */
	ILHttpdVHostConfigWorkingCopy createWorkingCopy(ILHttpdVHostConfig config);
	
	/**
	 * Sets the allow override directive value
	 * @param value
	 */
	void setAllowOverride(String value);
	
	/**
	 * Sets the flag for using allow override config from runtime
	 * @param flgRuntimeUsingAllowOverride
	 */
	void setUsingRuntimeAllowOverride(boolean flgRuntimeUsingAllowOverride);

}
