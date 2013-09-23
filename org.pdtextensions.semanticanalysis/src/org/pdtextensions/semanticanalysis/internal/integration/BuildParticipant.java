/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.integration;

import javax.inject.Inject;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension2;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension3;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.IValidatorParticipant;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.internal.validation.ValidatorContext;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.validation.Problem;

/**
 * Build participant for semantic validators
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class BuildParticipant implements IBuildParticipant, IBuildParticipantExtension2, IBuildParticipantExtension3 {
	@Inject
	private IValidatorManager manager;
	
	private IBuildChange buildChange;

	@Override
	public void build(IBuildContext context) throws CoreException {
		if (context.getBuildType() != IBuildContext.RECONCILE_BUILD) {
			try {
				context.getSourceModule().getResource().deleteMarkers(Problem.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
			} catch(CoreException e) {
				PEXAnalysisPlugin.error(e);
			}
		}
		if (!context.getSourceModule().getScriptProject().isOnBuildpath(context.getSourceModule().getResource()) || !manager.isEnabled(context.getSourceModule().getScriptProject())) {
			return;
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

	/**
	 * clear all markers in project
	 */
	@Override
	public void clean() {
		try {
			if (buildChange != null && buildChange.getProject() != null) {
				buildChange.getProject().deleteMarkers(Problem.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
			}
		} catch (CoreException e) {
			PEXAnalysisPlugin.error(e);
		}
	}

	@Override
	public boolean beginBuild(int buildType) {
		return true;
	}

	@Override
	public void endBuild(IProgressMonitor monitor) {
		buildChange = null;
	}

	@Override
	public void prepare(IBuildChange buildChange, IBuildState buildState) throws CoreException {
		if (buildChange.getBuildType() != IBuildContext.INCREMENTAL_BUILD) {
			for (ISourceModule file : buildChange.getSourceModules(IBuildChange.DEFAULT)) {
				report(file, buildState);
			}
		}
		
		this.buildChange = buildChange;
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
			String searchString = s.getNamespace().getFullyQualifiedName();

			for (IType type : PDTModelUtils.findTypes(module.getScriptProject(), searchString)) {
				if (allow(module, type)) {
					state.recordDependency(module.getResource().getFullPath(), type.getResource() == null ? type.getPath() : type.getResource().getFullPath());
					return true;
				}
			}

			for (IType type : PDTModelUtils.findTypes(module.getScriptProject(), searchString, true)) {
				if (allow(module, type)) {
					state.recordDependency(module.getResource().getFullPath(), type.getResource() == null ? type.getPath() : type.getResource().getFullPath());
					return true;
				}
			}
			
			return true;
		}
	}
	
	public boolean allow(ISourceModule module, IType type) {
		return !module.getResource().getFullPath().equals(type.getResource() == null ? type.getPath() : type.getResource().getFullPath());
	}

}
