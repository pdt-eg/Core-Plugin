package org.pex.core.launch;

public class ConsoleResponseHandler implements ILaunchResponseHandler {

	@Override
	public void handle(String response) {
		//TODO: log to eclipse console
		System.err.println(response);
	}
}
