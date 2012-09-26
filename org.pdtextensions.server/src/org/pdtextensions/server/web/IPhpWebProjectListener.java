package org.pdtextensions.server.web;

import org.eclipse.core.resources.IContainer;

/**
 * A listener that notifies about changes on php web projects.
 * 
 * @author mepeisen
 */
public interface IPhpWebProjectListener {
	
	/**
	 * Invoked on facet removals on a project
	 * @param webProject the web project changed to a classic php project
	 */
	void onFacetRemoved(IPhpWebProject webProject);
	
	/**
	 * Invoked on a change of the default webroot folder
	 * @param webProject the changed project
	 * @param oldValue old htdocs
	 * @param newValue new htdocs
	 */
	void onDefaultWebrootChanged(IPhpWebProject webProject, IContainer oldValue, IContainer newValue);
	
	/**
	 * Invoked on added web folders
	 * @param project changed project
	 * @param added added web folder
	 */
	void onAddedWebFolder(IPhpWebProject project, IPhpWebFolder added);
	
	/**
	 * Invoked on removed web folders
	 * @param project changed project
	 * @param removed removed web folder
	 */
	void onRemovedFolder(IPhpWebProject project, IPhpWebFolder removed);
	
	/**
	 * Invoked on changed web folders
	 * @param project changed project
	 * @param changed changed web folder
	 * @param oldFolder the old folder
	 * @param newFolder the new folder
	 * @param oldPath the old symbolic path
	 * @param newPath the new symbolic path
	 */
	void onChangedFolder(IPhpWebProject project, IPhpWebFolder changed, IContainer oldFolder, IContainer newFolder, String oldPath, String newPath);

}
