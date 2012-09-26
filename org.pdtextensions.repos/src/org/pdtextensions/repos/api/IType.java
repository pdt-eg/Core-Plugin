/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.repos.api;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return child types
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> getChildren(IProgressMonitor monitor) throws CoreException;
	
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
	 * Lists all modules in the types. 
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Searches the child types for a specific type
	 * @param name The tag name; must not contain any wildcards
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
	 * Searches the child types for a specific type (supports regular expressions)
	 * @param name The type name
	 * @param deep true to search for deep types; false to search on top level
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of types that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IType> searchTypeRegex(String name, boolean deep, IProgressMonitor monitor) throws CoreException;

}
