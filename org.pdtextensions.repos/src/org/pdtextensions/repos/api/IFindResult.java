package org.pdtextensions.repos.api;

import org.eclipse.core.runtime.IStatus;

/**
 * Interface for responses on repository searches.
 * 
 * @author mepeisen
 */
public interface IFindResult {
	
	/**
	 * Returns true if the search was successful in the meaning the search returned without errors. Empty result sets results in isOk() == true.
	 * @return true on successful searches.
	 */
	boolean isOk();
	
	/**
	 * Returns the status object.
	 * @return status object (may be an error or Status.OK).
	 */
	IStatus getStatus();
	
	/**
	 * Returns the results if there are any results.
	 * @return resulting versions.
	 */
	Iterable<IModuleVersion> moduleVersions();

}
