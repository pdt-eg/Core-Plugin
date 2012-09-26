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
 * A single module within a repository.
 * 
 * @author mepeisen
 */
public interface IModule {
	
	/**
	 * Returns the owning repository provider.
	 * @return repository provider.
	 */
	IRepositoryProvider getProvider();
	
	/**
	 * Returns the vendor of the module.
	 * @return module vendor.
	 */
	String getVendorName();
	
	/**
	 * Returns the name of the module.
	 * @return module name.
	 */
	String getName();
	
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the result of this search
	 */
	IFindResult findVersion(String version, IProgressMonitor monitor);
	
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the result of this search
	 */
	IFindResult findReleaseVersion(String version, IProgressMonitor monitor);
	/**
	 * Searches for a specific version (case-insensitive search)
	 * @param version The version number of the module; must not be null; may contain '*' wildcard
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the result of this search
	 */
	IFindResult findDevVersion(String version, IProgressMonitor monitor);
	

	/**
	 * Lists the release versions of this module.
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return release versions that are known by this repository.
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listReleaseVersions(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Lists the developer versions of this module
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return developer versions that are known by this repository
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listDevVersions(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Lists all versions of this module
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return versions that are known by this repository
	 * @throws CoreException 
	 */
	Iterable<IModuleVersion> listVersions(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the newest release version of the module
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return newest release version of the module or {@code null} if there is no known release version
	 * @throws CoreException 
	 */
	IModuleVersion getNewestReleaseVersion(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the newest snapshot version of the module
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return newest snapshot version of the module or {@code null} if there is no known snapshot version
	 * @throws CoreException 
	 */
	IModuleVersion getNewestDevVersion(IProgressMonitor monitor) throws CoreException;
	
}
