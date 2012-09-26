/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
