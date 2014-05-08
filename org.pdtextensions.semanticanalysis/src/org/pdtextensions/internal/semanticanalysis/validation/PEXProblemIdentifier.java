/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.IProblemCategory;
import org.eclipse.dltk.compiler.problem.ProblemCategory;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.validation.IValidatorIdentifier;
import org.pdtextensions.semanticanalysis.validation.Problem;
import org.pdtextensions.semanticanalysis.validation.validator.ImplementationValidator;
import org.pdtextensions.semanticanalysis.validation.validator.UsageValidator;
import org.pdtextensions.semanticanalysis.validation.validator.VariableValidator;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public enum PEXProblemIdentifier implements IValidatorIdentifier {
	INTERFACE_RELATED("methods", Problem.CAT_RESTRICTION, ImplementationValidator.ID), //$NON-NLS-1$
	USAGE_RELATED("use", Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, UsageValidator.ID), //$NON-NLS-1$
	UNRESOVABLE("use", Problem.CAT_IMPORT, UsageValidator.ID), //$NON-NLS-1$
	DUPLICATE("duplicate", Problem.CAT_RESTRICTION, UsageValidator.ID),
	UNUSED_VARIABLE("unused_variable", Problem.CAT_UNNECESSARY_CODE, VariableValidator.ID),
	UNDEFINED_VARIABLE("undefined_variable", Problem.CAT_UNNECESSARY_CODE, VariableValidator.ID),
	UNINITIALIZED_VARIABLE("uninitialized_variable", Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, VariableValidator.ID); //$NON-NLS-1$

	public static final String MARKER_TYPE = "org.pdtextensions.semanticanalysis.problem"; //$NON-NLS-1$
	
	private final String type;
	private final String validator;
	private final int category;

	private PEXProblemIdentifier(String type, int category, String validator) {
		this.type = type;
		this.validator = validator;
		this.category = category;
	}

	@Override
	public String type() {
		return type;
	}

	@Override
	public String validator() {
		return validator;
	}

	@Override
	public String contributor() {
		return PEXAnalysisPlugin.PLUGIN_ID;
	}


	@Override
	public boolean belongsTo(IProblemCategory category) {
		if (category == ProblemCategory.IMPORT && this.category == Problem.CAT_IMPORT) {
			return true;
		}

		return false;
	}

	@Override
	public String getMarkerType() {
		return MARKER_TYPE;
	}

	@Override
	public int getCategory() {
		return category;
	}
}