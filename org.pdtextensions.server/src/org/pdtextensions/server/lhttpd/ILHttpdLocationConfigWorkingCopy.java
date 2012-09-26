package org.pdtextensions.server.lhttpd;

public interface ILHttpdLocationConfigWorkingCopy extends ILHttpdLocationConfig {
	
	/**
	 * Sets the location path
	 * @param path location path
	 */
	void setPath(String path);
	
	/**
	 * Sets the module
	 * @param module module
	 */
	void setModule(ILHttpdModule module);
	
	/**
	 * Sets the allow override directive value
	 * @param value allow override; null to remove it and use the server default.
	 */
	void setAllowOverride(String value);

}
