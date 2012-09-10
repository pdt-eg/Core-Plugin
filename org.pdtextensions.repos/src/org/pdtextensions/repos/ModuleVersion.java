package org.pdtextensions.repos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.pdtextensions.repos.api.IModuleVersion;

/**
 * Default implementation for module versions taht already know their files.
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
	
	/**
	 * Constructor for implementations that are knowing their files
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 * @param files
	 * @param primaryFileName may be null if there are no files
	 */
	public ModuleVersion(String vendor, String module, String name, boolean isRelease, Map<String, IFile> files, String primaryFileName) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.name = name;
		this.isRelease = isRelease;
		this.files = new HashMap<String, IFileDownload>();
		for (final Map.Entry<String, IFile> entry : files.entrySet()) {
			this.files.put(entry.getKey(), new FileWrapper(entry.getValue()));
		}
		this.primaryFile = primaryFileName;
	}
	
	/**
	 * Constructor for implementations that are knowing their files
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 * @param primaryFileName may be null if there are no files
	 * @param primaryFile may be null if there are no files
	 */
	public ModuleVersion(String vendor, String module, String name, boolean isRelease, String primaryFileName, IFile primaryFile) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.isRelease = isRelease;
		this.files = new HashMap<String, IFileDownload>();
		if (primaryFileName != null) {
			this.files.put(primaryFileName, new FileWrapper(primaryFile));
		}
		this.primaryFile = primaryFileName;
	}
	
	/**
	 * Constructor for implementations that are aware or loading the files on demand
	 * @param vendor 
	 * @param module
	 * @param name
	 * @param isRelease
	 */
	protected ModuleVersion(String vendor, String module, String name, boolean isRelease) {
		this.moduleName = module;
		this.vendorName = vendor;
		this.name = name;
		this.isRelease = isRelease;
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
	public Iterable<String> getFiles() throws CoreException  {
		this.init();
		return new ArrayList<String>(this.files.keySet());
	}

	@Override
	public String getPrimaryFile() throws CoreException  {
		this.init();
		return this.primaryFile;
	}

	@Override
	public IFile download(String name, boolean useCache,
			IProgressMonitor monitor) throws CoreException {
		this.init();
		final IFileDownload download = this.files.get(name);
		if (download != null) {
			download.download(useCache, monitor);
		}
		return null;
	}
	
	public interface IFileDownload {
		IFile download(boolean useCache, IProgressMonitor monitor)throws CoreException ;
	}
	
	protected static final class FileWrapper implements IFileDownload {
		
		private IFile file;
		
		public FileWrapper(IFile file) {
			this.file = file;
		}

		@Override
		public IFile download(boolean useCache, IProgressMonitor monitor) throws CoreException {
			return file;
		}
		
	}

}
