/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.repos.api;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A single version of a module.
 * 
 * @author mepeisen
 */
public interface IModuleVersion {
	
	/**
	 * Returns the owning module.
	 * @return owning module.
	 */
	String getModuleName();
	
	/**
	 * Returns the owning vendor.
	 * @return owning vendor.
	 */
	String getVendorName();
	
	/**
	 * Returns the version name.
	 * @return version name.
	 */
	String getName();
	
	/**
	 * Returns true if this is a release version.
	 * @return true for release versions.
	 */
	boolean isRelease();
	
	/**
	 * Returns true for development versions.
	 * @return true for development versions.
	 */
	boolean isDevelopment();
	
	/**
	 * Returns the list of files associated with this version.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return files for this version.
	 * @throws CoreException 
	 */
	Iterable<String> getFiles(final IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the primary file for this version. The primary file should represent the phar file that contains the php files of this version.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return primary file name or {@code null} if there is no primary file (empty/unknown/broken/obselete module version).
	 * @throws CoreException 
	 */
	String getPrimaryFile(final IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Downloads the file with given name
	 * @param name the file name or {@code null} to download the primary phar file.
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @return the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	IFile download(String name, boolean useCache, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Downloads the file with given name
	 * @param name the file name or {@code null} to download the primary phar file.
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @return the input stream to read the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	InputStream openStream(String name, boolean useCache, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the dependencies of this module
	 * @param deep true to return the deep dependencies; false to return only direct dependencies
	 * @param monitor progress monitor; may be {@code null}
	 * @return dependencies
	 * @throws CoreException thrown if the repository could not be contacted
	 */
	Iterable<IDependency> getDependencies(boolean deep, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Downloads the files of dependencies
	 * @param deep true to return the deep dependencies; false to return only direct dependencies
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @param callback the callback invoked on each dependency file
	 * @return the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	void downloadDependencies(boolean deep, boolean useCache, IProgressMonitor monitor, IDownloadFileCallback callback) throws CoreException;
	
	/**
	 * Downloads the file with given name
	 * @param deep true to return the deep dependencies; false to return only direct dependencies
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @param callback the callback invoked on each dependency stream
	 * @return the input stream to read the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	void openDependenciesStream(boolean deep, boolean useCache, IProgressMonitor monitor, IDownloadStreamCallback callback) throws CoreException;

}
