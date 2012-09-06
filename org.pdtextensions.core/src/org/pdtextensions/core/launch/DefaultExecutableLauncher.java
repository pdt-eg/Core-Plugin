/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Path;
import org.pdtextensions.core.exception.ExecutableNotFoundException;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.util.LaunchUtil;


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
	
	protected void doLaunch(String scriptPath, String[] arguments, ILaunchResponseHandler handler, boolean async) throws IOException, InterruptedException {
		
		String phpExe = null;
		
		try {
			Logger.debug("Getting default executable");
			phpExe = LaunchUtil.getPHPExecutable();
		} catch (ExecutableNotFoundException e) {
			Logger.debug("Error retrieving executable");
			Logger.logException(e);
			return;
		}
		
		String[] args = new String[arguments.length + 2];
		args[0] = phpExe;
		args[1] = scriptPath;
		
		Logger.debug("Launching workspace php executable: " + phpExe + " using target " + scriptPath);
		
		System.arraycopy(arguments, 0, args, 2, arguments.length);
		
		Runtime runtime = Runtime.getRuntime();
		Path path = new Path(scriptPath);
		Process p = runtime.exec(args, new String[]{}, new File(path.removeLastSegments(1).toOSString()));
		
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
