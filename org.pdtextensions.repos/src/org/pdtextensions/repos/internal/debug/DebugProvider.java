package org.pdtextensions.repos.internal.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.pdtextensions.repos.FindResult;
import org.pdtextensions.repos.Module;
import org.pdtextensions.repos.ModuleVersion;
import org.pdtextensions.repos.PEXReposPlugin;
import org.pdtextensions.repos.Vendor;
import org.pdtextensions.repos.api.IFindResult;
import org.pdtextensions.repos.api.IModule;
import org.pdtextensions.repos.api.IModuleVersion;
import org.pdtextensions.repos.api.IRepositoryProvider;
import org.pdtextensions.repos.api.IVendor;
import org.pdtextensions.repos.api.IVendorAwareProvider;

/**
 * A sample provider for debug usage
 * 
 * @author mepeisen
 */
public class DebugProvider implements IRepositoryProvider, IVendorAwareProvider {
	
	public static final String TYPE = "DEBUG";
	
	/** the dummy modules */
	private Map<String, IVendor> dummyModules = new HashMap<String, IVendor>();
	
	public DebugProvider() {
		// initialize dummies
		final Vendor vendor1 = new Vendor("org.vendor1", this, new IModule[]{
				new Module("foo-lib", "org.vendor1", this, new IModuleVersion[]{
						new ModuleVersion("org.vendor1",  "foo-lib", "0.9", true, (String) null, null, null),
						new ModuleVersion("org.vendor1",  "foo-lib", "1.0", true, (String) null, null, null),
						new ModuleVersion("org.vendor1",  "foo-lib", "1.1-SNAPSHOT", false, (String) null, null, null)
				}, "1.0", "1.1-SNAPSHOT")
		});
		final Vendor vendor2 = new Vendor("com.vendor2", this, new IModule[]{});
		this.dummyModules.put(vendor1.getName(), vendor1);
		this.dummyModules.put(vendor2.getName(), vendor2);
	}

	@Override
	public String getId() {
		return TYPE;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public IFindResult findModule(String vendor, String name, String version, IProgressMonitor monitor) {
		final String searchName = name == null ? "*" : name;
		final String searchVersion = version == null ? "*" : version;
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		
		try {
			for (final IVendor v : this.searchVendor(vendor, monitor)) {
				if (monitor.isCanceled()) {
					break;
				}
				final IFindResult findResult = v.findModule(searchName, searchVersion, monitor);
				// return the error if there is any
				if (!findResult.isOk()) {
					return findResult;
				}
				for (final IModuleVersion v2 : findResult.moduleVersions()) {
					result.add(v2);
				}
			}
		} catch (CoreException e) {
			result.clear();
			return new FindResult(new Status(IStatus.ERROR, PEXReposPlugin.PLUGIN_ID, "Failed searching for vendors", e), result);
		}
		
		return new FindResult(Status.OK_STATUS, result);
	}

	@Override
	public Iterable<IModule> listModules(IProgressMonitor monitor) throws CoreException {
		final List<IModule> result = new ArrayList<IModule>();
		for (final IVendor vendor : this.dummyModules.values()) {
			if (monitor.isCanceled()) {
				break;
			}
			for (final IModule module : vendor.listModules(monitor)) {
				result.add(module);
			}
		}
		return result;
	}

	@Override
	public Iterable<IVendor> getVendors(IProgressMonitor monitor) throws CoreException {
		return new ArrayList<IVendor>(this.dummyModules.values());
	}

	@Override
	public Iterable<IVendor> searchVendor(String name, IProgressMonitor monitor) throws CoreException {
		final String searchVendor = PEXReposPlugin.maskSearchStringToRegexp(name);
		return searchVendorRegex(searchVendor, monitor);
	}

	@Override
	public boolean supportsRegexSearch() {
		return true;
	}

	@Override
	public List<IVendor> searchVendorRegex(String name, IProgressMonitor monitor) throws CoreException {
		final Pattern patternVendor = Pattern.compile(name);
		final List<IVendor> result = new ArrayList<IVendor>();
		
		for (final Map.Entry<String, IVendor> entry : this.dummyModules.entrySet()) {
			if (patternVendor.matcher(entry.getKey().toLowerCase()).matches()) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	@Override
	public String getUri() {
		return TYPE;
	}

	@Override
	public boolean supportsDependencies() {
		return false;
	}

}
