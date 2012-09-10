package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A single tag within the repository.
 * 
 * @author mepeisen
 */
public interface ITag {
	
	/**
	 * Returns the owning repository.
	 * @return owning repository
	 */
	ITagAwareProvider getProvider();
	
	/**
	 * Returns the parent tag.
	 * @return parent tag or {@code null} for top level tags
	 */
	ITag getParent();
	
	/**
	 * Returns the tag name.
	 * @return tag name
	 */
	String getName();
	
	/**
	 * Returns the child tags
	 * @return child tags
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> getChildren() throws CoreException;
	
	/**
	 * Searches for a specific module (case-insensitive search)
	 * @param vendor The vendor name of the module; may contain '*' wildcard
	 * @param name The module name of the module; may be null to search for all modules of the vendor; may contain '*' wildcard
	 * @param version The version number of the module; may be null to search for all versions; may contain '*' wildcard
	 * @return the result of this search
	 */
	IFindResult findModule(String vendor, String name, String version);
	
	/**
	 * Lists all modules in the tag. 
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules() throws CoreException;
	
	/**
	 * Searches the child tags for a specific tag
	 * @param name The tag name; must not contain any wildcards
	 * @param deep true to search for deep tags; false to search on top level
	 * @return the list of tags that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> searchTag(String name, boolean deep) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchTagRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the child tags for a specific tag (supports regular expressions)
	 * @param name The tag name
	 * @param deep true to search for deep tags; false to search on top level
	 * @return the list of tags that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> searchTagRegex(String name, boolean deep) throws CoreException;

}
