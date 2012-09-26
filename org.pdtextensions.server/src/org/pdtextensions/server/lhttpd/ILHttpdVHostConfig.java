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
