/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildContext;

/**
 * Validator context for each validator participant
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public interface IValidatorContext {
	/**
	 * Current module declaration. Created on demand
	 */
	public ModuleDeclaration getModuleDeclaration();

	/**
	 * Current source module
	 */
	public ISourceModule getSourceModule();

	public IScriptProject getProject();

	public boolean isDerived();

	public int getBuildType();

	public IBuildContext getRawContext();

	public void registerProblem(IValidatorIdentifier identifier, int category, String message, int start, int stop);

	public void registerProblem(IValidatorIdentifier identifier, int category, String message, int start, int stop, int lineNumber);

	public void registerProblem(IValidatorIdentifier identifier, int category, String message, int start, int stop, String[] arguments);

	public void registerProblem(IValidatorIdentifier identifier, int category, String message, int start, int stop, int lineNumber, String[] arguments);
}
