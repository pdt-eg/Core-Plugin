package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A single type within the repository.
 * 
 * @author mepeisen
 */
public interface IType {
	
	/**
	 * Returns the owning repository.
	 * @return owning repository
	 */
	ITypeAwareProvider getProvider();
	
	/**
	 * Returns the parent type.
	 * @return parent type or {@code null} for top level types
	 */
	IType getParent();
	
	/**
	 * Returns the type name.
	 * @return type name
	 */
	String getName();
	
	/**
	 * Returns the child types
	 * @return child types
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> getChildren() throws CoreException;
	
	/**
	 * Searches for a specific module (case-insensitive search)
	 * @param vendor The vendor name of the module; may contain '*' wildcard
	 * @param name The module name of the module; may be null to search for all modules of the vendor; may contain '*' wildcard
	 * @param version The version number of the module; may be null to search for all versions; may contain '*' wildcard
	 * @return the result of this search
	 */
	IFindResult findModule(String vendor, String name, String version);
	
	/**
	 * Lists all modules in the types. 
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules() throws CoreException;
	
	/**
	 * Searches the child types for a specific type
	 * @param name The tag name; must not contain any wildcards
	 * @param deep true to search for deep types; false to search on top level
	 * @return the list of types that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> searchType(String name, boolean deep) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchTypeRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the child types for a specific type (supports regular expressions)
	 * @param name The type name
	 * @param deep true to search for deep types; false to search on top level
	 * @return the list of types that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> searchTypeRegex(String name, boolean deep) throws CoreException;

}
