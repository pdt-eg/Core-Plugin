package org.pdtextensions.server.internal.lhttpd;


public class Win32Installable extends InstallableRuntime2 {

	@Override
	public String getId() {
		return "org.pdtextensions.server.lhttpd.LinuxInstallable"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return Messages.Win32Installable_Xampp180WindowsName;
	}

	@Override
	public String getArchiveUrl() {
		return "http://www.apachefriends.org/download.php?xampp-win32-1.8.0-VC9.zip"; //$NON-NLS-1$
	}

	@Override
	public String getArchivePath() {
		return "xampp"; //$NON-NLS-1$
	}

	@Override
	public String getLicenseURL() {
		return "http://www.gnu.org/licenses/gpl-2.0.txt"; //$NON-NLS-1$
	}

	@Override
	public int getArchiveSize() {
		return 174487038;
	}

	@Override
	public int getFileCount() {
		return 18892;
	}

}
