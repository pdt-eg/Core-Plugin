package org.pdtextensions.server.lhttpd;

public interface ILHttpdServer {
	
	/**
	 * Returns the port configurations
	 * @return port configurations
	 */
	ILHttpdPortConfig[] getPortConfigurations();
	
	/**
	 * Returns true if this server is using the port configuration from runtime
	 * @return true for using the runtime port configuration
	 */
	boolean isUsingRuntimePorts();
	
	/**
	 * Returns the htdocs folder
	 * @return htdocs folder
	 */
	String getDefaultHtdocs();
	
	/**
	 * Returns true if this server is using the htdocs folder from runtime
	 * @return true for using the runtime htdocs folder
	 */
	boolean isUsingRuntimeHtdocs();
	
	/**
	 * Returns module for htdocs folder (if any)
	 * @return htdocs module
	 */
	ILHttpdModule getHtdocsModule();
	
	/**
	 * Returns the location configs
	 * @return location configs
	 */
	ILHttpdLocationConfig[] getLocationConfigs();
	
	/**
	 * Returns true if this server is using the location config from runtime
	 * @return true for using the runtime location configs
	 */
	boolean isUsingRuntimeLocations();
	
	/**
	 * Returns the allow override config
	 * @return allow override
	 */
	String getAllowOverride();
	
	/**
	 * Returns true if this server is using the allow override config from runtime
	 * @return true for using the runtime allow override config
	 */
	boolean isUsingRuntimeAllowOverride();
	
	/**
	 * Returns the resulting httpd.conf
	 * @return resulting httpd.conf
	 */
	String getHttpdConf();

}
