package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A vendor aware provider allows to access the repository modules by vendor names.
 * 
 * @author mepeisen
 */
public interface IVendorAwareProvider extends IRepositoryProvider {
	
	/**
	 * Returns all vendors within the repository.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return vendors.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	Iterable<IVendor> getVendors(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Searches the repository for a specific vendor
	 * @param name The vendor name; must not contain be null; may conatin '*' as wildcard
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of vendors that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	Iterable<IVendor> searchVendor(String name, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchVendorRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the repository for a specific vendor (supports regular expressions)
	 * @param name The vendor name
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of vendors that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IVendor> searchVendorRegex(String name, IProgressMonitor monitor) throws CoreException;

}
