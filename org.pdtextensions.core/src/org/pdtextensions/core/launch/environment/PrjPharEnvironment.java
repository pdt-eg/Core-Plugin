package org.pdtextensions.core.launch.environment;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.pdtextensions.core.launch.ScriptNotFoundException;

public abstract class PrjPharEnvironment implements Environment {

	protected String phar;
	
	public void setUp(IProject project) throws ScriptNotFoundException {
		IResource phar = getScript(project);
		if (phar == null) {
			throw new ScriptNotFoundException("No script found in project " + project.getName());
		}
		
		if (phar.getFullPath().segmentCount() != 2) {
			throw new ScriptNotFoundException("The script file in project " + project.getName() + " is in the wrong location."); 
		}
		
		this.phar = phar.getFullPath().removeFirstSegments(1).toOSString();
	}
	
	protected abstract IResource getScript(IProject project);
	
}
