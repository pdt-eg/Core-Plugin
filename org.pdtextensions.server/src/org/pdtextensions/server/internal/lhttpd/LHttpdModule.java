package org.pdtextensions.server.internal.lhttpd;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.pdtextensions.server.lhttpd.ILHttpdModule;

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

}
