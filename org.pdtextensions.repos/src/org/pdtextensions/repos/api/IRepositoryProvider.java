/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.repos.api;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
	 * Returns the provider uri.
	 * @return provider uri
	 */
	String getUri();
	
	/**
	 * Returns the type of repository
	 * @return type of repository
	 */
	String getType();
	
	/**
	 * Searches for a specific module (case-insensitive search)
	 * @param vendor The vendor name of the module; may contain '*' wildcard
	 * @param name The module name of the module; may be null to search for all modules of the vendor; may contain '*' wildcard
	 * @param version The version number of the module; may be null to search for all versions; may contain '*' wildcard
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the result of this search
	 */
	IFindResult findModule(String vendor, String name, String version, IProgressMonitor monitor);
	
	/**
	 * Lists all modules in the repository. Notice: This may be very slow. Instead you should do a more finer search with find method
	 * or use the {@see TagAwareProvider} or {@see VendorAwareProvider} interface if it is available.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	Iterable<IModule> listModules(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if dependency management is allowed.
	 * @return true for support of dependency management.
	 */
	boolean supportsDependencies();

}
