/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.ui.refactoring.IPHPRefactorings;
import org.pdtextensions.core.ui.refactoring.IRefactoringProcessorIds;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameStaticPropertyProcessor extends RenameFieldProcessor {
	public RenameStaticPropertyProcessor(IField modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkFieldName(newName);
	}

	@Override
	public String getIdentifier() {
		return IRefactoringProcessorIds.RENAME_STATIC_PROPERTY_PROCESSOR;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameStaticPropertyRefactoring_name;
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	protected String getRefactoringId() {
		return IPHPRefactorings.RENAME_STATIC_PROPERTY;
	}

	@Override
	protected SearchPattern createSearchPatternForReferences() {
		return SearchPattern.createPattern(
			modelElement,
			IDLTKSearchConstants.ALL_OCCURRENCES,
			SearchPattern.R_EXACT_MATCH | SearchPattern.R_ERASURE_MATCH | SearchPattern.R_CASE_SENSITIVE,
			PHPLanguageToolkit.getDefault()
		);
	}

	@Override
	protected ReferenceFinder createReferenceFinder(ASTNode astNode, ISourceModule sourceModule) {
		return new ReferenceFinder(astNode, sourceModule) {
			@Override
			public boolean visit(StaticFieldAccess s) throws Exception {
				return find(s);
			}

			@Override
			protected ReplaceEdit createReplaceEdit(ASTNode astNode) {
				return new ReplaceEdit(astNode.sourceStart(), currentName.length(), getNewElementName());
			}
		};
	}
}
