package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * A tag aware provider allows to access the repository modules by human readable tags.
 * Tags are categories that group up modules and provide a human readable structure.
 * 
 * @author mepeisen
 */
public interface ITagAwareProvider extends IRepositoryProvider {
	
	/**
	 * Returns all tags within the repository at top level.
	 * @return tags at top level.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> getToplevelTags() throws CoreException;
	
	/**
	 * Searches the repository for a specific tag
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
	 * Searches the repository for a specific tag (supports regular expressions)
	 * @param name The tag name
	 * @param deep true to search for deep tags; false to search on top level
	 * @return the list of tags that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> searchTagRegex(String name, boolean deep) throws CoreException;

}
