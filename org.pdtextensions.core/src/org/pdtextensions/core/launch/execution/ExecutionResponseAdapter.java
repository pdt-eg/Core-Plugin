package org.pdtextensions.core.launch.execution;

public class ExecutionResponseAdapter implements ExecutionResponseListener {

	@Override
	public void executionFinished(String response, int exitValue) {
	}

	@Override
	public void executionFailed(String response, Exception exception) {
	}

	@Override
	public void executionError(String message) {
	}

	@Override
	public void executionMessage(String message) {
	}

	@Override
	public void executionAboutToStart() {
	}

	@Override
	public void executionStarted() {
	}

}
