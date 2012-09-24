package org.pdtextensions.repos.api;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A callback for file downloads of dependencies
 * 
 * @author mepeisen
 */
public interface IDownloadStreamCallback {
	
	/**
	 * Invoked on dependency download
	 * @param stream
	 * @param owningModule
	 * @param dependency
	 * @param monitor
	 */
	void onDependency(InputStream stream, IModuleVersion owningModule, IDependency dependency, IProgressMonitor monitor);

}
