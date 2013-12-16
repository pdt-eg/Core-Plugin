/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IType;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

/**
 * This class provides information about the type as a PHP class, interface, trait, and namespace.
 *
 * @since 0.20.0
 */
@SuppressWarnings("restriction")
public class PHPType {
	private final static String NAMESPACE_SEPARATOR = "\\"; //$NON-NLS-1$
	private IType type;

	public PHPType(IType type) {
		Assert.isNotNull(type);

		this.type = type;
	}

	public boolean inNamespace() {
		return PHPClassType.fromIType(type).getNamespace() != null;
	}

	public boolean inResourceWithSameName() throws CoreException {
		return inResourceWithSameName(type.getResource(), type.getElementName());
	}

	public boolean isPSR0Compliant() throws CoreException {
		if (inNamespace()) {
			String typeQualfiedName = type.getTypeQualifiedName(NAMESPACE_SEPARATOR);
			IResource resource = type.getResource();

			do {
				if (!inResourceWithSameName(resource, typeQualfiedName.substring(typeQualfiedName.lastIndexOf(NAMESPACE_SEPARATOR) + 1))) {
					return false;
				}

				typeQualfiedName = typeQualfiedName.substring(0, typeQualfiedName.lastIndexOf(NAMESPACE_SEPARATOR));
				resource = resource.getParent();
			} while (typeQualfiedName.contains(NAMESPACE_SEPARATOR));

			return true;
		} else {
			return false;
		}
	}

	private static boolean inResourceWithSameName(IResource resource, String typeName) throws CoreException {
		if (resource instanceof IFile) {
			return resource.getName().substring(0, resource.getName().indexOf(resource.getFileExtension()) - 1).equals(typeName);
		} else if (resource instanceof IFolder) {
			return resource.getName().equals(typeName.substring(typeName.lastIndexOf(NAMESPACE_SEPARATOR) + 1));
		} else {
			throw new CoreException(new Status(IStatus.ERROR, PEXCorePlugin.PLUGIN_ID, "The resource is neither a file or folder")); //$NON-NLS-1$
		}
	}
}
