package org.pdtextensions.server.lhttpd;

import org.pdtextensions.server.IPEXInstallableRuntime;

public interface ILHttpdRuntime {
	
	/**
	 * Returns the installable runtime to install xampp via web
	 * @return installable runtime
	 */
	IPEXInstallableRuntime getInstallableRuntime();
	
	/**
	 * Returns the default port configurations from httpd.conf
	 * @return port configurations
	 */
	ILHttpdPortConfig[] getPortConfigurations();
	
	/**
	 * Returns the httpd.conf contents
	 * @return httpd.conf contents
	 */
	String getHttpdConf();
	
	/**
	 * flag to return the config without original htdocs directive
	 */
	int CONF_WITHOUT_HTDOCS = 0x0001;
	
	/**
	 * Flag to return the config without Listen/vhost directives
	 */
	int CONF_WITHOUT_PORTS = 0x0002;
	
	/**
	 * Flag to return the config without Location directives
	 */
	int CONF_WITHOUT_LOCATIONS = 0x0004;
	
	/**
	 * Flag to return the config without AllowOverride directives
	 */
	int CONF_WITHOUT_ALLOW_OVERRIDE = 0x0008;
	
	/**
	 * Returns the httpd.conf contents by respective the given filter flags
	 * @param flags filter flags, see CONF_* constants
	 * @return filteres httpd.conf
	 */
	String getHttpdConf(int flags);
	
	/**
	 * Returns the default htdocs folder
	 * @return htdocs folder
	 */
	String getDefaultHtdocs();
	
	/**
	 * Returns the default location configurations from httpd.conf
	 * @return the default location configurations.
	 */
	ILHttpdLocationConfig[] getLocationConfigs();
	
	/**
	 * Returns the allow override config
	 * @return allow override
	 */
	String getAllowOverride();

}
