/*******************************************************************************
 * Copyright (c) 2012, 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.util;

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.eclipse.php.internal.debug.core.xdebug.communication.XDebugCommunicationDaemon;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.pdtextensions.core.exception.ExecutableNotFoundException;


@SuppressWarnings("restriction")
public class LaunchUtil {
	
	public static String getPHPExecutable() throws ExecutableNotFoundException
	{
		return getPHPExecutable("");
	}
	
	public static String getPHPExecutable(String debugger) throws ExecutableNotFoundException {
		// find the default PHP executable
		PHPexeItem defaultPhpExe = getDefaultPHPExeItem(debugger);
		
		// check if the SAPI type is CLI
		if (PHPexeItem.SAPI_CLI.equals(defaultPhpExe.getSapiType())) {
			// if yes - return it 
			return defaultPhpExe.getExecutable().toString();
		}
		
		// otherwise try to find a PHP CLI executable
		PHPexeItem[] cliItems = PHPexes.getInstance().getCLIItems();
		if (cliItems.length == 0) {
			// if no PHP CLI executable then return the default one
			return defaultPhpExe.getExecutable().toString();
		}
		
		// sort the PHP CLI executable by version
		SortedMap<String, PHPexeItem> map = new TreeMap<String, PHPexeItem>();
		for (PHPexeItem item : cliItems) {
			map.put(item.getVersion(), item);
		}
		
		// check if there is a PHP CLI executable with the same version as the default PHP executable
		PHPexeItem phpExe = map.get(defaultPhpExe.getVersion());
		if (phpExe != null) {
			return phpExe.getExecutable().toString();
		}
		
		// otherwise return the PHP CLI executable with the greatest version
		phpExe = map.get(map.lastKey());
		return phpExe.getExecutable().toString();
	}
	
	private static PHPexeItem getDefaultPHPExeItem(String debugger) throws ExecutableNotFoundException {
		PHPexeItem phpExe = PHPexes.getInstance().getDefaultItem();
		
		if (phpExe != null) {
			return phpExe;
		}

		throw new ExecutableNotFoundException("Unable to find PHP executable");
	}
}
