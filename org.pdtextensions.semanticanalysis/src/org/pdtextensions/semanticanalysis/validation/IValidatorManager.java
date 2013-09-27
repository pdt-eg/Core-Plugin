/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;

/**
 * Manager service interface
 * 
 * Allow to manipulate and manage PEX Validators
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public interface IValidatorManager {
	public final static String EXTENSION_ID = "org.pdtextensions.semanticanalysis.validator"; //$NON-NLS-1$
	public static final String DEFAULT_CATEGORY_ID = "org.pdtextensions.semanticanalysis.defaultCategory"; //$NON-NLS-1$
	
	public static final String DEFAULT_SEVERITY = "warning";

	public final static String ELEMENT_CATEGORY = "category"; //$NON-NLS-1$
	public final static String ELEMENT_VALIDATOR = "validator"; //$NON-NLS-1$
	public final static String ELEMENT_TYPE = "type"; //$NON-NLS-1$
	
	public final static String ATTR_ID = "id"; //$NON-NLS-1$
	public final static String ATTR_CLASS = "class"; //$NON-NLS-1$
	public final static String ATTR_LABEL = "label"; //$NON-NLS-1$
	public final static String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
	public final static String ATTR_CATEGORY = "categoryId"; //$NON-NLS-1$
	public final static String ATTR_DEFAULT_SEVERITY = "defaultSeverity"; //$NON-NLS-1$
	
	public boolean isEnabled(IScriptProject scriptProject);
	
	public Validator[] getValidators();
	
	public Validator[] getValidators(IScriptProject scriptProject);
	
	public Validator getValidator(String id);
	
	public Type getType(IValidatorIdentifier identifier);
	
	public IValidatorFactory getValidatorFactory(Validator validator) throws CoreException;
	
	public ProblemSeverity getSeverity(IScriptProject scriptProject, IValidatorIdentifier identifier);
	
	public ProblemSeverity getSeverity(IScriptProject scriptProject, Type type);
	
	public Category[] getCategories();
	
	public Category getCategory(String id);
}