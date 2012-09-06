package org.pdtextensions.repos;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pdtextensions.repos.api.IRepositoryProvider;
import org.pdtextensions.repos.api.IRepositoryProviderFactory;

public class PEXReposPlugin implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PEXReposPlugin.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		PEXReposPlugin.context = null;
	}
	
	/**
	 * Returns a generic provider that is aware of accessing all other repositories.
	 * @return the generic provider to access all other repositories.
	 */
	public static IRepositoryProvider getProvider() {
		// TODO
		return null;
	}
	
	/**
	 * Returns all known providers.
	 * @return iterable for all known repository providers.
	 */
	public static Iterable<IRepositoryProvider> getProviders() {
		// TODO
		return null;
	}
	
	/**
	 * Creates a new temporary provider
	 * @param type repository implementation type; equals the extension type name
	 * @param uri the uri to access the repository (may contain options; see the documentation of the repository implementation)
	 * @return the repository provider
	 * @throws CoreException thrown on errors
	 */
	public static IRepositoryProvider createProvider(String type, String uri) throws CoreException {
		// TODO
		return null;
	}
	
	/**
	 * Registers a repository provider.
	 * @param provider the provider to be registered.
	 */
	public static void registerProvider(IRepositoryProvider provider) {
		// TODO
	}
	
	/**
	 * Unregisters the repository provider.
	 * @param provider the provider to be unregistered.
	 */
	public static void unregisterProvider(IRepositoryProvider provider) {
		// TODO
	}
	
	/**
	 * Returns the provider factories being able to create repositories of a specific type.
	 * @return The provider factory list
	 */
	public static Iterable<IRepositoryProviderFactory> getFactories() {
		// TODO
		return null;
	}
	
}
