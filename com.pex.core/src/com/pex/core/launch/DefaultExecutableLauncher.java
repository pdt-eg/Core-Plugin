package com.pex.core.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.pex.core.exception.ExecutableNotFoundException;
import com.pex.core.log.Logger;
import com.pex.core.util.LaunchUtil;

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
