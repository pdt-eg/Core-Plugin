package org.pdtextensions.core.codegenerator;

public interface IElementGenerator {

	/**
	 * 
	 * @param name - stub name, stub can be class, trait, interface etc. 
	 */
	void setName(String name);

	/**
	 * 
	 * @param namespace - namespace for
	 */
	void setNamespace(String namespace);

	void addInterface(String interfaceName);
}
