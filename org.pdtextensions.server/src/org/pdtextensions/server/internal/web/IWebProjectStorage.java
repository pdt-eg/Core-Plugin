/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.internal.web;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.pdtextensions.server.web.IPhpWebFolder;

/**
 * A storage for web projects.
 * 
 * @author mepeisen
 */
public interface IWebProjectStorage {
	
	/**
	 * Returns the default web folder used as webroot
	 * @return default web folder.
	 */
	IContainer getDefaultWebFolder();
	
	/**
	 * Sets the default web folder. Can only be used on projects with web facets. Projects
	 * without web facets do always return the project itself as web root.
	 * @param folder folder to be used; must not be null
	 * @throws CoreException thrown on failures
	 */
	void setDefaultWebFolder(IContainer folder) throws CoreException;
	
	/**
	 * Returns the configured web folders
	 * @return configured web folders.
	 */
	IPhpWebFolder[] getWebFolders();
	
	/**
	 * Creates a new folder.
	 * @param folder eclipse folder
	 * @param pathName the symbolic path name to be used (including leading slash)
	 * @return The web folder objects
	 * @throws CoreException thrown on failures
	 */
	IPhpWebFolder createWebFolder(IContainer folder, String pathName) throws CoreException;
	
	/**
	 * Removes the web folder.
	 * @param folder web folder.
	 * @throws CoreException thrown on failures
	 */
	void removeWebFolder(IPhpWebFolder folder) throws CoreException;

}
