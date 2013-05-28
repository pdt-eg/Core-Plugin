/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.search.FieldReferenceMatch;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.core.util.PDTTypeInferenceUtils;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public abstract class RenameFieldProcessor extends PHPRenameProcessor {
	public RenameFieldProcessor(IModelElement modelElement) {
		super(modelElement);
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	protected RefactoringStatus updateReferences(IProgressMonitor pm) throws CoreException {
		new SearchEngine().search(
			createSearchPatternForReferences(),
			new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() },
			SearchEngine.createWorkspaceScope(PHPLanguageToolkit.getDefault()),
			new SearchRequestor() {
				@Override
				public void acceptSearchMatch(SearchMatch match) throws CoreException {
					if (match instanceof FieldReferenceMatch && match.getElement() instanceof IModelElement) {
						ISourceModule module = (ISourceModule) ((IModelElement) match.getElement()).getAncestor(IModelElement.SOURCE_MODULE);
						if (module != null && RefactoringAvailabilityTester.isRenameAvailable(module)) {
							ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
							if (moduleDeclaration != null) {
								ReferenceFinder referenceFinder = createReferenceFinder(((FieldReferenceMatch) match).getNode(), module);

								try {
									moduleDeclaration.traverse(referenceFinder);
								} catch (Exception e) {
									throw new CoreException(new Status(IStatus.ERROR, PEXUIPlugin.PLUGIN_ID, e.getMessage(), e));
								}

								ReplaceEdit replaceEdit = referenceFinder.getReplaceEdit();
								if (replaceEdit != null) {
									try {
										addTextEdit(changeManager.get(module), getProcessorName(), replaceEdit);
									} catch (MalformedTreeException e) {
										// conflicting update -> omit text match
									}
								}
							}
						}
					}
				}
			},
			new SubProgressMonitor(pm, 1000)
		);

		return new RefactoringStatus();
	}

	protected abstract SearchPattern createSearchPatternForReferences();

	protected abstract ReferenceFinder createReferenceFinder(ASTNode astNode, ISourceModule sourceModule);

	protected abstract class ReferenceFinder extends PHPASTVisitor {
		private ASTNode astNode;
		private ISourceModule sourceModule;
		private ReplaceEdit replaceEdit;

		public ReferenceFinder(ASTNode astNode, ISourceModule sourceModule) {
			this.astNode = astNode;
			this.sourceModule = sourceModule;
		}

		public ReplaceEdit getReplaceEdit() {
			return replaceEdit;
		}

		protected boolean find(Expression s) throws Exception {
			if (s.sourceStart() < astNode.sourceStart() && s.sourceEnd() == astNode.sourceEnd()) {
				List<ASTNode> children = s.getChilds();
				for (int i = 0; i < children.size(); ++i) {
					ASTNode fieldReference = children.get(i);
					if (fieldReference.sourceStart() == astNode.sourceStart() && fieldReference.sourceEnd() == astNode.sourceEnd()) {
						if (i > 0) {
							ASTNode receiverReference = children.get(i - 1);
							IType[] receiverTypes = null;
							if (receiverReference instanceof VariableReference) {
								receiverTypes = PDTTypeInferenceUtils.getTypes((VariableReference) receiverReference, sourceModule);
							} else if (receiverReference instanceof PHPCallExpression) {
								receiverTypes = PDTTypeInferenceUtils.getTypes((PHPCallExpression) receiverReference, sourceModule);
							} else if (receiverReference instanceof TypeReference) {
								receiverTypes = PDTTypeInferenceUtils.getTypes((TypeReference) receiverReference, sourceModule);
							}
							if (receiverTypes != null) {
								for (IType receiverType: receiverTypes) {
									IType ancestorType = (IType) modelElement.getAncestor(IModelElement.TYPE);
									if (ancestorType != null && PDTModelUtils.isInstanceOf(receiverType, ancestorType)) {
										replaceEdit = createReplaceEdit(astNode);

										return false;
									}
								}
							}
						}
					}
				}
			}

			return true;
		}

		protected abstract ReplaceEdit createReplaceEdit(ASTNode astNode);
	}
}
