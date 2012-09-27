/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.internal.web;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.web.IPhpWebFolder;

/**
 * A plain storage for non web projects
 * 
 * @author mepeisen
 *
 */
public class PlainPhpProjectStorage implements IWebProjectStorage {
	
	/** eclipse project */
	private IProject project;
	
	/**
	 * The php project
	 * @param project
	 */
	public PlainPhpProjectStorage(IProject project) {
		this.project = project;
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#setDefaultWebFolder(org.eclipse.core.resources.IContainer)
	 */
	@Override
	public void setDefaultWebFolder(IContainer folder) throws CoreException {
		if (this.project != folder) {
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Unable to change webroot on classic projects")); //$NON-NLS-1$
		}
		// silently ignore. The webroot is already to project itself.
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#getWebFolders()
	 */
	@Override
	public IPhpWebFolder[] getWebFolders() {
		return new IPhpWebFolder[0];
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#createWebFolder(org.eclipse.core.resources.IContainer, java.lang.String)
	 */
	@Override
	public IPhpWebFolder createWebFolder(IContainer folder, String pathName)
			throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Unable to create a new web folder on classic projects")); //$NON-NLS-1$
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#removeWebFolder(org.pdtextensions.server.web.IPhpWebFolder)
	 */
	@Override
	public void removeWebFolder(IPhpWebFolder folder) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Classic projects do not contain web folders. Cannot remove.")); //$NON-NLS-1$
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#getDefaultWebFolder()
	 */
	@Override
	public IContainer getDefaultWebFolder() {
		return this.project;
	}

}
