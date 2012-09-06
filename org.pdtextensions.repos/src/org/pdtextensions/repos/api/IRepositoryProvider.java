package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A repository provider enables access to a remote repository.
 * 
 * The API is independent from underlying implementation.
 * 
 * @author mepeisen
 */
public interface IRepositoryProvider {
	
	/**
	 * returns a generic id that can be used to identify a configured repository.
	 * @return the id.
	 */
	String getId();
	
	/**
	 * Searches for a specific module (case-insensitive search)
	 * @param vendor The vendor name of the module; may contain '*' wildcard
	 * @param name The module name of the module; may be null to search for all modules of the vendor; may contain '*' wildcard
	 * @param version The version number of the module; may be null to search for all versions; may contain '*' wildcard
	 * @return the result of this search
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	IFindResult findModule(String vendor, String name, String version) throws CoreException;
	
	/**
	 * Lists all modules in the repository. Notice: This may be very slow. Instead you should do a more finer search with find method
	 * or use the {@see TagAwareProvider} or {@see VendorAwareProvider} interface if it is available. 
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules() throws CoreException;

}
