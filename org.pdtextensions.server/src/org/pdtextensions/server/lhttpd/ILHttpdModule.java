package org.pdtextensions.server.lhttpd;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public interface ILHttpdModule {
	
	/**
	 * Returns the project this module is associated with
	 * @return project
	 */
	IProject getProject();
	
	/**
	 * Returns the folder this module is associated with (maybe a path inside the project or the project root)
	 * @return web-folder
	 */
	IFolder getFolder();

}
