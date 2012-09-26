/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.repos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.pdtextensions.repos.api.IDependency;
import org.pdtextensions.repos.api.IDownloadFileCallback;
import org.pdtextensions.repos.api.IDownloadStreamCallback;
import org.pdtextensions.repos.api.IModuleVersion;

/**
 * Default implementation for module versions that already know their files.
 * 
 * @author mepeisen
 */
public class ModuleVersion implements IModuleVersion {
	
	private String moduleName;
	private String vendorName;
	private String name;
	private boolean isRelease;
	
	private Map<String, IFileDownload> files = new HashMap<String, IFileDownload>();
	
	private String primaryFile;
	
	private List<IDependency> dependencies;
	private List<IDependency> deepDependencies;
	
	private IDependencyLoader dependencyLoader;
	
	/**
	 * Constructor for implementations that are knowing their files
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 * @param files
	 * @param primaryFileName may be null if there are no files
	 * @param depLoader helper to load the dependencies; maybe null if no dependencies are available
	 */
	public ModuleVersion(String vendor, String module, String name, boolean isRelease, Map<String, IFile> files, String primaryFileName, IDependencyLoader depLoader) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.name = name;
		this.isRelease = isRelease;
		this.files = new HashMap<String, IFileDownload>();
		for (final Map.Entry<String, IFile> entry : files.entrySet()) {
			this.files.put(entry.getKey(), new FileWrapper(entry.getValue()));
		}
		this.primaryFile = primaryFileName;
		this.dependencyLoader = depLoader;
	}
	
	/**
	 * Constructor for implementations that are knowing their files
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 * @param primaryFileName may be null if there are no files
	 * @param primaryFile may be null if there are no files
	 * @param depLoader helper to load the dependencies; maybe null if no dependencies are available
	 */
	public ModuleVersion(String vendor, String module, String name, boolean isRelease, String primaryFileName, IFile primaryFile, IDependencyLoader depLoader) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.isRelease = isRelease;
		this.files = new HashMap<String, IFileDownload>();
		if (primaryFileName != null) {
			this.files.put(primaryFileName, new FileWrapper(primaryFile));
		}
		this.primaryFile = primaryFileName;
		this.dependencyLoader = depLoader;
	}
	
	/**
	 * Constructor for implementations that are aware or loading the files on demand
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 * @param depLoader helper to load the dependencies; maybe null if no dependencies are available
	 */
	protected ModuleVersion(String vendor, String module, String name, boolean isRelease, IDependencyLoader depLoader) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.name = name;
		this.isRelease = isRelease;
		this.dependencyLoader = depLoader;
	}
	
	/**
	 * Method to load the files; must be overwritten by implementations that are aware of loading the files on demand
	 * @return files map; must not be null
	 * @throws CoreException thrown on errors.
	 */
	protected Map<String, IFileDownload> loadFiles() throws CoreException {
		return null;
	}
	
	/**
	 * Init the modules
	 * @throws CoreException thrown on errors.
	 */
	private void init() throws CoreException {
		if (this.files == null) {
			this.files = new HashMap<String, IFileDownload>();
			this.files = this.loadFiles();
			this.primaryFile = this.loadPrimaryFile();
		}
	}

	/**
	 * Calculates the name of the primary file
	 * @return primary file name or {@code null} if there is no primary file
	 * @throws CoreException thrown on errors
	 */
	protected String loadPrimaryFile() throws CoreException {
		return null;
	}

	@Override
	public String getModuleName() {
		return this.moduleName;
	}

	@Override
	public String getVendorName() {
		return this.vendorName;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isRelease() {
		return this.isRelease;
	}

	@Override
	public boolean isDevelopment() {
		return !this.isRelease;
	}

	@Override
	public Iterable<String> getFiles(IProgressMonitor monitor) throws CoreException  {
		this.init();
		return new ArrayList<String>(this.files.keySet());
	}

	@Override
	public String getPrimaryFile(IProgressMonitor monitor) throws CoreException  {
		this.init();
		return this.primaryFile;
	}

	@Override
	public IFile download(String name, boolean useCache,
			IProgressMonitor monitor) throws CoreException {
		this.init();
		final IFileDownload download = this.files.get(name);
		if (download != null) {
			return download.download(useCache, monitor);
		}
		return null;
	}

	@Override
	public InputStream openStream(String name, boolean useCache,
			IProgressMonitor monitor) throws CoreException {
		this.init();
		final IFileDownload download = this.files.get(name);
		if (download != null) {
			return download.openStream(useCache, monitor);
		}
		return null;
	}
	
	public interface IFileDownload {
		IFile download(boolean useCache, IProgressMonitor monitor) throws CoreException;
		InputStream openStream(boolean useCache, IProgressMonitor monitor) throws CoreException;
	}
	
	public static final class FileWrapper implements IFileDownload {
		
		private IFile file;
		
		public FileWrapper(IFile file) {
			this.file = file;
		}

		@Override
		public IFile download(boolean useCache, IProgressMonitor monitor) throws CoreException {
			return file;
		}

		@Override
		public InputStream openStream(boolean useCache, IProgressMonitor monitor)
				throws CoreException {
			return file.getContents();
		}
		
	}
	
	/**
	 * Dependency loader.
	 */
	public interface IDependencyLoader {
		
		/**
		 * Loads direct dependencies
		 * @param monitor progress monitor
		 * @return result; should be null if the monitor was canceled.
		 * @throws CoreException
		 */
		List<IDependency> loadDependencies(IProgressMonitor monitor) throws CoreException;
		
		/**
		 * Loads deep dependencies
		 * @param monitor progress monitor
		 * @return result; should be null if the monitor was canceled.
		 * @throws CoreException
		 */
		List<IDependency> loadDeepDependencies(IProgressMonitor monitor) throws CoreException;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<IDependency> getDependencies(boolean deep,
			IProgressMonitor monitor) throws CoreException {
		if (deep) {
			if (this.deepDependencies == null) {
				if (this.dependencyLoader == null) {
					return Collections.EMPTY_LIST;
				}
				this.deepDependencies = this.dependencyLoader.loadDeepDependencies(monitor);
			}
			return Collections.unmodifiableList(this.deepDependencies);
		}
		
		if (this.dependencies == null) {
			if (this.dependencyLoader == null) {
				return Collections.EMPTY_LIST;
			}
			this.dependencies = this.dependencyLoader.loadDependencies(monitor);
		}
		return Collections.unmodifiableList(this.dependencies);
	}

	@Override
	public void downloadDependencies(boolean deep, boolean useCache,
			IProgressMonitor monitor, IDownloadFileCallback callback)
			throws CoreException {
		for (final IDependency dep : this.getDependencies(deep, monitor)) {
			if (monitor.isCanceled()) {
				return;
			}
			callback.onDependency(dep.download(useCache, monitor), this, dep, monitor);
		}
	}

	@Override
	public void openDependenciesStream(boolean deep, boolean useCache,
			IProgressMonitor monitor, IDownloadStreamCallback callback)
			throws CoreException {
		for (final IDependency dep : this.getDependencies(deep, monitor)) {
			if (monitor.isCanceled()) {
				return;
			}
			callback.onDependency(dep.openStream(useCache, monitor), this, dep, monitor);
		}
	}

}
