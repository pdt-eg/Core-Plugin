package org.pdtextensions.server.web;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * The php web project
 * 
 * @author mepeisen
 */
public interface IPhpWebProject {
	
	/** the facets id for the extended web project. */
	String FACET_ID = "php.web.project"; //$NON-NLS-1$
	
	/**
	 * Returns the underlying eclipse project.
	 * @return eclipse project
	 */
	IProject getEclipseProject();
	
	/**
	 * Returns the dltk script project
	 * @return dltk script project
	 */
	IScriptProject getScriptProject();
	
	/**
	 * Returns the faceted project
	 * @return faceted project
	 */
	IFacetedProject getFacetedProject();
	
	/**
	 * Returns the default web folder used as webroot
	 * @return default web folder.
	 */
	IContainer getDefaultWebFolder();

	/**
	 * Returns true if the project has an installed web facet
	 * @return true for the web facet
	 */
	boolean hasWebFacet();
	
	/**
	 * Activates the web project facet
	 * @throws CoreException thrown while errors during facet installation
	 */
	void activateWebFacet() throws CoreException;
	
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
	
	/**
	 * Registers a project listener to watch for web project changes.
	 * @param listener project listener to be added
	 */
	void registerProjectListener(IPhpWebProjectListener listener);
	
	/**
	 * Removes the project listener
	 * @param listener project listener to be removed
	 */
	void removeProjectListener(IPhpWebProjectListener listener);

}
