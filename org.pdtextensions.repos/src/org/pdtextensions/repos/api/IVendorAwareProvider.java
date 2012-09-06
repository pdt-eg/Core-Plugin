package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A vendor aware provider allows to access the repository modules by vendor names.
 * 
 * @author mepeisen
 */
public interface IVendorAwareProvider extends IRepositoryProvider {
	
	/**
	 * Returns all vendors within the repository.
	 * @return vendors.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IVendor> getVendors() throws CoreException;
	
	/**
	 * Searches the repository for a specific vendor
	 * @param name The vendor name; must not contain any wildcards
	 * @return the list of vendors that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IVendor> searchVendor(String name) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchVendorRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the repository for a specific vendor (supports regular expressions)
	 * @param name The vendor name
	 * @return the list of vendors that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IVendor> searchVendorRegex(String name) throws CoreException;

}
