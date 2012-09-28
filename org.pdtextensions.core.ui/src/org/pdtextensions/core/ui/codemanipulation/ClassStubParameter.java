/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Marek Maksimczyk <marek.maksimczyk@mandos.net.pl> 
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.codemanipulation;

import java.util.List;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;

public class ClassStubParameter {
	private IScriptProject project;
	private String name = "";
	private boolean abstractClass = false;
	private boolean finalClass = false;
	private String namespace;
	private String modifier;
	private IType superclass;
	private List<IType> interfaces;
	private boolean constructor;
	private boolean inheritedMethods;
	private boolean comments;

	public IScriptProject getProject() {
		return project;
	}

	public void setProject(IScriptProject project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public IType getSuperclass() {
		return superclass;
	}

	public void setSuperclass(IType superclass) {
		this.superclass = superclass;
	}

	public List<IType> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<IType> interfaces) {
		this.interfaces = interfaces;
	}

	public boolean isConstructor() {
		return constructor;
	}

	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}

	public boolean createInheritedMethods() {
		return inheritedMethods;
	}

	public void setInheritedMethods(boolean inheritedMethods) {
		this.inheritedMethods = inheritedMethods;
	}

	public boolean isComments() {
		return comments;
	}

	public void setComments(boolean comments) {
		this.comments = comments;
	}

	public boolean isAbstractClass() {
		return abstractClass;
	}

	public void setAbstractClass(boolean abstractClass) {
		this.abstractClass = abstractClass;
	}

	public boolean isFinalClass() {
		return finalClass;
	}

	public void setFinalClass(boolean finalClass) {
		this.finalClass = finalClass;
	}
}