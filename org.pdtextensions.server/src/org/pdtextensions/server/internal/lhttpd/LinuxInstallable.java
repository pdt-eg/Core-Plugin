package org.pdtextensions.server.internal.lhttpd;


/**
 * Linux unstallable
 * 
 * @author mepeisen
 */
public class LinuxInstallable extends InstallableRuntime2 {

	@Override
	public String getId() {
		return "org.pdtextensions.server.lhttpd.LinuxInstallable";
	}

	@Override
	public String getName() {
		return "XAMPP 1.8.0 for Linux";
	}

	@Override
	public String getArchiveUrl() {
		return "http://www.apachefriends.org/download.php?xampp-linux-1.8.0.tar.gz";
	}

	@Override
	public String getArchivePath() {
		return "lampp";
	}

	@Override
	public String getLicenseURL() {
		return "http://www.gnu.org/licenses/gpl-2.0.txt";
	}

	@Override
	public int getArchiveSize() {
		return 84706874;
	}

	@Override
	public int getFileCount() {
		return 8849;
	}

}
