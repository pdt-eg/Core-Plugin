package org.pdtextensions.server.web;

import org.eclipse.core.resources.IContainer;

public interface IPhpWebFolder {
	
	IContainer getFolder();
	
	String getPathName();
	
	void set(IContainer folder, String pathName);

}
