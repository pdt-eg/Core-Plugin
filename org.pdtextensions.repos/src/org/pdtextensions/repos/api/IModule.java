package org.pdtextensions.repos.api;

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
	String getVendor();
	
	/**
	 * Returns the name of the module.
	 * @return module name.
	 */
	String getName();
	
	/**
	 * Lists the release versions of this module.
	 * @return release versions that are known by this repository.
	 */
	Iterable<IModuleVersion> listReleaseVersions();
	
	/**
	 * Lists the developer versions of this module
	 * @return developer versions that are known by this repository
	 */
	Iterable<IModuleVersion> listDevVersions();
	
	/**
	 * Lists all versions of this module
	 * @return versions that are known by this repository
	 */
	Iterable<IModuleVersion> listVersions();
	
	/**
	 * Returns the newest release version of the module
	 * @return newest release version of the module or {@code null} if there is no known release version
	 */
	IModuleVersion getNewestReleaseVersion();
	
	/**
	 * Returns the newest snapshot version of the module
	 * @return newest snapshot version of the module or {@code null} if there is no known snapshot version
	 */
	IModuleVersion getNewestDevVersion();
	
}
