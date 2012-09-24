package org.pdtextensions.repos.api;

/**
 * The repository provider factory is responsible to create repository providers of a certain type.
 * 
 * @author mepeisen
 */
public interface IRepositoryProviderFactory {
	
	/**
	 * Returns the repository type name
	 * @return repository type name
	 */
	String getType();
	
	/**
	 * Creates a temporary repository
	 * @param uri uri of the repository
	 * @param id the temporary id
	 * @return the new provider object
	 */
	IRepositoryProvider createTemporary(String uri, String id);
	
	/**
	 * Creates the default repositories
	 * @return the default repositories that are self-registered as soon as the underlying plugin is activated
	 */
	Iterable<IRepositoryProvider> createDefaultRepositories();
	
	/**
	 * Persists the provider, changes the id to a persistent id and should save all additional provider options to the workspace storage
	 * so that {@see #recoverPersistent(String, String)} is able to reload the additional options.
	 * @param provider
	 * @param id The new persistent id
	 */
	void persist(IRepositoryProvider provider, String id);
	
	/**
	 * Recovers a persistent repository (f.e. after restarting eclipse).
	 * @param uri the uri
	 * @param id the persistent id
	 * @return recovered provider.
	 */
	IRepositoryProvider recoverPersistent(String uri, String id);
	
	/**
	 * Destroy the persistent provider
	 * @param provider the provider to be destroyed
	 */
	void destroy(IRepositoryProvider provider);

}
