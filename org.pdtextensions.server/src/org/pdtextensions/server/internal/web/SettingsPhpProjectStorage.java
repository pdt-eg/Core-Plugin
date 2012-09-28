/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.internal.web;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.web.IPhpWebFolder;

/**
 * A storage using a settings file inside the projects root (.settings/org.pdtextensions.server.web.prefs).
 * 
 * @author mepeisen
 *
 */
public class SettingsPhpProjectStorage implements IWebProjectStorage {
	
	private static final String PREFS_DEFAULT_WEB_ROOT = "defaultWebRoot"; //$NON-NLS-1$

	private static final String PREFS_FOLDER_COUNT = "folderCount"; //$NON-NLS-1$

	private static final String PREFS_FOLDER_CONTAINER = "folder.container."; //$NON-NLS-1$

	private static final String PREFS_FOLDER_PATHNAME = "folder.pathName."; //$NON-NLS-1$

	private static final String PREFS_QUALIFIER = "org.pdtextensions.server.webproject"; //$NON-NLS-1$
	
	/** the settings filename */
	public static final String SETTINGS_FILENAME = ".settings/org.pdtextensions.server.webproject.prefs"; //$NON-NLS-1$
	
	/** eclipse project */
	private IProject project;
	
	/** the project default web folder. */
	private IContainer defaultWebFolder;
	
	/** the additional web folders. */
	private List<WebFolder> folders = new ArrayList<WebFolder>();
	
	private boolean prefsChanging = false;
	
	/**
	 * The php project
	 * @param project
	 */
	public SettingsPhpProjectStorage(IProject project) throws BackingStoreException {
		this.project = project;
		this.reload();
	}
	
	public void reload() throws BackingStoreException {
		if (!this.prefsChanging) {
			this.folders.clear();
			final ProjectScope scope = new ProjectScope(project);
			final IEclipsePreferences prefs = scope.getNode(PREFS_QUALIFIER);
			
			if (prefs.nodeExists(PREFS_DEFAULT_WEB_ROOT)) {
				final String def = prefs.get(PREFS_DEFAULT_WEB_ROOT, null);
				if (def.length() == 0) {
					this.defaultWebFolder = this.project;
				} else {
					this.defaultWebFolder = this.project.getFolder(def);
				}
			} else {
				this.defaultWebFolder = this.project;
			}
			
			if (prefs.nodeExists(PREFS_FOLDER_COUNT)) {
				for (int i = 0; i < prefs.getInt(PREFS_FOLDER_COUNT, 0); i++) {
					final String container = prefs.get(PREFS_FOLDER_CONTAINER + i, null);
					final String pathName = prefs.get(PREFS_FOLDER_PATHNAME + i, null);
					this.folders.add(new WebFolder(project.getFolder(container), pathName));
				}
			}
		}
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#getDefaultWebFolder()
	 */
	@Override
	public IContainer getDefaultWebFolder() {
		return this.defaultWebFolder;
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#setDefaultWebFolder(org.eclipse.core.resources.IContainer)
	 */
	@Override
	public void setDefaultWebFolder(IContainer folder) throws CoreException {
		if (!folder.getProject().equals(this.project)) {
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Container not within target project")); //$NON-NLS-1$
		}
		this.prefsChanging = true;
		try {
			final ProjectScope scope = new ProjectScope(project);
			final IEclipsePreferences prefs = scope.getNode(PREFS_QUALIFIER);
			prefs.put(PREFS_DEFAULT_WEB_ROOT, folder.getProjectRelativePath().toString());
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Error while saving preferences", e)); //$NON-NLS-1$
			}
			this.defaultWebFolder = folder;
		}
		finally {
			this.prefsChanging = false;
		}
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#getWebFolders()
	 */
	@Override
	public IPhpWebFolder[] getWebFolders() {
		return this.folders.toArray(new IPhpWebFolder[this.folders.size()]);
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#createWebFolder(org.eclipse.core.resources.IContainer, java.lang.String)
	 */
	@Override
	public IPhpWebFolder createWebFolder(IContainer folder, String pathName)
			throws CoreException {
		if (!folder.getProject().equals(this.project)) {
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Container not within target project")); //$NON-NLS-1$
		}
		if (!pathName.startsWith("/")) { //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Path name must start with /")); //$NON-NLS-1$
		}
		for (final WebFolder f : this.folders) {
			if (f.getFolder().equals(folder)) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Duplicate container")); //$NON-NLS-1$
			}
			if (f.getPathName().equals(pathName)) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Duplicate path namne")); //$NON-NLS-1$
			}
		}
		final WebFolder result = new WebFolder(folder, pathName);
		this.folders.add(result);
		this.saveFolders();
		return result;
	}

	private void saveFolders() throws CoreException {
		this.prefsChanging = true;
		try {
			final ProjectScope scope = new ProjectScope(project);
			final IEclipsePreferences prefs = scope.getNode(PREFS_QUALIFIER);
			prefs.put(PREFS_FOLDER_COUNT, String.valueOf(folders.size()));
			for (int i = 0; i < folders.size(); i++) {
				final WebFolder folder = this.folders.get(i);
				prefs.put(PREFS_FOLDER_CONTAINER + i, folder.getFolder().getProjectRelativePath().toString());
				prefs.put(PREFS_FOLDER_PATHNAME + i, folder.getPathName());
			}
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Error while saving preferences", e)); //$NON-NLS-1$
			}
		}
		finally {
			this.prefsChanging = false;
		}
	}

	/**
	 * @see org.pdtextensions.server.internal.web.IWebProjectStorage#removeWebFolder(org.pdtextensions.server.web.IPhpWebFolder)
	 */
	@Override
	public void removeWebFolder(IPhpWebFolder folder) throws CoreException {
		if (this.folders.remove(folder)) {
			this.saveFolders();
		}
	}
	
	private final class WebFolder implements IPhpWebFolder {

		private IContainer folder;
		private String pathName;
		
		/**
		 * @param folder
		 * @param pathName
		 */
		public WebFolder(IContainer folder, String pathName) {
			this.folder = folder;
			this.pathName = pathName;
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#getFolder()
		 */
		@Override
		public IContainer getFolder() {
			return this.folder;
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#getPathName()
		 */
		@Override
		public String getPathName() {
			return this.pathName;
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#set(org.eclipse.core.resources.IContainer, java.lang.String)
		 */
		@Override
		public void set(IContainer folder, String pathName) throws CoreException {
			if (!folder.getProject().equals(SettingsPhpProjectStorage.this.project)) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Container not within target project")); //$NON-NLS-1$
			}
			if (!pathName.startsWith("/")) { //$NON-NLS-1$
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Path name must start with /")); //$NON-NLS-1$
			}
			for (final WebFolder f : SettingsPhpProjectStorage.this.folders) {
				if (f.getFolder().equals(folder) && f != this) {
					throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Duplicate container")); //$NON-NLS-1$
				}
				if (f.getPathName().equals(pathName) && f != this) {
					throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Duplicate path namne")); //$NON-NLS-1$
				}
			}
			this.folder = folder;
			this.pathName = pathName;
			saveFolders();
		}
		
	}

}
