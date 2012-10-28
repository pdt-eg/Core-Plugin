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
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.ui.CodeGeneration;
import org.pdtextensions.core.log.Logger;

/**
 * Utilities for class generation.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Marek Maksimczyk <marek.maksimczyk@mandos.net.pl>
 * 
 */
@SuppressWarnings("restriction")
public class ClassStub extends ElementStub {

	protected boolean isAbstract;
	protected IType superclass;
	protected boolean generateConstructor;
	protected boolean generateInheritedMethods;
	protected boolean isFinal;
	private ArrayList<IMethod> unimplementedMethods;

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
	protected void generateCode() {

		try {
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

			buffer.append(generateAncestorsPart());

			buffer.append(generateInterfacesCode());

			buffer.append("{" + lineDelim);

			if (generateConstructor) {
				buffer.append(generateConstructor());
			}

			buffer.append(generateMethods());

			buffer.append(lineDelim + "}");
			code = buffer.toString();
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}

	private String generateConstructor() {
		String constructor = "";
		try {
			IMethod[] constructors;
			// Searching for constructor in parent class;
			constructors = PHPModelUtils.getTypeMethod(superclass, "__construct", true);
			if (constructors.length == 0) {
				// Searching for constructor in hierarchy;
				constructors = PHPModelUtils.getSuperTypeHierarchyMethod(superclass,
					"__construct",
					true,
					new NullProgressMonitor());
			}
			if (constructors.length != 0) {
				constructor = new MethodStub(scriptProject, constructors[0], generateComments).toString();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return constructor;
	}

	private String generateMethods() {
		String code = "";
		if (getUnimplementedMethods() != null) {
			for (IMethod method : unimplementedMethods) {
				code += new MethodStub(scriptProject, method, generateComments).toString();
			}
		}

		return code;
	}
	
	private boolean hasPossibleMethodsToImplement()
	{
		return superclass instanceof IType || (interfaces != null && interfaces.size() > 0);
	}

	private ArrayList<IMethod> getUnimplementedMethods() {
		
		if (generateInheritedMethods == true && hasPossibleMethodsToImplement() && unimplementedMethods == null) {
			unimplementedMethods = new ArrayList<IMethod>();
			
			if (superclass != null) {
				Collections.addAll(unimplementedMethods, getUnimplementedMethods(superclass));
			}

			for (IType interfaceObject : interfaces) {
				System.err.println(interfaceObject);
				Collections.addAll(unimplementedMethods, getUnimplementedMethods(interfaceObject));
			}
		}

		return unimplementedMethods;
	}

	private IMethod[] getUnimplementedMethods(IType type) {
		try {

			return PHPModelUtils.getUnimplementedMethods(type, new NullProgressMonitor());
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}

	private String generateAncestorsPart() {
		if (superclass != null && superclass.getElementName() != null) {
			// If there are namespace we will add it in use section if not we
			// add \ before name
			String prefix = getUseNamespaceString(superclass) == null ? "\\" : "";

			return " extends " + prefix + superclass.getElementName();
		}

		return "";
	}

	@Override
	protected HashSet<String> getUseNamespacesList() {
		// "use" statement for interfaces
		HashSet<String> namespaces = super.getUseNamespacesList();
		// "use" statement for superclass
		if (superclass != null && getUseNamespaceString(superclass) != null) {
			namespaces.add(getUseNamespaceString(superclass));
		}
		// TODO: Add "use" section for parameters object
		
		return namespaces;
	}
}
