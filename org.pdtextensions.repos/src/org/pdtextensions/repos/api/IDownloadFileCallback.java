package org.pdtextensions.repos.api;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A callback for file downloads of dependencies
 * 
 * @author mepeisen
 */
public interface IDownloadFileCallback {
	
	/**
	 * Invoked on dependency download
	 * @param file
	 * @param owningModule
	 * @param dependency
	 * @param monitor
	 */
	void onDependency(IFile file, IModuleVersion owningModule, IDependency dependency, IProgressMonitor monitor);

}
