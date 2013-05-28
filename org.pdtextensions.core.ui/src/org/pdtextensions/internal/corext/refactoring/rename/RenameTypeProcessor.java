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
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKContentTypeManager;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.ui.refactoring.IRefactoringProcessorIds;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameTypeProcessor extends PHPRenameProcessor {
	public RenameTypeProcessor(IType modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkTypeName(newName);
	}

	@Override
	public String getIdentifier() {
		return IRefactoringProcessorIds.RENAME_TYPE_PROCESSOR;
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
	public Change createChange(IProgressMonitor pm) throws CoreException {
		pm.beginTask(RefactoringCoreMessages.RenameTypeRefactoring_checking, 1);

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
			pm.worked(1);

			return result;
		} finally {
			changeManager.clear();
			pm.done();
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
	protected RefactoringStatus updateReferences(IProgressMonitor pm) throws CoreException {
		new SearchEngine().search(
			SearchPattern.createPattern(
				modelElement,
				IDLTKSearchConstants.REFERENCES,
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_ERASURE_MATCH,
				PHPLanguageToolkit.getDefault()
			),
			new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() },
			SearchEngine.createWorkspaceScope(PHPLanguageToolkit.getDefault()),
			new SearchRequestor() {
				@Override
				public void acceptSearchMatch(SearchMatch match) throws CoreException {
					if (match.getElement() instanceof IModelElement) {
						ISourceModule module = (ISourceModule) ((IModelElement) match.getElement()).getAncestor(IModelElement.SOURCE_MODULE);
						if (!(module instanceof IExternalSourceModule)) {
							try {
								addTextEdit(changeManager.get(module), getProcessorName(), new ReplaceEdit(match.getOffset(), currentName.length(), getNewElementName()));
							} catch (MalformedTreeException e) {
								// conflicting update -> omit text match
							}
						}
					}
				}
			},
			new SubProgressMonitor(pm, 1000)
		);

		return new RefactoringStatus();
	}

	private boolean willRenameCU(IResource resource) {
		if (resource.isLinked()) return false;
		if (!resource.getName().substring(0, resource.getName().indexOf(resource.getFileExtension()) - 1).equals(modelElement.getElementName())) return false;
		if (!DLTKContentTypeManager.isValidFileNameForContentType(PHPLanguageToolkit.getDefault(), getNewElementName() + "." + resource.getFileExtension())) return false; //$NON-NLS-1$

		return true;
	}
}
