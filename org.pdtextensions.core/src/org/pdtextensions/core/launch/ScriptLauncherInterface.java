package org.pdtextensions.core.launch;

import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.exception.ExecutableNotFoundException;

public interface ScriptLauncherInterface {

	public abstract ScriptLauncher getLauncher(String factoryId,
			IProject project) throws ScriptNotFoundException,
			ExecutableNotFoundException;

	public abstract void resetEnvironment();

}