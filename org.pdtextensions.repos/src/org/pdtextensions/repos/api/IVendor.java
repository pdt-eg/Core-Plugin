package org.pdtextensions.repos.api;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the result of this search
	 */
	IFindResult findModule(String name, String version, IProgressMonitor monitor);
	
	/**
	 * Lists all modules in the vendor. 
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	Iterable<IModule> listModules(IProgressMonitor monitor) throws CoreException;

}
