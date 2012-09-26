package org.pdtextensions.server.lhttpd;

/**
 * A configuration representing a location section in httpd.conf
 * @author mepeisen
 */
public interface ILHttpdLocationConfig {
	
	/**
	 * Returns the path of this location section
	 * @return path
	 */
	String getPath();
	
	/**
	 * Returns the htdocs path
	 * @return htdocs path
	 */
	String getHtdocs();
	
	/**
	 * Returns the module (if any module)
	 * @return the module
	 */
	ILHttpdModule getModule();
	
	/**
	 * Returns the allow override config
	 * @return allow ovveride directive content
	 */
	String getAllowOverride();

}
