/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RenamePHPElementDescriptor;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameMethodProcessor extends PHPRenameProcessor {
	public static final String IDENTIFIER = "org.pdtextensions.internal.corext.refactoring.rename.renameMethodProcessor"; //$NON-NLS-1$

	public RenameMethodProcessor(IMethod method) {
		super(method, PHPLanguageToolkit.getDefault());
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkMethodName(newName);
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameMethodRefactoring_name;
	}

	@Override
	public boolean isApplicable() throws CoreException {
	    return Checks.isAvailable(fModelElement);
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	protected RefactoringDescriptor createRefactoringDescriptor() {
		return new RenamePHPElementDescriptor(IScriptRefactorings.RENAME_METHOD);
	}
}
