package org.pdtextensions.core.ui.exception;

public class RefactoringStatusException extends Exception {
	
	private String fStatusMessage;

	public RefactoringStatusException(String statusMessage)
	{
		fStatusMessage = statusMessage;
	}
	
	public String getStatusMessage()
	{
		return fStatusMessage;
	}
	
}
