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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayVariableReference;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.refactoring.IPHPRefactorings;
import org.pdtextensions.core.ui.refactoring.IRefactoringProcessorIds;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameLocalVariableProcessor extends PHPRenameProcessor {
	public RenameLocalVariableProcessor(IField modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkFieldName(newName);
	}

	@Override
	public String getIdentifier() {
		return IRefactoringProcessorIds.RENAME_LOCAL_VARIABLE_PROCESSOR;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameLocalVariableRefactoring_name;
	}

	@Override
	public boolean needsSavedEditors() {
		return false;
	}

	@Override
	protected String getRefactoringId() {
		return IPHPRefactorings.RENAME_LOCAL_VARIABLE;
	}

	@Override
	protected RefactoringStatus updateReferences(IProgressMonitor pm) throws CoreException {
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(cu);
		if (moduleDeclaration != null) {
			IModelElement parent = modelElement.getParent();
			ISourceRange range = null;

			if (parent.getElementType() == IModelElement.METHOD) {
				range = ((IMethod) parent).getSourceRange();
			} else if (parent.getElementType() == IModelElement.SOURCE_MODULE) {
				range = ((ISourceModule) parent).getSourceRange();
			}

			if (range != null) {
				final int sourceStart = range.getOffset();
				final int sourceEnd = range.getOffset() + range.getLength();

				try {
					moduleDeclaration.traverse(new PHPASTVisitor() {
						private int occurrenceCount = 0;

						@Override
						public boolean visit(ArrayVariableReference s) throws Exception {
							return visit((VariableReference) s);
						}

						@Override
						public boolean visit(VariableReference s) throws Exception {
							if (s.sourceStart() >= sourceStart && s.sourceEnd() <= sourceEnd && s.getName().equals(modelElement.getElementName())) {
								if (occurrenceCount > 0) {
									addTextEdit(changeManager.get(cu), getProcessorName(), new ReplaceEdit(s.sourceStart(), currentName.length(), getNewElementName()));
								}

								occurrenceCount += 1;
							}

							return true;
						}
					});
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, PEXUIPlugin.PLUGIN_ID, e.getMessage(), e));
				}
			}
		}

		return new RefactoringStatus();
	}
}
