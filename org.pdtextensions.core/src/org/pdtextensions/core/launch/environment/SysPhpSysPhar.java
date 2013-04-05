package org.pdtextensions.core.launch.environment;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;

/**
 * An environment which uses a PHP executable selected by the user
 * and a script to global script to launch (also selected by the user).
 * 
 */
public class SysPhpSysPhar implements Environment {

	private String php;
	private String phar;
	
	public SysPhpSysPhar(String executable, String composerPhar) {
		php = executable;
		phar = composerPhar;
	}

	public boolean isAvailable() {
		return php != null && phar != null;
	}

	public void setUp(IProject project) {
		
	}

	public CommandLine getCommand() {
		CommandLine cmd = new CommandLine(php.trim());
		cmd.addArgument(phar.trim());
		
		return cmd;
	}
}
