/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis;

import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public interface IValidatorManager {
	public final static String Extension_ID = "org.pdtextensions.semanticanalysis.validator";
	
	public boolean isEnabled(IScriptProject scriptProject);
	
	public Validator[] getValidators();
	
	public Validator[] getValidators(IScriptProject scriptProject);
	
	public ProblemSeverity getSeverity(IScriptProject scriptProject, Validator validator, String type);
	
	public ProblemSeverity getSeverity(IScriptProject scriptProject, String validator, String type);
	
	public Category[] getCategories();
	
	public Category getCategory(String id);
	
	public Validator getValidator(String id);
}