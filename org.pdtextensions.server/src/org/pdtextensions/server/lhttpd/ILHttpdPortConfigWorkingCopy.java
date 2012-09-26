/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

public interface ILHttpdPortConfigWorkingCopy extends ILHttpdPortConfig {
	
	/**
	 * Sets the port number
	 * @param port port number
	 */
	void setPortNumber(int port);
	
	/**
	 * Sets the ssl flag
	 * @param flgIsSsl
	 */
	void setIsSsl(boolean flgIsSsl);
	
	/**
	 * Sets the vhosts flag
	 * @param flgIsVHost
	 */
	void setIsVHost(boolean flgIsVHost);
	
	/**
	 * Creates a new vhost config
	 * @param serverName server name of the vhost
	 * @return vhosts working copy
	 */
	ILHttpdVHostConfigWorkingCopy createVhost(String serverName);
	
	/**
	 * Removes an existing vhost
	 * @param config
	 */
	void removeVhost(ILHttpdVHostConfig config);

}
