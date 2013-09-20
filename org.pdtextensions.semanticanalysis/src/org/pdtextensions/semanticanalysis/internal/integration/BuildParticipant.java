/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.integration;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.CategorizedProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension2;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension3;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension4;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.internal.core.builder.BuildProblemReporter;
import org.eclipse.dltk.internal.core.builder.SourceModuleBuildContext;
import org.pdtextensions.core.CorePreferenceConstants;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.Problem;

/**
 * Build participant for semantic validators
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class BuildParticipant implements IBuildParticipant, IBuildParticipantExtension2, IBuildParticipantExtension3, IBuildParticipantExtension4 {
	@Inject
	private IValidatorManager manager;
	
	private IBuildChange buildChange;

	@Override
	public void build(IBuildContext context) throws CoreException {
		
		final ISourceModule sourceModule = context.getSourceModule();
		/*if (sourceModule.getResource().isDerived(IResource.CHECK_ANCESTORS)
				|| !CorePreferencesSupport.getInstance().getBooleanPreference(
						CorePreferenceConstants.PREF_SA_ENABLE, true,
						sourceModule.getScriptProject().getProject())) {
			return;
		}
		
		final ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
		
		try {
			moduleDeclaration.traverse(new UsageValidator(context,sourceModule, moduleDeclaration));
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
		}

		try {
			final ImplementationValidator visitor = new ImplementationValidator(sourceModule);
			moduleDeclaration.traverse(visitor);
			
			if (visitor.hasMissing() && visitor.getClassDeclaration() != null) {
				int start = visitor.getClassDeclaration().getNameStart();
				int end = visitor.getClassDeclaration().getNameEnd();

				String message = "The class " + visitor.getClassDeclaration().getName() + " must implement the inherited abstract method "
				+ visitor.getMissing().get(0).getFirstMethodName();
				context.getProblemReporter().reportProblem(
						new ValidationProblem(PEXProblem.INTERFACE_IMPLEMENTATION, 
						ProblemSeverity.WARNING, 
						CategorizedProblem.CAT_RESTRICTION, 
						new String[0], 
						message, 
						sourceModule.getElementName(), 
						visitor.getClassDeclaration().getNameStart(), 
						visitor.getClassDeclaration().getNameEnd(), 
						context.getLineTracker().getLineNumberOfOffset(visitor.getClassDeclaration().getNameStart())
						));
			}
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
		}*/
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
	public void prepare(IBuildChange buildChange, IBuildState buildState)
			throws CoreException {
		this.buildChange = buildChange;
	}

	@Override
	public void buildExternalModule(IBuildContext context) throws CoreException {
		
	}

	@Override
	public void notifyDependents(IBuildParticipant[] dependents) {
		
	}

	@Override
	public void afterBuild(IBuildContext context) {
		//clean old markers
		try {
			context.getSourceModule().getResource().deleteMarkers(Problem.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			PEXAnalysisPlugin.error(e);
		}
	}

}
