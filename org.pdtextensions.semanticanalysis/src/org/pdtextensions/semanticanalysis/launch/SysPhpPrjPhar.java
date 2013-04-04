package org.pdtextensions.semanticanalysis.launch;

import org.apache.commons.exec.CommandLine;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.pdtextensions.core.launch.environment.PrjPharEnvironment;

public class SysPhpPrjPhar extends PrjPharEnvironment {

	private String php;
	
	public SysPhpPrjPhar(String executable) {
		php = executable;
	}

	public boolean isAvailable() {
		return php != null;
	}

	public CommandLine getCommand() {
		CommandLine cmd = new CommandLine(php.trim());
		cmd.addArgument(phar.trim());
		
		return cmd;
	}
	@Override
	protected IResource getScript(IProject project) {
		return project.findMember("csfixer.phar");
	}
}
