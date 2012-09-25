package org.pdtextensions.server.internal.lhttpd;


public class Win32Installable extends InstallableRuntime2 {

	@Override
	public String getId() {
		return "org.pdtextensions.server.lhttpd.LinuxInstallable";
	}

	@Override
	public String getName() {
		return "XAMPP 1.8.0 for Windows (VC9)";
	}

	@Override
	public String getArchiveUrl() {
		return "http://www.apachefriends.org/download.php?xampp-win32-1.8.0-VC9.zip";
	}

	@Override
	public String getArchivePath() {
		return "xampp";
	}

	@Override
	public String getLicenseURL() {
		return "http://www.gnu.org/licenses/gpl-2.0.txt";
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
