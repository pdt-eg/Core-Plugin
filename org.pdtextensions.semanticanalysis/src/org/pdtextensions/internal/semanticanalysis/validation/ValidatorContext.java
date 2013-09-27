/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.validation.IValidatorContext;
import org.pdtextensions.semanticanalysis.validation.IValidatorIdentifier;
import org.pdtextensions.semanticanalysis.validation.IValidatorManager;
import org.pdtextensions.semanticanalysis.validation.Problem;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
final public class ValidatorContext implements IValidatorContext {
	private static final String[] emptyArguments = new String[0];
	private ModuleDeclaration moduleDeclaration;
	private final IBuildContext buildContext;
	private final boolean derived;
	private final Map<IValidatorIdentifier, ProblemSeverity> severities;
	private final IValidatorManager manager;
	
	public ValidatorContext(Validator validator, IBuildContext buildContext, IValidatorManager manager) {
		this.buildContext = buildContext;
		this.derived = getSourceModule().getResource().isDerived(IResource.CHECK_ANCESTORS);
		this.severities = new HashMap<IValidatorIdentifier, ProblemSeverity>();
		this.manager = manager;
	}
	
	@Override
	public ModuleDeclaration getModuleDeclaration() {
		if (moduleDeclaration == null) {
			 moduleDeclaration = SourceParserUtil.getModuleDeclaration(buildContext.getSourceModule());
		}
		
		return moduleDeclaration;
	}
	
	@Override
	public ISourceModule getSourceModule() {
		return buildContext.getSourceModule();
	}

	@Override
	public int getBuildType() {
		return buildContext.getBuildType();
	}

	@Override
	public IBuildContext getRawContext() {
		return buildContext;
	}

	@Override
	public IScriptProject getProject() {
		return buildContext.getSourceModule().getScriptProject();
	}

	@Override
	public boolean isDerived() {
		return derived;
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier, int category,
			String message, int start, int stop) {
		registerProblem(identifier, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier, int category,
			String message, int start, int stop, int lineNumber) {
		registerProblem(identifier, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier, int category,
			String message, int start, int stop, String[] arguments) {
		registerProblem(identifier, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), arguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier, int category,
			String message, int start, int stop, int lineNumber,
			String[] arguments) {
		buildContext.getProblemReporter().reportProblem(new Problem(identifier, getSeverity(identifier), category, arguments, message, buildContext.getFileName(), start, stop, lineNumber));
	}

	private ProblemSeverity getSeverity(IValidatorIdentifier identifier) {
		if (!severities.containsKey(identifier)) {
			severities.put(identifier, manager.getSeverity(getProject(), manager.getType(identifier)));
		}
		
		return severities.get(identifier);
	}
}
