/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.corext.refactoring.rename;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.MethodReferenceMatch;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.ui.refactoring.IPHPRefactorings;
import org.pdtextensions.core.ui.refactoring.IRefactoringProcessorIds;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.RefactoringCoreMessages;

/**
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class RenameMethodProcessor extends PHPRenameProcessor {
	private List<IMethod> overridingMethods;

	public RenameMethodProcessor(IMethod modelElement) {
		super(modelElement);
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkMethodName(newName);
	}

	@Override
	public String getIdentifier() {
		return IRefactoringProcessorIds.RENAME_METHOD_PROCESSOR;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameMethodRefactoring_name;
	}

	@Override
	public boolean needsSavedEditors() {
		return true;
	}

	@Override
	protected String getRefactoringId() {
		return IPHPRefactorings.RENAME_METHOD;
	}

	@Override
	protected RefactoringStatus renameDeclaration(IProgressMonitor pm) throws CoreException {
		RefactoringStatus result = super.renameDeclaration(pm);
		if (result.hasFatalError()) return result;

		for (IMethod overridingMethods: getOverridingMethods(pm)) {
			result = renameDeclaration(pm, overridingMethods, overridingMethods.getSourceModule());
			if (result.hasFatalError()) return result;
		}

		return result;
	}

	@Override
	protected RefactoringStatus updateReferences(IProgressMonitor pm) throws CoreException {
		RefactoringStatus result = updateReferences(pm, (IMethod) modelElement);
		if (result.hasFatalError()) return result;

		for (IMethod overridingMethods: getOverridingMethods(pm)) {
			result = updateReferences(pm, overridingMethods);
			if (result.hasFatalError()) return result;
		}

		return result;
	}

	private RefactoringStatus updateReferences(IProgressMonitor pm, final IMethod method) throws CoreException {
		new SearchEngine().search(
			SearchPattern.createPattern(
				method,
				IDLTKSearchConstants.REFERENCES,
				SearchPattern.R_FULL_MATCH,
				PHPLanguageToolkit.getDefault()
			),
			new SearchParticipant[]{ SearchEngine.getDefaultSearchParticipant() },
				SearchEngine.createWorkspaceScope(PHPLanguageToolkit.getDefault()),
				new SearchRequestor() {
					@Override
					public void acceptSearchMatch(final SearchMatch match) throws CoreException {
						if (match instanceof MethodReferenceMatch && ((MethodReferenceMatch) match).getNode() instanceof PHPCallExpression && match.getElement() instanceof IModelElement) {
							final ISourceModule module = (ISourceModule) ((IModelElement) match.getElement()).getAncestor(IModelElement.SOURCE_MODULE);
							if (module != null && RefactoringAvailabilityTester.isRenameAvailable(module)) {
								PHPCallExpression expression = (PHPCallExpression) ((MethodReferenceMatch) match).getNode();
								if (expression.getReceiver() == null) {
									if (expression.getCallName() instanceof FullyQualifiedReference) {
										int offset;
										if (((FullyQualifiedReference) expression.getCallName()).getNamespace() == null) {
											offset = expression.getCallName().sourceStart();
										} else {
											if ("\\".equals(((FullyQualifiedReference) expression.getCallName()).getNamespace().getName())) {
												offset = ((FullyQualifiedReference) expression.getCallName()).getNamespace().sourceEnd();
											} else {
												offset = ((FullyQualifiedReference) expression.getCallName()).getNamespace().sourceEnd() + 1;
											}
										}

										try {
											addTextEdit(
												changeManager.get(module),
												getProcessorName(),
												new ReplaceEdit(offset, method.getElementName().length(), getNewElementName())
											);
										} catch (MalformedTreeException e) {
											// conflicting update -> omit text match
										}
									}
								} else {
									IModelElement sourceElement = PDTModelUtils.getSourceElement(module, expression.getCallName().sourceStart(), expression.getCallName().matchLength());
									if (sourceElement != null && sourceElement.getElementType() == IModelElement.METHOD) {
										IType declaringType = ((IMethod) sourceElement).getDeclaringType();
										if (declaringType != null && PDTModelUtils.isInstanceOf(declaringType, method.getDeclaringType())) {
											try {
												addTextEdit(
													changeManager.get(module),
													getProcessorName(),
													new ReplaceEdit(expression.getCallName().sourceStart(), method.getElementName().length(), getNewElementName())
												);
											} catch (MalformedTreeException e) {
												// conflicting update -> omit text match
											}
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

	private List<IMethod> getOverridingMethods(IProgressMonitor pm) throws ModelException {
		if (overridingMethods == null) {
			overridingMethods = new ArrayList<IMethod>();
			IType declaringType = ((IMethod) modelElement).getDeclaringType();
			if (declaringType != null) {
				for (IType subType: declaringType.newTypeHierarchy(pm).getAllSubtypes(declaringType)) {
					for (IMethod methodBySubType: subType.getMethods()) {
						if (methodBySubType.getElementName().equals(getCurrentElementName())) {
							overridingMethods.add(methodBySubType);
							break;
						}
					}
				}
			}
		}

		return overridingMethods;
	}
}
