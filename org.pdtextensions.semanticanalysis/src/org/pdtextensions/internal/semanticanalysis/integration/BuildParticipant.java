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
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension2;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension3;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension4;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.internal.core.ModelManager;
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
public class BuildParticipant implements IBuildParticipantExtension4, IBuildParticipantExtension2, IBuildParticipantExtension3 {
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

	@Override
	public void notifyDependents(IBuildParticipant[] dependents) {
	}

	@Override
	public void afterBuild(IBuildContext context) {

	}

	@Override
	public boolean beginBuild(int buildType) {
		return true;
	}

	@Override
	public void endBuild(IProgressMonitor monitor) {

	}
	
	@Override
	public void clean() {
		tmpTypes.clear();
	}

	@Override
	public void prepare(IBuildChange buildChange, IBuildState buildState) throws CoreException {
		if (buildChange.isDependencyBuild()) {
			ModelManager.getModelManager().getIndexManager().waitUntilReady();
			for (ISourceModule file : buildChange.getSourceModules(IBuildChange.DEFAULT)) {
				report(file, buildState);
			}
		}
	}

	@Override
	public void buildExternalModule(IBuildContext context) throws CoreException {
	}

	private void report(final ISourceModule module, final IBuildState state) {
		final ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
		try {
			moduleDeclaration.traverse(new Visitor(module, state));
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
		}
	}

	

	private class Visitor extends PHPASTVisitor {
		final private IBuildState state;
		final private ISourceModule module;

		public Visitor(final ISourceModule module, final IBuildState state) {
			this.state = state;
			this.module = module;
		}

		@Override
		public boolean visit(UsePart s) throws Exception {
			final String searchString = s.getNamespace().getFullyQualifiedName();
			if (!tmpTypes.containsKey(searchString)) {
				resolve(searchString, tmpTypes.put(searchString, new Files()));
			}
			final Files files = tmpTypes.get(searchString);
			
			for (IPath f : files) {
				state.recordDependency(getModulePath(), f);
			}
			
			return true;
		}
		
		private void resolve(String searchString, Files files) throws ModelException {
			registerTypes(PDTModelUtils.findTypes(module.getScriptProject(), searchString), files);
			if (files.size() == 0) {
				registerTypes(PDTModelUtils.findTypes(module.getScriptProject(), searchString, true), files);
			}
		}

		private void registerTypes(IType[] types, Files files) {
			for (IType type : types) {
				if (getModulePath().equals(getTypePath(type))) {
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
