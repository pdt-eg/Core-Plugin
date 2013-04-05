package org.pdtextensions.core.launch;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.eclipse.core.resources.IProject;
import org.pdtextensions.core.launch.environment.Environment;
import org.pdtextensions.core.launch.execution.ExecutionResponseListener;
import org.pdtextensions.core.launch.execution.ScriptExecutor;
import org.pdtextensions.core.log.Logger;

/**
 * 
 * Uses a {@link ScriptExecutor} to launch a PHP script with a specific {@link Environment}.
 * 
 * You can use an {@link ExecutionResponseListener} to get retrieve the output
 * of the executed script.
 *
 */
public class ScriptLauncher {

	private Environment environment;
	private IProject project;
	private ScriptExecutor executor;
	private Set<ExecutionResponseListener> listeners = new HashSet<ExecutionResponseListener>();
	
	public ScriptLauncher(Environment environment, IProject project) throws ScriptNotFoundException {
		this.environment = environment;
		this.project = project;
		this.environment.setUp(project);
	}

	public void addResponseListener(ExecutionResponseListener listener) {
		listeners.add(listener);
	}

	public void removeResponseListener(ExecutionResponseListener listener) {
		listeners.remove(listener);
	}
	
	public void launch(String argument) throws ExecuteException, IOException, InterruptedException {
		launch(argument, new String[]{});
	}
	
	public void launch(String argument, String param) throws ExecuteException, IOException, InterruptedException {
		launch(argument, new String[]{param});
	}
	
	public void launch(String argument, String[] params) throws ExecuteException, IOException, InterruptedException {
		CommandLine cmd = environment.getCommand();
		cmd.addArgument(argument);
		cmd.addArguments(params);
		
		executor = new ScriptExecutor();
		
		Logger.debug("Setting executor working directory to " + project.getLocation().toOSString());
		executor.setWorkingDirectory(project.getLocation().toFile());
		
		for (ExecutionResponseListener listener : listeners) {
			executor.addResponseListener(listener);
		}
		
		executor.execute(cmd);
	}
	
	public void abort() {
		executor.abort();
	}
}
