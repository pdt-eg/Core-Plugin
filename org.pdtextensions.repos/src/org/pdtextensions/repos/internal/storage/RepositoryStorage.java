package org.pdtextensions.repos.internal.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Helper class to access persistent storages
 * 
 * @author mepeisen
 */
public class RepositoryStorage {
	
	/** the singleton instance */
	private static RepositoryStorage INSTANCE;
	
	private int nextId;
	
	/** the known providers */
	private List<Provider> providers = new ArrayList<Provider>();
	
	private RepositoryStorage() {
		final Preferences preferences = getPrefs();
		this.nextId = preferences.getInt("nextid", 0);
		final String[] names = preferences.get("names", "").split(":");
		for (final String id : names) {
			if (id.length() == 0) {
				final Preferences node = preferences.node(id);
				final String type = node.get("type", null);
				final String uri = node.get("uri", null);
				this.providers.add(new Provider(id, type, uri));
			}
		}
	}
	
	/**
	 * Returns the singleton instance
	 * @return instance of the singleton
	 */
	public static RepositoryStorage instance() {
		if (INSTANCE == null) {
			INSTANCE = new RepositoryStorage();
		}
		return INSTANCE;
	}
	
	public String getNextId() {
		this.nextId++;
		final Preferences preferences = getPrefs();
		preferences.putInt("nextId", this.nextId);
		return "persist-repository-" + this.nextId;
	}

	private IEclipsePreferences getPrefs() {
		return ConfigurationScope.INSTANCE.getNode("org.pdtextensions.repos");
	}
	
	public void addProvider(String id, String type, String uri) {
		final Preferences preferences = getPrefs();
		final Preferences node = preferences.node(id);
		node.put("type", type);
		node.put("uri", uri);
		this.providers.add(new Provider(id, type, uri));
	}
	
	public void removeProvider(String id) {
		for (final Provider provider : this.providers) {
			if (provider.getId().equals(id)) {
				this.providers.remove(provider);
				break;
			}
		}
		final Preferences preferences = getPrefs();
		try {
			preferences.node(id).removeNode();
		} catch (BackingStoreException e) {
			// TODO Error logging
		}
	}
	
	public Iterable<Provider> providers() {
		return Collections.unmodifiableList(this.providers);
	}

}
