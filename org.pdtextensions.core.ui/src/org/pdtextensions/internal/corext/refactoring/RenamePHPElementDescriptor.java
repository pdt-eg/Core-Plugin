/*******************************************************************************
 * Copyright (c) 2006, 2008, 2013 IBM Corporation and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.internal.corext.refactoring.ScriptRefactoringDescriptor;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.pdtextensions.core.ui.refactoring.IPHPRefactorings;

/**
 * This is the replacement of the org.eclipse.dltk.internal.core.refactoring.descriptors.RenameModelElementDescriptor
 * class for PHP elements.
 *
 * @since 0.17.0
 */
public class RenamePHPElementDescriptor extends ScriptRefactoringDescriptor {
	/**
	 * The PHP element attribute.
	 * WARNING: may not exist, see comment in
	 * {@link ScriptRefactoringDescriptor#handleToElement(org.eclipse.dltk.core.WorkingCopyOwner, String, String, boolean)}.
	 */
	private IModelElement phpElement;

	/** The name attribute */
	private String name;

	/** The references attribute */
	private boolean references = false;

	/**
	 * Creates a new refactoring descriptor.
	 *
	 * @param id
	 *            the unique id of the rename refactoring
	 * @see IScriptRefactorings
	 */
	public RenamePHPElementDescriptor(final String id) {
		super(id);
		Assert.isLegal(checkId(id), "Refactoring id is not a rename refactoring id"); //$NON-NLS-1$
	}

	/**
	 * Creates a new refactoring descriptor.
	 *
	 * @param id
	 *            the ID of this descriptor
	 * @param project
	 *            the non-empty name of the project associated with this
	 *            refactoring, or <code>null</code> for a workspace
	 *            refactoring
	 * @param description
	 *            a non-empty human-readable description of the particular
	 *            refactoring instance
	 * @param comment
	 *            the human-readable comment of the particular refactoring
	 *            instance, or <code>null</code> for no comment
	 * @param arguments
	 * 			  a map of arguments that will be persisted and describes
	 * 			  all settings for this refactoring
	 * @param flags
	 *            the flags of the refactoring descriptor
	 *
	 * @throws IllegalArgumentException if the argument map contains invalid keys/values
	 *
	 * @since 1.2
	 */
	public RenamePHPElementDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
		super(id, project, description, comment, arguments, flags);

		Assert.isLegal(checkId(id), "Refactoring id is not a rename refactoring id"); //$NON-NLS-1$

		name = PHPRefactoringDescriptorUtil.getString(fArguments, ATTRIBUTE_NAME);
		phpElement = PHPRefactoringDescriptorUtil.getPHPElement(fArguments, ATTRIBUTE_INPUT, getProject());
		references = PHPRefactoringDescriptorUtil.getBoolean(fArguments, ATTRIBUTE_REFERENCES, references);
	}

	/**
	 * Checks whether the refactoring id is valid.
	 *
	 * @param id
	 *            the refactoring id
	 * @return the outcome of the validation
	 */
	private boolean checkId(final String id) {
		Assert.isNotNull(id);

		if (id.equals(IScriptRefactorings.RENAME_TYPE)) {
			return true;
		} else if (id.equals(IScriptRefactorings.RENAME_METHOD)) {
			return true;
		} else if (id.equals(IPHPRefactorings.RENAME_PROPERTY)) {
			return true;
		} else if (id.equals(IPHPRefactorings.RENAME_STATIC_PROPERTY)) {
			return true;
		} else if (id.equals(IScriptRefactorings.RENAME_LOCAL_VARIABLE)) {
			return true;
		} else if (id.equals(IPHPRefactorings.RENAME_CONSTANT)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void populateArgumentMap() {
		super.populateArgumentMap();

		PHPRefactoringDescriptorUtil.setString(fArguments, ATTRIBUTE_NAME, name);
		PHPRefactoringDescriptorUtil.setPHPElement(fArguments, ATTRIBUTE_INPUT, getProject(), phpElement);
		PHPRefactoringDescriptorUtil.setBoolean(fArguments, ATTRIBUTE_REFERENCES, references);
	}

	/**
	 * Sets the Script element to be renamed.
	 * <p>
	 * Note: If the Script element to be renamed is of type
	 * {@link IModelElement#SCRIPT_PROJECT}, clients are required to to set the
	 * project name to <code>null</code>.
	 * </p>
	 *
	 * @param element
	 *            the Script element to be renamed
	 */
	public void setModelElement(final IModelElement element) {
		Assert.isNotNull(element);

		phpElement = element;
	}

	/**
	 * Sets the new name to rename the Script element to.
	 *
	 * @param name
	 *            the non-empty new name to set
	 */
	public void setNewName(final String name) {
		Assert.isNotNull(name);
		Assert.isLegal(!"".equals(name), "Name must not be empty"); //$NON-NLS-1$ //$NON-NLS-2$

		this.name = name;
	}

	/**
	 * Determines whether references to the Script element should be renamed.
	 * <p>
	 * Note: Reference updating is currently applicable to all Script element types except
	 * {@link IPackageFragmentRoot}. The default is to not update references.
	 * </p>
	 *
	 * @param update
	 *            <code>true</code> to update references, <code>false</code>
	 *            otherwise
	 */
	public void setUpdateReferences(final boolean update) {
		references = update;
	}
}
