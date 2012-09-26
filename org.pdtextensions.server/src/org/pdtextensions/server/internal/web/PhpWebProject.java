package org.pdtextensions.server.internal.web;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
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
		// TODO Auto-generated method stub
	}

	@Override
	public IContainer getDefaultWebFolder() {
		init();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasWebFacet() {
		final IFacetedProject faceted = this.getFacetedProject();
		if (faceted != null) {
			
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void activateWebFacet() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultWebFolder(IContainer folder) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IPhpWebFolder[] getWebFolders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhpWebFolder createWebFolder(IContainer folder, String pathName)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeWebFolder(IPhpWebFolder folder) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerProjectListener(IPhpWebProjectListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeProjectListener(IPhpWebProjectListener listener) {
		// TODO Auto-generated method stub

	}

	public void notifyProjectClosed() {
		// TODO Auto-generated method stub
		
	}

}
