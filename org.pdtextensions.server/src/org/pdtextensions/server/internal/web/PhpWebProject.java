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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.web.IPhpWebFolder;
import org.pdtextensions.server.web.IPhpWebProject;
import org.pdtextensions.server.web.IPhpWebProjectListener;

/**
 * The web project implementation.
 * 
 * @author mepeisen
 */
public class PhpWebProject implements IPhpWebProject {
	
	/**
	 * the underlying project
	 */
	private IProject project;
	
	private IWebProjectStorage storage;
	
	private List<IPhpWebProjectListener> listeners = new ArrayList<IPhpWebProjectListener>();

	/**
	 * Constructor
	 * @param project
	 */
	public PhpWebProject(IProject project) {
		this.project = project;
	}

	@Override
	public IProject getEclipseProject() {
		return this.project;
	}

	@Override
	public IScriptProject getScriptProject() {
		return DLTKCore.create(project);
	}

	@Override
	public IFacetedProject getFacetedProject() {
		try {
			return ProjectFacetsManager.create(project);
		} catch (CoreException ex) {
			PEXServerPlugin.logError(ex);
			return null;
		}
	}

	private void init() {
		if (this.storage == null) {
			if (this.hasWebFacet()) {
				try {
					this.storage = new SettingsPhpProjectStorage(project);
				} catch (BackingStoreException e) {
					PEXServerPlugin.logError(e);
					this.storage = new PlainPhpProjectStorage(project);
				}
			} else {
				this.storage = new PlainPhpProjectStorage(project);
			}
		}
	}

	@Override
	public IContainer getDefaultWebFolder() {
		init();
		return this.storage.getDefaultWebFolder();
	}

	@Override
	public boolean hasWebFacet() {
		final IFacetedProject faceted = this.getFacetedProject();
		if (faceted != null) {
			final IProjectFacet webFacet = ProjectFacetsManager
					.getProjectFacet(FACET_ID);
			return faceted.hasProjectFacet(webFacet);
		}
		return false;
	}

	@Override
	public void activateWebFacet() throws CoreException {
		final IFacetedProject faceted = this.getFacetedProject();
		if (faceted != null) {
			final IProjectFacet webFacet = ProjectFacetsManager
					.getProjectFacet(FACET_ID);
			if (faceted.hasProjectFacet(webFacet)) {
				return;
			}
			final IProjectFacetVersion version = webFacet.getVersion(FACET_VERSION_1_0);
			faceted.installProjectFacet(version, null, new NullProgressMonitor());
			try {
				this.storage = new SettingsPhpProjectStorage(project);
			} catch (BackingStoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Error reading preferences", e)); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void setDefaultWebFolder(IContainer folder) throws CoreException {
		init();
		final IContainer oldValue = this.getDefaultWebFolder();
		if (!oldValue.equals(folder)) {
			this.storage.setDefaultWebFolder(folder);
		}
	}

	@Override
	public IPhpWebFolder[] getWebFolders() {
		init();
		return this.storage.getWebFolders();
	}

	@Override
	public IPhpWebFolder createWebFolder(IContainer folder, String pathName)
			throws CoreException {
		init();
		final WebFolder result = new WebFolder(storage.createWebFolder(folder, pathName));
		for (final IPhpWebProjectListener listener : listeners) {
			listener.onAddedWebFolder(this, result);
		}
		return result;
	}

	@Override
	public void removeWebFolder(IPhpWebFolder folder) throws CoreException {
		init();
		this.storage.removeWebFolder(((WebFolder)folder).folder);
		for (final IPhpWebProjectListener listener : listeners) {
			listener.onRemovedFolder(this, folder);
		}
	}

	@Override
	public void registerProjectListener(IPhpWebProjectListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeProjectListener(IPhpWebProjectListener listener) {
		this.listeners.remove(listener);
	}

	public void notifyProjectClosed() {
		for (final IPhpWebProjectListener listener : listeners) {
			listener.onProjectClosed(this);
		}
	}

	public void notifySettingsChanged() {
		if (storage instanceof SettingsPhpProjectStorage) {
			try {
				((SettingsPhpProjectStorage) storage).reload();
			} catch (BackingStoreException e) {
				PEXServerPlugin.logError(e);
			}
			// TODO Notify listeners about changes
		}
	}
	
	private class WebFolder implements IPhpWebFolder {
		
		private IPhpWebFolder folder;
		
		public WebFolder(IPhpWebFolder folder) {
			this.folder = folder;
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#getFolder()
		 */
		@Override
		public IContainer getFolder() {
			return folder.getFolder();
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#getPathName()
		 */
		@Override
		public String getPathName() {
			return folder.getPathName();
		}

		/**
		 * @see org.pdtextensions.server.web.IPhpWebFolder#set(org.eclipse.core.resources.IContainer, java.lang.String)
		 */
		@Override
		public void set(IContainer folder, String pathName)
				throws CoreException {
			final IContainer oldFolder = this.folder.getFolder();
			final String oldPath = this.folder.getPathName();
			this.folder.set(folder, pathName);
			for (final IPhpWebProjectListener listener : listeners) {
				listener.onChangedFolder(PhpWebProject.this, this.folder, oldFolder, folder, oldPath, pathName);
			}
		}
		
	}

}
