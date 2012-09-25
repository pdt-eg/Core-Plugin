package org.pdtextensions.server.internal.lhttpd;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.pdtextensions.server.IPEXInstallableRuntime;
import org.pdtextensions.server.PEXServerPlugin;
import org.pdtextensions.server.lhttpd.ILHttpdRuntime;
import org.pdtextensions.server.lhttpd.ILHttpdRuntimeWorkingCopy;

/**
 * The local httpd runtime delegate
 * 
 * @author mepeisen
 */
public class LHttpdServerRuntime extends RuntimeDelegate implements ILHttpdRuntime, ILHttpdRuntimeWorkingCopy {
	
	private static final String[] HTTPD_EXECUTABLES = new String[] {
		// classic executable
		"httpd.exe", "bin/httpd.exe", "apache2ctl", "bin/apache2ctl", "apache2", "bin/apache2", "httpd", "bin/httpd"
		,
		// xampp directory layout
		"apache/bin/httpd.exe", "apache/bin/apache2ctl", "apache/bin/apache2", "apache/bin/httpd"
	};

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
		getRuntimeWorkingCopy().setLocation(new Path(PEXServerPlugin.getPreference("location" + type.getId())));
	}

	/**
	 * Searches and returns the exeuctable
	 * @return
	 */
	protected File getExecutable() {
		final IPath location = getRuntime().getLocation();
		final File locationPath = location.toFile();
		for (final String ex : HTTPD_EXECUTABLES) {
			final File exe = new File(locationPath, ex);
			if (exe.isFile() && exe.exists()) {
				return exe;
			}
		}
		return null;
	}
	
	protected IStatus verifyLocation() {
		if (getExecutable() == null) {
			return new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, "Installation directory invalid; httpd executable not found.");
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;
		
		status = verifyLocation();
		if (!status.isOK())
			return status;
		
		return Status.OK_STATUS;
	}

	@Override
	public IPEXInstallableRuntime getInstallableRuntime() {
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			return new Win32Installable();
		}
		if (Platform.OS_LINUX.equals(Platform.getOS())) {
			return new LinuxInstallable();
		}
		return null;
	}

	
	
}
