package org.pdtextensions.server.lhttpd;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.server.web.IPhpWebProject;

public interface ILHttpdModule {
	
	/**
	 * Returns the project this module is associated with
	 * @return project
	 */
	IProject getProject();
	
	/**
	 * Returns the web project
	 * @return web project
	 */
	IPhpWebProject getWebProject();
	
	/**
	 * Returns the folder this module is associated with (maybe a path inside the project or the project root)
	 * @return web-folder
	 */
	IContainer getFolder();

}
