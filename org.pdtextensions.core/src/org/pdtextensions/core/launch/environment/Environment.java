package org.pdtextensions.core.launch.environment;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.ScriptNotFoundException;

public interface Environment {

	public boolean isAvailable();
	
	public void setUp(IProject project) throws ScriptNotFoundException;
	
	public CommandLine getCommand();
}
