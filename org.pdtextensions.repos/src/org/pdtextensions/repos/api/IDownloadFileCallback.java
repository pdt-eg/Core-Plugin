/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.repos.api;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A callback for file downloads of dependencies
 * 
 * @author mepeisen
 */
public interface IDownloadFileCallback {
	
	/**
	 * Invoked on dependency download
	 * @param file
	 * @param owningModule
	 * @param dependency
	 * @param monitor
	 */
	void onDependency(IFile file, IModuleVersion owningModule, IDependency dependency, IProgressMonitor monitor);

}
