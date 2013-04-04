package org.pdtextensions.core.launch.environment;

import org.eclipse.core.resources.IProject;

public interface EnvironmentFactory {

	public Environment getEnvironment(IProject project);
}
