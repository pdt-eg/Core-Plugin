package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A type aware provider is similar to the tag aware provider. Instead of providing human readable names
 * it provides technical names (f.e. "library" or "webapp").
 * 
 * @author mepeisen
 */
public interface ITypeAwareProvider extends IRepositoryProvider {
	
	/**
	 * Returns all types within the repository at top level.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return types at top level.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> getToplevelTypes(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Searches the repository for a specific type
	 * @param name The type name; must not contain any wildcards
	 * @param deep true to search for deep types; false to search on top level
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of types that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> searchType(String name, boolean deep, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchTypeRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the repository for a specific type (supports regular expressions)
	 * @param name The type name
	 * @param deep true to search for deep types; false to search on top level
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of types that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> searchTypeRegex(String name, boolean deep, IProgressMonitor monitor) throws CoreException;

}
