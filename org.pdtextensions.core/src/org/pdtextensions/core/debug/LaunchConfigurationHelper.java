package org.pdtextensions.core.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.server.core.Server;
import org.pdtextensions.core.log.Logger;

/**
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class LaunchConfigurationHelper {
	
	/**
	 * @param project
	 * @param server
	 * @param file
	 * @return
	 * @throws CoreException
	 */
    public static ILaunchConfiguration createLaunchConfiguration(IProject project, Server server, IFile file) throws CoreException {
    	
        ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType  configType = lm.getLaunchConfigurationType(IPHPDebugConstants.PHPServerLaunchType);
    	
    	if (!file.exists()) {
            Logger.debugMSG("File does not exist, cannot create launch configuration: " + file.toString());
            return null;
        }
        
        ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, project.getName());
        String debuggerID = PHPProjectPreferences.getDefaultDebuggerID(project);
        String URL = server.getBaseURL() + "/app_dev.php";
        AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry.getDebuggerConfiguration(debuggerID);
        
        wc.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, debuggerID);
        wc.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, debuggerConfiguration.getWebLaunchDelegateClass());
        wc.setAttribute(Server.NAME, server.getName());
        wc.setAttribute(Server.FILE_NAME, file.getFullPath().toOSString());
        wc.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
        wc.setAttribute(IPHPDebugConstants.OPEN_IN_BROWSER, PHPDebugPlugin.getOpenInBrowserOption());
        wc.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, false);
        wc.setAttribute(Server.BASE_URL, URL);
        wc.setAttribute("auto_generated_url", false);
        
        return wc.doSave();
    }
}
