/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKContentTypeManager;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.internal.corext.refactoring.CaseSensitivity;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameTypeProcessor extends PHPRenameProcessor {
	public static final String IDENTIFIER = "org.pdtextensions.internal.corext.refactoring.rename.renameTypeProcessor"; //$NON-NLS-1$

	public RenameTypeProcessor(IType modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkTypeName(newName);
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameTypeRefactoring_name;
	}

	@Override
	public boolean isApplicable() throws CoreException {
	    return Checks.isAvailable(modelElement);
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(RefactoringCoreMessages.RenameTypeRefactoring_checking, 1);

		try {
			DynamicValidationRefactoringChange result = new DynamicValidationRefactoringChange(createRefactoringDescriptor(), getProcessorName(), changeManager.getAllChanges());
			IResource resource = modelElement.getResource();
			if (resource != null && resource instanceof IFile && willRenameCU(resource)) {
				result.add(
					new RenameResourceChange(
						resource.getFullPath(),
						resource.getFileExtension().isEmpty() ? getNewElementName() : getNewElementName() + "." + resource.getFileExtension() //$NON-NLS-1$
					)
				);
			}
			monitor.worked(1);

			return result;
		} finally {
			changeManager.clear();
		}
	}

	@Override
	protected IFile[] getChangedFiles() throws CoreException {
		List<IFile> result = new ArrayList<IFile>();
		result.addAll(Arrays.asList(super.getChangedFiles()));

		IResource resource = modelElement.getResource();
		if (resource != null && resource instanceof IFile && willRenameCU(resource)) {
			result.add((IFile) resource);
		}

		return result.toArray(new IFile[result.size()]);
	}

	@Override
	protected String getRefactoringId() {
		return IScriptRefactorings.RENAME_TYPE;
	}

	@Override
	protected ReplaceEdit createReplaceEdit(SearchMatch match) {
		return new ReplaceEdit(match.getOffset(), currentName.length(), getNewElementName());
	}

	@Override
	protected CaseSensitivity getNameCaseSensitivity() {
		return CaseSensitivity.CaseInsensitive;
	}

	private boolean willRenameCU(IResource resource) {
		if (resource.isLinked()) return false;
		if (!resource.getName().substring(0, resource.getName().indexOf(resource.getFileExtension()) - 1).equals(modelElement.getElementName())) return false;
		if (!DLTKContentTypeManager.isValidFileNameForContentType(PHPLanguageToolkit.getDefault(), getNewElementName() + "." + resource.getFileExtension())) return false; //$NON-NLS-1$

		return true;
	}
}
