package org.pdtextensions.repos.api;

import org.eclipse.core.runtime.CoreException;

/**
 * A single module within a repository.
 * 
 * @author mepeisen
 */
public interface IModule {
	
	/**
	 * Returns the owning repository provider.
	 * @return repository provider.
	 */
	IRepositoryProvider getProvider();
	
	/**
	 * Returns the vendor of the module.
	 * @return module vendor.
	 */
	String getVendorName();
	
	/**
	 * Returns the name of the module.
	 * @return module name.
	 */
	String getName();
	
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @return the result of this search
	 */
	IFindResult findVersion(String version);
	
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @return the result of this search
	 */
	IFindResult findReleaseVersion(String version);
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @return the result of this search
	 */
	IFindResult findDevVersion(String version);
	

	/**
	 * Lists the release versions of this module.
	 * @return release versions that are known by this repository.
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listReleaseVersions() throws CoreException;
	
	/**
	 * Lists the developer versions of this module
	 * @return developer versions that are known by this repository
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listDevVersions() throws CoreException;
	
	/**
	 * Lists all versions of this module
	 * @return versions that are known by this repository
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listVersions() throws CoreException;
	
	/**
	 * Returns the newest release version of the module
	 * @return newest release version of the module or {@code null} if there is no known release version
	 * @throws CoreException 
	 */
	IModuleVersion getNewestReleaseVersion() throws CoreException;
	
	/**
	 * Returns the newest snapshot version of the module
	 * @return newest snapshot version of the module or {@code null} if there is no known snapshot version
	 * @throws CoreException 
	 */
	IModuleVersion getNewestDevVersion() throws CoreException;
	
}
