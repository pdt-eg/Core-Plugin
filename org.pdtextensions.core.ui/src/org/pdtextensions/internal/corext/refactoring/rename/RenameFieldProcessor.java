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
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.search.FieldReferenceMatch;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
public class RenameFieldProcessor extends PHPRenameProcessor {
	public static final String IDENTIFIER = "org.pdtextensions.internal.corext.refactoring.rename.renameFieldProcessor"; //$NON-NLS-1$

	public RenameFieldProcessor(IField modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkFieldName(newName);
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameFieldRefactoring_name;
	}

	@Override
	public boolean isApplicable() throws CoreException {
	    return Checks.isAvailable(modelElement);
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(RefactoringCoreMessages.RenameFieldRefactoring_checking, 1);

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
		return IScriptRefactorings.RENAME_FIELD;
	}

	@Override
	protected ReplaceEdit createReplaceEdit(SearchMatch match) {
		if (match instanceof FieldReferenceMatch) {
			String newElementName = getNewElementName();
			newElementName = newElementName.replace("$", ""); //$NON-NLS-1$ //$NON-NLS-2$

			return new ReplaceEdit(match.getOffset(), currentName.length() - 1, newElementName);
		} else {
			return new ReplaceEdit(match.getOffset(), currentName.length(), getNewElementName());
		}
	}
}
