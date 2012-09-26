/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
