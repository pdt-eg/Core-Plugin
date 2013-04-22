/*******************************************************************************
 * Copyright (c) 2007, 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.internal.corext.refactoring;

import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.WorkingCopyOwner;

/**
 * This is the replacement of the {@link org.eclipse.jdt.internal.core.refactoring.descriptors.JavaRefactoringDescriptorUtil}
 * class for PHP elements.
 *
 * @since 0.17.0
 */
public class PHPRefactoringDescriptorUtil {
	private static final String LOWER_CASE_FALSE = Boolean.FALSE.toString().toLowerCase();
	private static final String LOWER_CASE_TRUE = Boolean.TRUE.toString().toLowerCase();

	/**
	 * Converts the specified element to an input handle.
	 *
	 * @param project
	 *            the project, or <code>null</code> for the workspace
	 * @param element
	 *            the element
	 * @return a corresponding input handle.
	 *            Note: if the given project is not the ModelElement's project, then the full handle is returned
	 */
	public static String elementToHandle(final String project, final IModelElement element) {
		final String handle = element.getHandleIdentifier();
		if (project != null && !(element instanceof IScriptProject)) {
			IScriptProject phpProject = element.getScriptProject();
			if (project.equals(phpProject.getElementName())) {
				final String id = phpProject.getHandleIdentifier();

				return handle.substring(id.length());
			}
		}

		return handle;
	}

	/**
	 * Converts an input handle back to the corresponding PHP element.
	 *
	 * @param owner
	 *            the working copy owner
	 * @param project
	 *            the project, or <code>null</code> for the workspace
	 * @param handle
	 *            the input handle
	 * @param check
	 *            <code>true</code> to check for existence of the element,
	 *            <code>false</code> otherwise
	 * @return the corresponding PHP element, or <code>null</code> if no such
	 *         element exists
	 */
	public static IModelElement handleToElement(final WorkingCopyOwner owner, final String project, final String handle, final boolean check) {
		IModelElement element = owner == null ? element = DLTKCore.create(handle) : DLTKCore.create(handle, owner);

		if (element == null && project != null) {
			final IScriptProject phpProject = DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot()).getScriptProject(project);
			final String identifier = phpProject.getHandleIdentifier();
			element = owner == null ? DLTKCore.create(identifier + handle) : DLTKCore.create(identifier + handle, owner);
		}

		if (check && element instanceof IMethod) {
			/*
			 * Resolve the method based on simple names of parameter types
			 * (to accommodate for different qualifications when refactoring is e.g.
			 * recorded in source but applied on binary method):
			 */
			final IMethod method = (IMethod) element;
			final IMethod[] methods = method.getDeclaringType().findMethods(method);
			if (methods != null && methods.length > 0) {
				element = methods[0];
			}
		}

		if (element == null) {
			return null;
		} else {
			if (check) {
				if (element.exists()) {
					return element;
				} else {
					return null;
				}
			} else {
				return element;
			}
		}
	}

	/**
	 * Retrieves a {@link String} attribute from map.
	 *
	 * @param map the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key in the map
	 * @param allowNull if <code>true</code> a <code>null</code> will be returned if the attribute does not exist
	 * @return the value of the attribute
	 *
	 * @throws IllegalArgumentException if the value of the attribute is not a {@link String} or allowNull is <code>false</code> and the attribute does not exist
	 */
	public static String getString(Map map, String attribute, boolean allowNull) throws IllegalArgumentException {
		Object object = map.get(attribute);
		if (object == null) {
			if (allowNull) {
				return null;
			}

			throw new IllegalArgumentException("The map does not contain the attribute '" + attribute + "'"); //$NON-NLS-1$//$NON-NLS-2$
		}

		if (object instanceof String) {
			String value = (String) object;
			return value;
		}

		throw new IllegalArgumentException("The provided map does not contain a string for attribute '" + attribute + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Retrieves a {@link String} attribute from map.
	 *
	 * @param map the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key in the map
	 * @return the value of the attribute
	 *
	 * @throws IllegalArgumentException if the value of the attribute is not a {@link String} or the attribute does not exist
	 */
	public static String getString(Map map, String attribute)  throws IllegalArgumentException {
		return getString(map, attribute, false);
	}

	/**
	 * Retrieves an <code>{@link IModelElement}</code> attribute from map.
	 *
	 * @param map the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key in the map
	 * @param project the project for resolving the PHP element. Can be <code>null</code> for workspace
	 * @return a {@link IModelElement} or <code>null</code>
	 *
	 * @throws IllegalArgumentException if the attribute does not exist or is not a PHP element
	 * @see #handleToElement(WorkingCopyOwner, String, String, boolean)
	 */
	public static IModelElement getPHPElement(Map map, String attribute, String project)  throws IllegalArgumentException{
		return getPHPElement(map, attribute, project, false);
	}

	/**
	 * Retrieves an <code>{@link IModelElement}</code> attribute from map.
	 *
	 * @param map the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key in the map
	 * @param project the project for resolving the PHP element. Can be <code>null</code> for workspace
	 * @param allowNull if <code>true</code> a <code>null</code> will be returned if the attribute does not exist
	 * @return a {@link IModelElement} or <code>null</code>
	 *
	 * @throws IllegalArgumentException if the attribute does not existt
	 * @see #handleToElement(WorkingCopyOwner, String, String, boolean)
	 */
	public static IModelElement getPHPElement(Map map, String attribute, String project, boolean allowNull)  throws IllegalArgumentException{
		String handle = getString(map, attribute, allowNull);
		if (handle == null) return null;

		return handleToElement(null, project, handle, false); //TODO: update Javadoc
	}

	/**
	 * Retrieves a <code>boolean</code> attribute from map. If the attribute does not exist it returns the default value
	 *
	 * @param map the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key in the map
	 * @param defaultValue the default value to use if the attribute does not exist
	 * @return the <code>boolean</code> value of the attribute or the specified default value if the attribute does not exist
	 *
	 * @throws IllegalArgumentException if the attribute does not contain a valid value
	 */
	public static boolean getBoolean(Map map, String attribute, boolean defaultValue) throws IllegalArgumentException {
		String value = getString(map, attribute, true);
		if (value == null) {
			return defaultValue;
		}

		value = value.toLowerCase();
		if (LOWER_CASE_TRUE.equals(value)) {
			return true;
		}

		if (LOWER_CASE_FALSE.equals(value)) {
			return false;
		}

		throw new IllegalArgumentException("The attribute '" + attribute + "' does not contain a valid boolean: '" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Inserts the <code>{@link IModelElement}</code> into the map.
	 *
	 * @param arguments the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key's name for the map
	 * @param project the project of the element or <code>null</code>.
	 *        Note: if the given project is not the ModelElement's project, then the full handle is stored
	 * @param element the element to store
	 *
	 * @throws IllegalArgumentException if the attribute name is invalid, or the element is <code>null</code>
	 */
	public static void setPHPElement(Map arguments, String attribute, String project, IModelElement element)  throws IllegalArgumentException{
		if (element == null) {
			throw new IllegalArgumentException("The element for attribute '" + attribute + "' may not be null"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		setString(arguments, attribute, elementToHandle(project, element));
	}

	/**
	 * Inserts the <code>boolean</code> into the map.
	 *
	 * @param arguments the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key's name for the map
	 * @param value the <code>boolean</code> to store
	 *
	 * @throws IllegalArgumentException if the attribute name is invalid
	 */
	public static void setBoolean(Map arguments, String attribute, boolean value)  throws IllegalArgumentException{
		setString(arguments, attribute, value ? LOWER_CASE_TRUE : LOWER_CASE_FALSE);
	}

	/**
	 * Inserts the <code>{@link String}</code> into the map.
	 *
	 * @param arguments the map with <code>&lt;String, String&gt;</code> mapping
	 * @param attribute the key's name for the map
	 * @param value the <code>{@link String}</code> to store. If <code>null</code> no insertion is performed
	 *
	 * @throws IllegalArgumentException if the attribute name is invalid
	 */
	public static void setString(Map arguments, String attribute, String value)  throws IllegalArgumentException{
		if (attribute == null || "".equals(attribute) || attribute.indexOf(' ') != -1) { //$NON-NLS-1$
			throw new IllegalArgumentException("Attribute '" + attribute + "' is not valid: '" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (value == null) {
			arguments.remove(attribute);
		} else {
			arguments.put(attribute, value);
		}
	}
}
