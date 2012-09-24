package org.pdtextensions.repos.api;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * A single module dependency.
 * 
 * @author mepeisen
 */
public interface IDependency {
	
	/**
	 * Returns the owning repository provider.
	 * @return repository provider.
	 */
	IRepositoryProvider getProvider();
	
	/**
	 * Returns the owning module (that module declaring the dependency).
	 * @return owning module.
	 */
	String getOwningModuleName();
	
	/**
	 * Returns the owning vendor (that module declaring the dependency).
	 * @return owning vendor.
	 */
	String getOwningVendorName();
	
	/**
	 * Returns the owning version name (that module declaring the dependency).
	 * @return owning version name.
	 */
	String getOwningVersionName();
	
	/**
	 * Returns the module (that module the owning module is depending on).
	 * @return module.
	 */
	String getModuleName();

	/**
	 * Returns the vendor (of that module the owning module is depending on).
	 * @return module.
	 */
	String getVendorName();
	
	/**
	 * Returns the version name (of that module the owning module is depending on).
	 * @return version name. May contain additional hints for version ranges, for example "[1.0.0,)"
	 */
	String getVersionName();
	
	/**
	 * Returns the file name (of that module the owning module is depending on).
	 * @return file name or {@code null} for the primary file. 
	 */
	String getFileName();
	
	/**
	 * Downloads the file with given name
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @return the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	IFile download(boolean useCache, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Downloads the file with given name
	 * @param useCache true to return files from cache; false to force direct download by skipping the cache. Caching is specific to
	 *        repository implementations. Some implementations may not use any caches. However users and implementors may assume
	 *        that it is always safe to cache release files for unlimited time.
	 * @param monitor progress monitor; may be {@code null}
	 * @return the input stream to read the file
	 * @throws CoreException thrown if the file could not be fully downloaded or the file was not found.
	 */
	InputStream openStream(boolean useCache, IProgressMonitor monitor) throws CoreException;

}
