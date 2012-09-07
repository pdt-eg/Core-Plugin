package org.pdtextensions.repos.internal.storage;

/**
 * A single provider definition
 * 
 * @author mepeisen
 */
public class Provider {
	
	private String type;
	
	private String uri;
	
	private String id;
	
	public Provider(String id, String type, String uri) {
		this.type = type;
		this.id = id;
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public String getUri() {
		return uri;
	}

	public String getId() {
		return id;
	}

}
