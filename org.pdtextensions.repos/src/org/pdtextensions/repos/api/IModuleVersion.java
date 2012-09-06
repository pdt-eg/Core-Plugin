package org.pdtextensions.repos.api;

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
	IModule getModule();
	
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
	 * @return files for this version.
	 */
	Iterable<String> getFiles();
	
	/**
	 * Returns the primary file for this version. The primary file should represent the phar file that contains the php files of this version.
	 * @return primary file name or {@code null} if there is no primary file (empty/unknown/broken/obselete module version).
	 */
	String getPrimaryFile();
	
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
	IFile download(String name, boolean useCache, IProgressMonitor monitor);

}
