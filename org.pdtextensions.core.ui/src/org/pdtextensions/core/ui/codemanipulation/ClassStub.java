/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * Modified by Marek Maksimczyk <marek.maksimczyk@mandos.net.pl>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.codemanipulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.ui.CodeGeneration;

/**
 * Utilities for class generation.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Marek Maksimczyk <marek.maksimczyk@mandos.net.pl>
 */
public class ClassStub {

	private String code = null;
	private IScriptProject scriptProject = null;

	private String lineDelim = "\n";

	private String name;
	private String namespace;
	private IType superclass;
	private boolean isFinal;
	private boolean isAbstract;
	private List<IType> interfaces;
	private boolean generateComments;
	private boolean generateConstructor;
	private boolean generateInheritedMethods;

	public ClassStub(IScriptProject scriptProject, ClassStubParameter parameters) {
		this.scriptProject = scriptProject;

		name = parameters.getName();
		superclass = parameters.getSuperclass();
		namespace = parameters.getNamespace();
		isFinal = parameters.isFinalClass();
		isAbstract = parameters.isAbstractClass();
		interfaces = parameters.getInterfaces();
		generateComments = parameters.isComments();
		generateConstructor = parameters.isConstructor();
		generateInheritedMethods = parameters.createInheritedMethods();
	}

	/**
	 * Retrieve the code for a class stub.
	 * 
	 * @throws CoreException
	 */
	private void generateCode() throws CoreException {

		StringBuilder buffer = new StringBuilder("<?php");
		buffer.append(lineDelim);

		buffer.append(generateNamespacePart());

		if (generateComments == true)
			buffer.append(CodeGeneration.getTypeComment(scriptProject, name, lineDelim) + lineDelim);

		if (isFinal == true) {
			buffer.append("final ");
		}

		if (isAbstract == true) {
			buffer.append("abstract ");
		}

		buffer.append("class " + name);

		buffer.append(generateSuperclassPart());

		buffer.append(generateInterfacesCode());

		buffer.append("{" + lineDelim);

		if (generateConstructor) {
			// TODO: generate Constructor
		}

		buffer.append(generateMethods());

		buffer.append(lineDelim + "}");
		code = buffer.toString();
	}

	private String generateSuperclassPart() {
		if (superclass != null && superclass.getElementName() != null) {

			return " extends " + superclass.getElementName();
		}

		return "";
	}

	private String generateInterfacesCode() {

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

	private String generateNamespacePart() {
		// TODO: Create list of namespaces to avoid duplication;
		String code = new String();
		if (namespace != null && namespace.length() > 0) {
			code = "namespace " + namespace + ";\n\n";
		}

		if (superclass != null && superclass.getParent() != null && getNamespace(superclass) != null
				&& !getNamespace(superclass).equals(namespace)) {
			code += "use " + getNamespace(superclass) + ";\n";
		}

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

	private String generateMethods() {
		String code = "";
		if (generateInheritedMethods == true) {
			try {
				IMethod[] methods = PHPModelUtils.getUnimplementedMethods(superclass, new NullProgressMonitor());
				for (IMethod method : methods) {
					code += new MethodStub(scriptProject, method, generateComments).toString();
				}
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}

		return code;
	}

	public String toString() {

		if (code == null) {
			try {
				generateCode();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return code;
	}
}
