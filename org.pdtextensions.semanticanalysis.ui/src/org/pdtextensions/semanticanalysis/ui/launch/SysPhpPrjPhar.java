/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.launch;

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
