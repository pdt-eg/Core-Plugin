/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.lhttpd;

public interface ILHttpdPortConfig {
	
	/**
	 * Returns the port number
	 * @return port number
	 */
	int getPortNumber();
	
	/**
	 * Returns true if this is a ssl port
	 * @return true for ssl port
	 */
	boolean isSsl();
	
	/**
	 * Returns true if this is a vhost port
	 * @return true for vhost port
	 */
	boolean isVHost();
	
	/**
	 * Returns the vhosts config
	 * @return vhosts config
	 */
	ILHttpdVHostConfig[] getVHosts();

}
