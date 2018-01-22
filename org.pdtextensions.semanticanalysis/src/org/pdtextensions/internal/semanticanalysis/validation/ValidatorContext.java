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
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.project.ProjectOptions;
import org.eclipse.php.core.validation.IProblemPreferences;
import org.eclipse.php.internal.core.PHPCorePlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
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
	private final ModuleDeclaration moduleDeclaration;
	private final IBuildContext buildContext;
	private final boolean derived;
	private final IValidatorManager manager;
	private ISourceModule workingCopy;
	private PHPVersion phpVersion;

	public ValidatorContext(Validator validator, ISourceModule workingCopy, IBuildContext buildContext, IValidatorManager manager) {
		this.buildContext = buildContext;
		this.derived = buildContext.getSourceModule().getResource().isDerived(IResource.CHECK_ANCESTORS);
		this.manager = manager;
		this.workingCopy = workingCopy;

		if (buildContext.get(IBuildContext.ATTR_MODULE_DECLARATION) != null) {
			this.moduleDeclaration = (ModuleDeclaration) buildContext.get(IBuildContext.ATTR_MODULE_DECLARATION);
		} else {
			this.moduleDeclaration = null;
		}
	}

	@Override
	public ModuleDeclaration getModuleDeclaration() {
		return moduleDeclaration;
	}

	@Override
	public ISourceModule getSourceModule() {
		return workingCopy;
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
	public PHPVersion getPHPVersion() {
		if (phpVersion == null) {
			phpVersion = ProjectOptions.getPHPVersion(getProject().getProject());
		}
		
		return phpVersion;
	}

	@Override
	public boolean isDerived() {
		return derived;
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier,
			String message, int start, int stop) {
		registerProblem(identifier,  message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier,
			String message, int start, int stop, int lineNumber) {
		registerProblem(identifier, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), emptyArguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier,
			String message, int start, int stop, String[] arguments) {
		registerProblem(identifier, message, start, stop, buildContext.getLineTracker().getLineNumberOfOffset(start), arguments);
	}

	@Override
	public void registerProblem(IValidatorIdentifier identifier,
			String message, int start, int stop, int lineNumber,
			String[] arguments) {

		buildContext.getProblemReporter().reportProblem(new Problem(identifier, getSeverity(identifier), identifier.getCategory(), arguments, message, buildContext.getFileName(), start, stop, lineNumber));
	}

	private ProblemSeverity getSeverity(IValidatorIdentifier identifier) {
		IProblemPreferences problemPreferences = PHPCorePlugin.getDefault().getProblemPreferences();
		return problemPreferences.getSeverity(identifier, problemPreferences.getScopeContexts(getProject().getProject()));
	}
}
