package org.pdtextensions.core.launch;

/**
 * Thrown if a PHP script to be executed could not be found. Use this to trigger 
 * automatic download of non-existing scripts/phars. 
 *
 */
public class ScriptNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ScriptNotFoundException(String message) {
		super(message);
	}
}
