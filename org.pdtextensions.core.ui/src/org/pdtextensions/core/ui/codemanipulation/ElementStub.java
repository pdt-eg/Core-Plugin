package org.pdtextensions.core.ui.codemanipulation;

import java.util.List;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;

public abstract class ElementStub {

	protected String code = null;
	protected IScriptProject scriptProject = null;
	protected String lineDelim = "\n";
	protected String name;
	protected String namespace;
	protected IType superclass;
	protected boolean isFinal;
	protected List<IType> interfaces;
	protected boolean generateComments;

	public ElementStub() {
		super();
	}

	protected String generateAncestorsPart() {
		if (superclass != null && superclass.getElementName() != null) {

			return " extends " + superclass.getElementName();
		}

		return "";
	}

	protected String generateInterfacesCode() {

		String code = new String();
		if (!interfaces.isEmpty()) {
			code = " implements";

			int size = interfaces.size();
			int i = 1;
			for (IType interfaceObject : interfaces) {
				if (i < size) {
					code += " " + interfaceObject.getElementName() + ",";
				} else {
					code += " " + interfaceObject.getElementName();
				}
				i = i + 1;
			}
		}

		return code;
	}

	protected String generateNamespacePart() {
		// TODO: Create list of namespaces to avoid duplication;
		String code = new String();
		if (namespace != null && namespace.length() > 0) {
			code = "namespace " + namespace + ";\n\n";
		}

		if (superclass != null && superclass.getParent() != null && getNamespace(superclass) != null
				&& !getNamespace(superclass).equals(namespace)) {
			code += "use " + superclass.getFullyQualifiedName().replace("$", "\\") + ";" + lineDelim;
		}

		// TODO: Check why this don't work.
		if (interfaces != null) {
			for (IType interfaceObject : interfaces) {
				if (interfaceObject.getParent() != null && getNamespace(interfaceObject) != null
						&& getNamespace(interfaceObject).equals(namespace)) {
					code += "use " + getNamespace(interfaceObject) + ";\n";
				}
			}
		}

		code += "\n";

		return code;
	}

	private String getNamespace(IType type) {
		// I'm not sure it is good way to check namespaces for class/interface
		if (type.getParent() != null && type.getParent().getElementType() == IType.TYPE) {

			return type.getParent().getElementName();
		}

		return null;
	}

	protected abstract void generateCode();

	public String toString() {

		if (code == null) {
			generateCode();
		}

		return code;
	}

}