package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A single vendor within the repository.
 * 
 * @author mepeisen
 */
public interface IVendor {
	
	/**
	 * Returns the owning repository.
	 * @return owning repository
	 */
	IVendorAwareProvider getProvider();
	
	/**
	 * Returns the vendor name.
	 * @return vendor name
	 */
	String getName();
	
	/**
	 * Searches for a specific module (case-insensitive search)
	 * @param name The module name of the module; may be null to search for all modules of the vendor; may contain '*' wildcard
	 * @param version The version number of the module; may be null to search for all versions; may contain '*' wildcard
	 * @return the result of this search
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	IFindResult findModule(String name, String version) throws CoreException;
	
	/**
	 * Lists all modules in the vendor. 
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules() throws CoreException;

}
