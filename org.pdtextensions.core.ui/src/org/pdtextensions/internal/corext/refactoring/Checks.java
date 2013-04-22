/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.model.binary.BinaryMember;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.pdtextensions.core.PHPConventions;

/**
 * @since 0.17.0
 */
public class Checks {
	private Checks() {
	}

	public static RefactoringStatus checkName(String name, IStatus status) {
		if ("".equals(name)) { //$NON-NLS-1$
			return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.Checks_Choose_name);
		}

		if (status.isOK()) {
			return new RefactoringStatus();
		} else {
			switch (status.getSeverity()){
			case IStatus.ERROR:
				return RefactoringStatus.createFatalErrorStatus(status.getMessage());
			case IStatus.WARNING:
				return RefactoringStatus.createWarningStatus(status.getMessage());
			case IStatus.INFO:
				return RefactoringStatus.createInfoStatus(status.getMessage());
			default:
				return new RefactoringStatus();
			}
		}
	}

	public static RefactoringStatus checkFieldName(String name) {
		return checkName(name, PHPConventions.validateFieldName(name));
	}

	public static RefactoringStatus checkMethodName(String name) {
		return checkName(name, PHPConventions.validateMethodName(name));
	}

	public static RefactoringStatus checkTypeName(String name) {
		return checkName(name, PHPConventions.validateTypeName(name));
	}

	public static boolean isAvailable(IModelElement modelElement) throws ModelException {
		if (modelElement == null) return false;
		if (!modelElement.exists()) return false;
		if (modelElement.isReadOnly()) return false;
		// work around for https://bugs.eclipse.org/bugs/show_bug.cgi?id=48422
		// the Java project is now cheating regarding its children so we shouldn't
		// call isStructureKnown if the project isn't open.
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=52474
		if (!(modelElement instanceof IScriptProject) && !(modelElement instanceof ILocalVariable) && !modelElement.isStructureKnown()) return false;
		if (modelElement instanceof IMember && modelElement instanceof BinaryMember) return false;

		return true;
	}
}
