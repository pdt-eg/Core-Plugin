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
