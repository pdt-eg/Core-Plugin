/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.web;

import org.eclipse.core.resources.IContainer;

public interface IPhpWebFolder {
	
	IContainer getFolder();
	
	String getPathName();
	
	void set(IContainer folder, String pathName);

}
