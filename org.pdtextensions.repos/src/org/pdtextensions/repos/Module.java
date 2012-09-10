package org.pdtextensions.repos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pdtextensions.repos.api.IFindResult;
import org.pdtextensions.repos.api.IModule;
import org.pdtextensions.repos.api.IModuleVersion;
import org.pdtextensions.repos.api.IRepositoryProvider;

/**
 * Default implementation of a module that knows its versions.
 * 
 * @author mepeisen
 */
public class Module implements IModule {
	
	private String name;
	
	private String vendor;
	
	private IRepositoryProvider provider;
	
	private Map<String, IModuleVersion> versions = new HashMap<String, IModuleVersion>();
	
	private IModuleVersion newestRelease;
	
	private IModuleVersion newestSnapshot;
	
	/**
	 * Constructor for implementations that know their versions
	 * @param name
	 * @param vendor
	 * @param provider
	 * @param versions
	 * @param newestRelease
	 * @param newestSnapshot
	 */
	public Module(String name, String vendor, IRepositoryProvider provider, IModuleVersion[] versions, String newestRelease, String newestSnapshot) {
		this.name = name;
		this.vendor = vendor;
		this.provider = provider;
		this.versions = new HashMap<String, IModuleVersion>();
		for (final IModuleVersion version : versions) {
			this.versions.put(version.getName().toLowerCase(), version);
		}
		if (newestRelease != null) {
			this.newestRelease = this.versions.get(newestRelease.toLowerCase());
		}
		if (newestSnapshot != null) {
			this.newestSnapshot = this.versions.get(newestSnapshot.toLowerCase());
		}
	}
	
	/**
	 * Constructor for implementations that are able to load their versions on demand
	 * @param name
	 * @param vendor
	 * @param provider
	 */
	protected Module(String name, String vendor, IRepositoryProvider provider) {
		this.name = name;
		this.vendor = vendor;
		this.provider = provider;
	}
	
	/**
	 * Method to load versions; must be overwritten by implementations that are aware of loading the versions on demand
	 * @return versions map; must not be null; key must be lower cased
	 * @throws CoreException thrown on errors.
	 */
	protected Map<String, IModuleVersion> loadVersions() throws CoreException {
		return null;
	}
	
	/**
	 * Method to load versions; must be overwritten by implementations that are aware of loading the versions on demand
	 * @return the name of the newest release version or null if there is no release version
	 * @throws CoreException thrown on errors.
	 */
	protected String loadNewestReleaseVersion() throws CoreException {
		return null;
	}
	
	/**
	 * Method to load versions; must be overwritten by implementations that are aware of loading the versions on demand
	 * @return the name of the newest dev version or null if there is no dev version
	 * @throws CoreException thrown on errors.
	 */
	protected String loadNewestSnapshotVersion() throws CoreException {
		return null;
	}
	
	/**
	 * Init the modules
	 * @throws CoreException thrown on errors.
	 */
	private void init() throws CoreException {
		if (this.versions == null) {
			this.versions = this.loadVersions();
			final String devV = this.loadNewestSnapshotVersion();
			final String relV = this.loadNewestReleaseVersion();
			if (devV != null) {
				this.newestSnapshot = this.versions.get(devV.toLowerCase());
			}
			if (relV != null) {
				this.newestRelease = this.versions.get(relV.toLowerCase());
			}
		}
	}
	
	@Override
	public IRepositoryProvider getProvider() {
		return this.provider;
	}

	@Override
	public String getVendorName() {
		return this.vendor;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFindResult findVersion(String version) {
		try {
			this.init();
		} catch (CoreException e) {
			return new FindResult(new Status(IStatus.ERROR, PEXReposPlugin.PLUGIN_ID, "error loading versions", e), Collections.EMPTY_LIST);
		}
		final String searchVersion = version == null ? ".*" : PEXReposPlugin.maskSearchStringToRegexp(version);
		final Pattern patternVersion = Pattern.compile(searchVersion);
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		for (final Map.Entry<String, IModuleVersion> v : this.versions.entrySet()) {
			if (patternVersion.matcher(v.getKey()).matches()) {
				result.add(v.getValue());
			}
		}
		return new FindResult(Status.OK_STATUS, result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFindResult findReleaseVersion(String version) {
		try {
			this.init();
		} catch (CoreException e) {
			return new FindResult(new Status(IStatus.ERROR, PEXReposPlugin.PLUGIN_ID, "error loading versions", e), Collections.EMPTY_LIST);
		}
		final String searchVersion = version == null ? ".*" : PEXReposPlugin.maskSearchStringToRegexp(version);
		final Pattern patternVersion = Pattern.compile(searchVersion);
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		for (final Map.Entry<String, IModuleVersion> v : this.versions.entrySet()) {
			if (patternVersion.matcher(v.getKey()).matches() && v.getValue().isDevelopment()) {
				result.add(v.getValue());
			}
		}
		return new FindResult(Status.OK_STATUS, result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFindResult findDevVersion(String version) {
		try {
			this.init();
		} catch (CoreException e) {
			return new FindResult(new Status(IStatus.ERROR, PEXReposPlugin.PLUGIN_ID, "error loading versions", e), Collections.EMPTY_LIST);
		}
		final String searchVersion = version == null ? ".*" : PEXReposPlugin.maskSearchStringToRegexp(version);
		final Pattern patternVersion = Pattern.compile(searchVersion);
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		for (final Map.Entry<String, IModuleVersion> v : this.versions.entrySet()) {
			if (patternVersion.matcher(v.getKey()).matches() && v.getValue().isRelease()) {
				result.add(v.getValue());
			}
		}
		return new FindResult(Status.OK_STATUS, result);
	}

	@Override
	public Iterable<IModuleVersion> listReleaseVersions() throws CoreException {
		this.init();
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		for (final IModuleVersion v : this.versions.values()) {
			if (v.isRelease()) {
				result.add(v);
			}
		}
		return result;
	}

	@Override
	public Iterable<IModuleVersion> listDevVersions() throws CoreException {
		this.init();
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		for (final IModuleVersion v : this.versions.values()) {
			if (v.isDevelopment()) {
				result.add(v);
			}
		}
		return result;
	}

	@Override
	public Iterable<IModuleVersion> listVersions() throws CoreException {
		this.init();
		return new ArrayList<IModuleVersion>(this.versions.values());
	}

	@Override
	public IModuleVersion getNewestReleaseVersion() throws CoreException {
		this.init();
		return this.newestRelease;
	}

	@Override
	public IModuleVersion getNewestDevVersion() throws CoreException {
		this.init();
		return this.newestSnapshot;
	}

}
