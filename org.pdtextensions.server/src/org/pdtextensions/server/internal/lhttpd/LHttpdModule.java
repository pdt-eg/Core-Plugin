/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.internal.lhttpd;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.pdtextensions.server.lhttpd.ILHttpdModule;
import org.pdtextensions.server.web.IPhpWebProject;

public class LHttpdModule extends ProjectModule implements ILHttpdModule {

	public LHttpdModule() {
		super();
	}

	public LHttpdModule(IProject project) {
		super(project);
	}

	@Override
	public IFolder getFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pdtextensions.server.lhttpd.ILHttpdModule#getWebProject()
	 */
	@Override
	public IPhpWebProject getWebProject() {
		// TODO Auto-generated method stub
		return null;
	}

}
