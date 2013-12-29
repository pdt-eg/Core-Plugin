/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension3;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.php.internal.core.PHPCorePlugin;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.internal.semanticanalysis.validation.ValidatorContext;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.validation.IValidatorManager;
import org.pdtextensions.semanticanalysis.validation.IValidatorParticipant;

/**
 * Build participant for semantic validators
 *
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class BuildParticipant implements IBuildParticipant, IBuildParticipantExtension3 {
	@Inject
	private IValidatorManager manager;

	private Map<String, Files> tmpTypes = new HashMap<String, Files>();

	@Override
	public void build(IBuildContext context) throws CoreException {
		if (!context.getSourceModule().getScriptProject().isOnBuildpath(context.getSourceModule().getResource())
				|| !manager.isEnabled(context.getSourceModule().getScriptProject())) {
			return;
		}
		if (context.getBuildType() != IBuildContext.RECONCILE_BUILD) {
			ModelManager.getModelManager().getIndexManager().waitUntilReady();
		}

		for (Validator validator : manager.getValidators(context.getSourceModule().getScriptProject())) {
			final ValidatorContext validatorContext = new ValidatorContext(validator, context, manager);

			validate(validatorContext, validator.getValidatorFactory().getValidatorParticipant(validatorContext.getProject()));
		}

		// dependency report
		if (context.getBuildType() == IBuildContext.RECONCILE_BUILD) {
			return;
		}

		ModuleDeclaration moduleDeclaration = (ModuleDeclaration) context.get(IBuildContext.ATTR_MODULE_DECLARATION);
		if (moduleDeclaration != null) {
			try {
				moduleDeclaration.traverse(new UsageVisitor(context));
			} catch (Exception e) {
				PEXAnalysisPlugin.error("Exception during dependency detection", e); //$NON-NLS-1$
			}
		}
	}

	private void validate(final ValidatorContext validatorContext, final IValidatorParticipant validatorParticipant) {
		// if null ignore
		if (validatorParticipant == null) {
			return;
		}

		if (validatorContext.isDerived() && !validatorParticipant.allowDerived()) {
			return;
		}

		try {
			validatorParticipant.validate(validatorContext);
		} catch (Exception e) {
			PEXAnalysisPlugin.error("Exception during validation", e); //$NON-NLS-1$
		}
	}

	/**
	 * Do not run if toolkit is not ready
	 */
	@Override
	public boolean beginBuild(int buildType) {
		return PHPCorePlugin.toolkitInitialized;
	}

	@Override
	public void endBuild(IProgressMonitor monitor) {

	}

	@Override
	public void clean() {
		tmpTypes.clear();
	}

	/**
	 * TODO: Detect real use (not only imports)
	 */
	private class UsageVisitor extends PHPASTVisitor {
		final private IBuildContext context;
		final private ISourceModule module;

		public UsageVisitor(final IBuildContext context) {
			this.context = context;
			this.module = context.getSourceModule();
		}

		@Override
		public boolean visit(UsePart s) throws Exception {
			final String searchString = s.getNamespace().getFullyQualifiedName();
			if (!tmpTypes.containsKey(searchString)) {
				final Files files = new Files();
				tmpTypes.put(searchString, files);
				resolve(searchString, files);
			}
			final Files files = tmpTypes.get(searchString);
			for (IPath f : files) {
				context.recordDependency(f, IBuildState.STRUCTURAL); // mark as dependecy as structural
			}
			return true;
		}

		private void resolve(String searchString, Files files) throws ModelException {
			registerTypes(PDTModelUtils.findTypes(module.getScriptProject(), searchString), files);
		}

		private void registerTypes(IType[] types, Files files) {
			for (IType type : types) {
				if (!getModulePath().equals(getTypePath(type))) {
					files.add(getTypePath(type));
				}
			}
		}

		private IPath getModulePath() {
			return module.getResource().getFullPath();
		}

		private IPath getTypePath(IType type) {
			return type.getResource() == null ? type.getPath() : type.getResource().getFullPath();
		}
	}

	private class Files extends HashSet<IPath> {
		private static final long serialVersionUID = -4757183524417278724L;
	}
}
