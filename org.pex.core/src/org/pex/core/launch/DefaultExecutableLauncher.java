/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pex.core.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.pex.core.exception.ExecutableNotFoundException;
import org.pex.core.log.Logger;
import org.pex.core.util.LaunchUtil;


public class DefaultExecutableLauncher implements IPHPLauncher {

	@Override
	public void launch(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException {

		doLaunch(scriptPath, arguments, handler, false);
		
	}

	@Override
	public void launchAsync(String scriptPath, String[] arguments, ILaunchResponseHandler handler)
			throws IOException, InterruptedException {

		doLaunch(scriptPath, arguments, handler, true);
		
	}
	
	public void doLaunch(String scriptPath, String[] arguments, ILaunchResponseHandler handler, boolean async) throws IOException, InterruptedException {
		
		String phpExe = null;
		
		try {
			phpExe = LaunchUtil.getPHPExecutable();
		} catch (ExecutableNotFoundException e) {
			Logger.logException(e);
		}
		
		String[] args = new String[arguments.length + 2];
		args[0] = phpExe;
		args[1] = scriptPath;
		
		System.arraycopy(arguments, 0, args, 2, arguments.length);
		Process p=Runtime.getRuntime().exec(args);
		
		if (async == false) {
			p.waitFor();
		}

		BufferedReader output=new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		String result="";
		String temp=output.readLine();
		while (temp != null) {
			if (temp.trim().length() > 0) {
				result=result + "\n" + temp;
			}
			temp=output.readLine();
		}
		
		if (handler != null) {
			handler.handle(result.trim());
		}
	}
}
