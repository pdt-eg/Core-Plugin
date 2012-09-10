package org.pdtextensions.repos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pdtextensions.repos.api.IRepositoryProvider;
import org.pdtextensions.repos.api.IRepositoryProviderFactory;
import org.pdtextensions.repos.internal.wrapper.GlobalRepositoryProvider;

public class PEXReposPlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.pdtextensions.repos";

	private static PEXReposPlugin plugin;

	private static BundleContext context;
	
	/**
	 * The factories
	 */
	private static List<IRepositoryProviderFactory> FACTORIES;
	
	private static GlobalRepositoryProvider GLOBAL_PROVIDER;
	
	private static int nextId = 0;

	static BundleContext getContext() {
		return context;
	}
	
	public static PEXReposPlugin getDefault() {
		return plugin;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PEXReposPlugin.context = bundleContext;
		plugin = this;
		init();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		FACTORIES = null;
		plugin = null;
		GLOBAL_PROVIDER = null;
		PEXReposPlugin.context = null;
	}
	
	private static void init() {
		if (FACTORIES == null) {
			FACTORIES = new ArrayList<IRepositoryProviderFactory>();
			final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.pdtextensions.repos.factories");
			for (final IConfigurationElement element : point.getConfigurationElements()) {
				try {
					FACTORIES.add((IRepositoryProviderFactory) element.createExecutableExtension("class"));
				} catch (CoreException e) {
					// TODO logging
				}
			}
			GLOBAL_PROVIDER = new GlobalRepositoryProvider();
		}
	}
	
	/**
	 * Returns a generic provider that is aware of accessing all other repositories.
	 * @return the generic provider to access all other repositories.
	 */
	public static IRepositoryProvider getProvider() {
		init();
		return GLOBAL_PROVIDER;
	}
	
	/**
	 * Returns all known providers.
	 * @return iterable for all known repository providers.
	 */
	public static Iterable<IRepositoryProvider> getProviders() {
		init();
		return GLOBAL_PROVIDER.getProviders();
	}
	
	/**
	 * Creates a new temporary provider
	 * @param type repository implementation type; equals the extension type name
	 * @param uri the uri to access the repository (may contain options; see the documentation of the repository implementation)
	 * @return the repository provider
	 * @throws CoreException thrown on errors
	 */
	public static IRepositoryProvider createProvider(String type, String uri) throws CoreException {
		init();
		for (final IRepositoryProviderFactory factory : FACTORIES) {
			if (type.equals(factory.getType())) {
				nextId++;
				return factory.createTemporary(uri, "temp-" + nextId);
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "Cannot create repository; type not found."));
	}
	
	/**
	 * Registers a repository provider.
	 * @param provider the provider to be registered.
	 */
	public static void registerProvider(IRepositoryProvider provider) {
		init();
		GLOBAL_PROVIDER.registerProvider(provider);
	}
	
	/**
	 * Unregisters the repository provider.
	 * @param provider the provider to be unregistered.
	 */
	public static void unregisterProvider(IRepositoryProvider provider) {
		init();
		GLOBAL_PROVIDER.unregisterProvider(provider);
	}
	
	/**
	 * Returns the provider factories being able to create repositories of a specific type.
	 * @return The provider factory list
	 */
	public static Iterable<IRepositoryProviderFactory> getFactories() {
		init();
		return Collections.unmodifiableList(FACTORIES);
	}
	
	/**
	 * Mask search Strings, for example given to {@see IRepositoryProvider#findModule(String, String, String)} to represent a regex
	 * @param searchString search string (must not be null)
	 * @return the regex string
	 */
	public static String maskSearchStringToRegexp(String searchString) {
		return searchString.toLowerCase()
				.replace("\\", "\\\\")
				.replace(".", "\\.")
				.replace("[", "\\[")
				.replace("]", "\\]")
				.replace("^", "\\^")
				.replace("$", "\\$")
				.replace("|", "\\|")
				.replace("?", "\\?")
				.replace("+", "\\+")
				.replace("(", "\\(")
				.replace(")", "\\)")
				// allowed wildcards
				.replace("*", ".*");
	}
	

	public static boolean debug() {
		final String debugOption = Platform.getDebugOption("org.pdtextensions.repos/debug"); //$NON-NLS-1$
		return "true".equalsIgnoreCase(debugOption); 
		
	}
	
}
