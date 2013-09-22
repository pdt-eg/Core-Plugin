/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.validation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.pdtextensions.semanticanalysis.IValidatorContext;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.validation.Identifier;
import org.pdtextensions.semanticanalysis.validation.Problem;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
final public class ValidatorContext implements IValidatorContext {
	private static final String[] emptyArguments = new String[0];
	private ModuleDeclaration moduleDeclaration;
	private final IBuildContext buildContext;
	private final boolean derived;
	private final Validator validator;
	private final Map<String, Type> types;
	private final Map<String, ProblemSeverity> severities;
	private final IValidatorManager manager;
	
	public ValidatorContext(Validator validator, IBuildContext buildContext, IValidatorManager manager) {
		this.buildContext = buildContext;
		this.validator = validator;
		this.derived = getSourceModule().getResource().isDerived(IResource.CHECK_ANCESTORS);
		this.types = new HashMap<String, Type>();
		this.severities = new HashMap<String, ProblemSeverity>();
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
	public void registerProblem(String type, int category,
			String message, int start, int stop) {
		registerProblem(type, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(String type, int category,
			String message, int start, int stop, int lineNumber) {
		registerProblem(type, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(String type, int category,
			String message, int start, int stop, String[] arguments) {
		registerProblem(type, category, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), arguments);
	}

	@Override
	public void registerProblem(String type, int category,
			String message, int start, int stop, int lineNumber,
			String[] arguments) {
		buildContext.getProblemReporter().reportProblem(new Problem(validator.getId(), getIdentifier(type), getSeverity(type), category, arguments, message, buildContext.getFileName(), start, stop, lineNumber));
	}
	
	private Identifier getIdentifier(String type) {
		if (!types.containsKey(type)) {
			types.put(type, validator.getType(type));
		}
		
		try {
			return types.get(type).getId();
		} catch (Exception e) {
			PEXAnalysisPlugin.error("Type not exists");
		}
		
		return null;
	}

	private ProblemSeverity getSeverity(String type) {
		if (!severities.containsKey(type)) {
			severities.put(type, manager.getSeverity(getProject(), validator, type));
		}
		
		return severities.get(type);
	}
}
