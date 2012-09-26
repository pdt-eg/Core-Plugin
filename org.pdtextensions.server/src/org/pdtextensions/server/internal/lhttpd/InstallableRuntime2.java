/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.pdtextensions.server.internal.lhttpd;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.core.helpers.TarEntry;
import org.eclipse.equinox.internal.p2.core.helpers.TarException;
import org.eclipse.equinox.internal.p2.core.helpers.TarInputStream;
import org.eclipse.osgi.util.NLS;
import org.pdtextensions.server.IPEXInstallableRuntime;
import org.pdtextensions.server.PEXServerPlugin;

/**
 * mostly taken from wst
 * 
 */
@SuppressWarnings("restriction")
public abstract class InstallableRuntime2 implements IPEXInstallableRuntime {
	private byte[] BUFFER = null;

	// Default sizes (infinite logarithmic progress will be used when default is employed)
	private int DEFAULT_DOWNLOAD_SIZE = 10000000;
	private int DEFAULT_FILE_COUNT = 1000;

	public abstract String getArchiveUrl();

	public abstract String getArchivePath();

	public int getArchiveSize() {
		return -1;
	}

	public int getFileCount() {
		return -1;
	}

	public abstract String getLicenseURL();

	/*
	 * @see IInstallableRuntime#getLicense(IProgressMonitor)
	 */
	public String getLicense(IProgressMonitor monitor) throws CoreException {
		URL url = null;
		ByteArrayOutputStream out = null;
		try {
			String licenseURL = getLicenseURL();
			if (licenseURL == null)
				return null;
			
			url = new URL(licenseURL);
			InputStream in = url.openStream();
			out = new ByteArrayOutputStream();
			copyWithSize(in, out, null, 0);
			return new String(out.toByteArray());
		} catch (Exception e) {
//			if (Trace.WARNING) {
//				Trace.trace(Trace.STRING_WARNING, "Error loading license", e);
//			}
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, 0,
					NLS.bind(Messages.InstallableRuntime2_ErrorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/*
	 * @see IInstallableRuntime#install(IPath)
	 */
	public void install(final IPath path) {
		Job installRuntimeJob = new Job(NLS.bind(Messages.InstallableRuntime2_TaskInstallRuntime, this.getName())) {
			public boolean belongsTo(Object family) {
				return PEXServerPlugin.PLUGIN_ID.equals(family);
			}
			
			protected IStatus run(IProgressMonitor monitor) {
				try {
					install(path, monitor);
				} catch (CoreException ce) {
					return ce.getStatus();
				}
				
				return Status.OK_STATUS;
			}
		};
		
		installRuntimeJob.schedule();
	}

	private void copyWithSize(InputStream in, OutputStream out, IProgressMonitor monitor, int size) throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		SubMonitor progress = SubMonitor.convert(monitor, size);
		int r = in.read(BUFFER);
		while (r >= 0) {
			out.write(BUFFER, 0, r);
			progress.worked(r);
			r = in.read(BUFFER);
		}
	}

	private void download(InputStream in, OutputStream out, IProgressMonitor monitor, String name, int size) throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		
		String msg = NLS.bind((size > 0) ? Messages.InstallableRuntime2_DownloadKnownSize : Messages.InstallableRuntime2_DownloadUnknownSize,
				new Object [] { name, "{0}", Integer.toString(size / 1024) }); //$NON-NLS-1$
		SubMonitor progress = SubMonitor.convert(monitor, NLS.bind(msg, "0"), (size > 0) ? size : DEFAULT_DOWNLOAD_SIZE); //$NON-NLS-1$
		
		int r = in.read(BUFFER);
		int total = 0;
		int lastTotal = 0;
		while (r >= 0) {
			out.write(BUFFER, 0, r);
			total += r;
			if (total >= lastTotal + 8192) {
				lastTotal = total;
				progress.subTask(NLS.bind(msg, Integer.toString(lastTotal / 1024)));
			}
			progress.worked(r);
			// if size is not known, use infinite logarithmic progress
			if (size <= 0)
				progress.setWorkRemaining(DEFAULT_DOWNLOAD_SIZE);
			
			if (progress.isCanceled())
				break;
			r = in.read(BUFFER);
		}
	}

	/*
	 * @see IInstallableRuntime#install(IPath, IProgressMonitor)
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 1000);
		URL url = null;
		File temp = null;
		try {
			url = new URL(getArchiveUrl());
			temp = File.createTempFile("runtime", ""); //$NON-NLS-1$ //$NON-NLS-2$
			temp.deleteOnExit();
		} catch (IOException e) {
			if (monitor != null)
				monitor.done();
//			if (Trace.WARNING) {
//				Trace.trace(Trace.STRING_WARNING, "Error creating url and temp file", e);
//			}
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.InstallableRuntime2_ErrorInstallingServer, e.getLocalizedMessage()), e));
		}
		String name = url.getQuery();
//		int slashIdx = name.lastIndexOf('/');
//		if (slashIdx >= 0)
//			name = name.substring(slashIdx + 1);
		
		int archiveSize = getArchiveSize();
		
		// download
		FileOutputStream fout = null;
		try {
			InputStream in = url.openStream();
			fout = new FileOutputStream(temp);
			download(in, fout, progress.newChild(500), name, archiveSize);
			progress.setWorkRemaining(500);
		} catch (Exception e) {
			if (monitor != null)
				monitor.done();
//			if (Trace.WARNING) {
//				Trace.trace(Trace.STRING_WARNING, "Error downloading runtime", e);
//			}
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.InstallableRuntime2_ErrorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				// ignore
			}
		}
		if (progress.isCanceled())
			throw new CoreException(Status.CANCEL_STATUS);
		
		FileInputStream in = null;
		try {
			in = new FileInputStream(temp);
			if (name.endsWith("zip")) //$NON-NLS-1$
				unzip(in, path, progress.newChild(500));
			else if (name.endsWith("tar")) //$NON-NLS-1$
				untar(in, path, progress.newChild(500));
			else if (name.endsWith("tar.gz")) { //$NON-NLS-1$
				File tarFile = File.createTempFile("runtime", ".tar"); //$NON-NLS-1$ //$NON-NLS-2$
				tarFile.deleteOnExit();
				String tarName = name;
				
				progress.subTask(NLS.bind(Messages.InstallableRuntime2_TaskUncompressing, tarName));
				int tempSize = Integer.MAX_VALUE;
				if (temp.length() < Integer.MAX_VALUE)
					tempSize = (int)temp.length();
				
				ungzip(in, tarFile, progress.newChild(250), tempSize);
				progress.setWorkRemaining(250);
				if (!progress.isCanceled()) {
					in = new FileInputStream(tarFile);
					untar(in, path, progress.newChild(250));
				}
			}
		} catch (Exception e) {
//			if (Trace.SEVERE) {
//				Trace.trace(Trace.STRING_SEVERE, "Error uncompressing runtime", e);
//			}
			throw new CoreException(new Status(IStatus.ERROR, PEXServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.InstallableRuntime2_ErrorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				// ignore
			}
			progress.done();
		}
	}

	/**
	 * Unzip the input stream into the given path.
	 * 
	 * @param in
	 * @param path
	 * @param monitor
	 * @throws IOException
	 */
	private void unzip(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		int fileCnt = getFileCount();
		SubMonitor progress = SubMonitor.convert(monitor, (fileCnt > 0) ? fileCnt : DEFAULT_FILE_COUNT);
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		ZipInputStream zin = new ZipInputStream(bin);
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			progress.subTask(NLS.bind(Messages.InstallableRuntime2_TaskUncompressing, name));
			if (archivePath != null && name.startsWith(archivePath)) {
				name = name.substring(archivePath.length());
				if (name.length() > 1)
					name = name.substring(1);
			}
			
			if (name != null && name.length() > 0) {
				if (entry.isDirectory())
					path.append(name).toFile().mkdirs();
				else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copyWithSize(zin, fout, progress.newChild(1), (int)entry.getSize());
					fout.close();
					// if count is not known, use infinite logarithmic progress
					if (fileCnt <= 0)
						progress.setWorkRemaining(DEFAULT_FILE_COUNT);
				}
			}
			zin.closeEntry();
			entry = zin.getNextEntry();
		}
		zin.close();
	}

	/**
	 * Untar the input stream into the given path.
	 * 
	 * @param in
	 * @param path
	 * @param monitor
	 * @throws IOException
	 */
	protected void untar(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		int fileCnt = getFileCount();
		SubMonitor progress = SubMonitor.convert(monitor, (fileCnt > 0) ? fileCnt : 500);
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		try {
			TarInputStream zin = new TarInputStream(bin);
			TarEntry entry = zin.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				progress.subTask(NLS.bind(Messages.InstallableRuntime2_TaskUncompressing, name));
				if (archivePath != null && name.startsWith(archivePath)) {
					name = name.substring(archivePath.length());
					if (name.length() > 1)
						name = name.substring(1);
				}
				
				if (name != null && name.length() > 0) {
					if (entry.getFileType() == TarEntry.DIRECTORY)
						path.append(name).toFile().mkdirs();
					else {
						File dir = path.append(name).removeLastSegments(1).toFile();
						if (!dir.exists())
							dir.mkdirs();
						
						FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
						copyWithSize(zin, fout, progress.newChild(1), (int)entry.getSize());
						fout.close();
						if (fileCnt <= 0)
							progress.setWorkRemaining(500);
					}
				}
				entry = zin.getNextEntry();
			}
			zin.close();
		} catch (TarException ex) {
			throw new IOException(ex);
		}
	}

	protected void ungzip(InputStream in, File tarFile, IProgressMonitor monitor, int size) throws IOException {
		GZIPInputStream gzin = null;
		FileOutputStream fout = null;
		try {
			gzin = new GZIPInputStream(in);
			fout = new FileOutputStream(tarFile);
			copyWithSize(gzin, fout, monitor, size);
		} finally {
			if (gzin != null) {
				try {
					gzin.close();
				} catch (IOException e) {
					// ignore
				}
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	public String toString() {
		return "InstallableRuntime2[" + getId() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}