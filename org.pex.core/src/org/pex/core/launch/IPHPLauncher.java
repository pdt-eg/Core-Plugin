package org.pex.core.launch;

import java.io.IOException;


public interface IPHPLauncher {

	void launch(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException;
	
	void launchAsync(String scriptPath, String[] arguments, ILaunchResponseHandler handler) throws IOException, InterruptedException;
}
