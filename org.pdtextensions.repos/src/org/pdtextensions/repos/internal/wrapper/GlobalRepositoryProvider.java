package org.pdtextensions.repos.internal.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.pdtextensions.repos.api.IFindResult;
import org.pdtextensions.repos.api.IModule;
import org.pdtextensions.repos.api.IRepositoryProvider;
import org.pdtextensions.repos.internal.storage.RepositoryStorage;

/**
 * The global repository provider wrapping access to all other repositories.
 * 
 * @author mepeisen
 */
public class GlobalRepositoryProvider implements IRepositoryProvider {
	
	private List<IRepositoryProvider> providers = new ArrayList<IRepositoryProvider>();
	
	public GlobalRepositoryProvider() {
		// TODO
	}

	@Override
	public IFindResult findModule(String vendor, String name, String version)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IModule> listModules() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "GLOBAL";
	}

	@Override
	public String getId() {
		return "GLOBAL";
	}

	public Iterable<IRepositoryProvider> getProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerProvider(IRepositoryProvider provider) {
		if ("GLOBAL".equals(provider.getType())) {
			// skip this hack
			throw new IllegalArgumentException("Cannot register global provider");
		}
		if ("DEBUG".equals(provider.getType())) {
			// skip this hack
			throw new IllegalArgumentException("Cannot register debug provider");
		}
		if (!provider.getId().startsWith("temp-")) {
			// skip this hack
			throw new IllegalArgumentException("You need to register temporary providers");
		}
		
		final String id = RepositoryStorage.instance().getNextId();
		// TODO
	}

	public void unregisterProvider(IRepositoryProvider provider) {
		RepositoryStorage.instance().removeProvider(provider.getId());
	}

}
