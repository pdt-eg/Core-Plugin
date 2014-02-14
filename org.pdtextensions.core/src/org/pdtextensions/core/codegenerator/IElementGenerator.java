package org.pdtextensions.core.codegenerator;

public interface IElementGenerator {

	public abstract void setName(String name);

	public abstract void setSuperclass(String superclass);

	public abstract void setNamespace(String namespace);

	public abstract String generateCode();

	public abstract void addInterface(String interfaceFullName);

	public abstract void addTrait(String traitFullQualifiName);
}