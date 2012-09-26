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
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return child tags
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> getChildren(IProgressMonitor monitor) throws CoreException;
	
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
	 * Lists all modules in the tag. 
	 * @param monitor the progress monitor; support to cancel a long search 
	 * @return The modules list.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<IModule> listModules(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Searches the child tags for a specific tag
	 * @param name The tag name; must not contain any wildcards
	 * @param deep true to search for deep tags; false to search on top level
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of tags that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> searchTag(String name, boolean deep, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if the repository supports regular expression search
	 * @return true for support of method {@see #searchTagRegex(String, boolean)}
	 */
	boolean supportsRegexSearch();
	
	/**
	 * Searches the child tags for a specific tag (supports regular expressions)
	 * @param name The tag name
	 * @param deep true to search for deep tags; false to search on top level
	 * @param monitor the progress monitor; support to cancel a long search; maybe null
	 * @return the list of tags that were found.
	 * @throws CoreException thrown if the remote repository could not be contacted
	 */
	List<ITag> searchTagRegex(String name, boolean deep, IProgressMonitor monitor) throws CoreException;

}
