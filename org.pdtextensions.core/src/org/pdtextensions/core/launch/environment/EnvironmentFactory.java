package org.pdtextensions.core.launch.environment;

import org.eclipse.core.resources.IProject;

/**
 * Interface for retrieving the proper {@link Environment} to lauch a script
 * for a project.
 * 
 * Use {@link AbstractEnvironmentFactory} for an abstract implementation.
 *  
 */
public interface EnvironmentFactory {

	public Environment getEnvironment(IProject project);
}
