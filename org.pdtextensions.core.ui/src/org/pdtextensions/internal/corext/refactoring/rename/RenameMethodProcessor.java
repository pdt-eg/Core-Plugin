/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.search.MethodReferenceMatch;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.internal.corext.refactoring.Checks;

/**
 * @since 0.17.0
 */
public class RenameMethodProcessor extends PHPRenameProcessor {
	public static final String IDENTIFIER = "org.pdtextensions.internal.corext.refactoring.rename.renameMethodProcessor"; //$NON-NLS-1$

	public RenameMethodProcessor(IMethod modelElement) {
		super(modelElement);
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
	    return Checks.isAvailable(modelElement);
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(RefactoringCoreMessages.RenameTypeRefactoring_checking, 1);

		try {
			Change result = new DynamicValidationRefactoringChange(createRefactoringDescriptor(), getProcessorName(), changeManager.getAllChanges());
			monitor.worked(1);

			return result;
		} finally {
			changeManager.clear();
		}
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	protected String getRefactoringId() {
		return IScriptRefactorings.RENAME_METHOD;
	}

	@Override
	protected ReplaceEdit createReplaceEdit(SearchMatch match) {
		if (match instanceof MethodReferenceMatch) {
			if (((MethodReferenceMatch) match).getNode() instanceof CallExpression) {
				return new ReplaceEdit(((CallExpression) ((MethodReferenceMatch) match).getNode()).getCallName().sourceStart(), currentName.length(), getNewElementName());
			} else {
				return null;
			}
		} else {
			return new ReplaceEdit(match.getOffset(), currentName.length(), getNewElementName());
		}
	}
}
