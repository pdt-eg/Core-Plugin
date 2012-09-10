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
import org.pdtextensions.repos.api.IVendor;
import org.pdtextensions.repos.api.IVendorAwareProvider;

/**
 * Default implementation of a vendor that already knows the modules or is able to fetch them on demand.
 * 
 * @author mepeisen
 */
public class Vendor implements IVendor {
	
	private Map<String, IModule> modules;
	private String name;
	private IVendorAwareProvider provider;
	
	/**
	 * Constructor for implementations that are aware or loading the modules on demand
	 * @param name
	 * @param provider
	 */
	protected Vendor(String name, IVendorAwareProvider provider) {
		this.name = name;
		this.provider = provider;
	}
	
	/**
	 * Constructor for implementations that are knowing their modules 
	 * @param name
	 * @param provider
	 * @param modules
	 */
	public Vendor(String name, IVendorAwareProvider provider, IModule[] modules) {
		this.name = name;
		this.provider = provider;
		this.modules = new HashMap<String, IModule>();
		for (final IModule module : modules) {
			this.modules.put(module.getName().toLowerCase(), module);
		}
	}
	
	/**
	 * Method to load modules; must be overwritten by implementations that are aware of loading the modules on demand
	 * @return modules map; must not be null; key must be lower cased
	 * @throws CoreException thrown on errors.
	 */
	protected Map<String, IModule> loadModules() throws CoreException {
		return null;
	}
	
	/**
	 * Init the modules
	 * @throws CoreException thrown on errors.
	 */
	private void init() throws CoreException {
		if (this.modules == null) {
			this.modules = this.loadModules();
		}
	}

	@Override
	public IVendorAwareProvider getProvider() {
		return this.provider;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFindResult findModule(String name, String version) {
		try {
			this.init();
		} catch (CoreException e) {
			return new FindResult(new Status(IStatus.ERROR, PEXReposPlugin.PLUGIN_ID, "error loading modules", e), Collections.EMPTY_LIST);
		}
		final String searchName = name == null ? ".*" : PEXReposPlugin.maskSearchStringToRegexp(name);
		final String searchVersion = version == null ? "*" : version;
		final Pattern patternName = Pattern.compile(searchName);
		final List<IModuleVersion> result = new ArrayList<IModuleVersion>();
		
		for (final Map.Entry<String, IModule> module : this.modules.entrySet()) {
			if (patternName.matcher(module.getKey().toLowerCase()).matches()) {
				final IFindResult findResult = module.getValue().findVersion(searchVersion);
				// return the error if there is any
				if (!findResult.isOk()) {
					return findResult;
				}
				for (final IModuleVersion v : findResult.moduleVersions()) {
					result.add(v);
				}
			}
		}
		
		return new FindResult(Status.OK_STATUS, result);
	}

	@Override
	public Iterable<IModule> listModules() throws CoreException {
		this.init();
		return new ArrayList<IModule>(modules.values());
	}

}
