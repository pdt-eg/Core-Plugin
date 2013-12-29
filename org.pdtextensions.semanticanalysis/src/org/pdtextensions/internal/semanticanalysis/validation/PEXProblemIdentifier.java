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
import org.pdtextensions.semanticanalysis.validation.validator.ImplementationValidator;
import org.pdtextensions.semanticanalysis.validation.validator.UsageValidator;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public enum PEXProblemIdentifier implements IValidatorIdentifier {
	INTERFACE_RELATED(false, "methods", ImplementationValidator.ID), //$NON-NLS-1$
	USAGE_RELATED(true, "use", UsageValidator.ID), //$NON-NLS-1$
	UNRESOVABLE(true, "use", UsageValidator.ID), //$NON-NLS-1$
	DUPLICATE(false, "duplicate", UsageValidator.ID); //$NON-NLS-1$

	public static final String MARKER_TYPE = "org.pdtextensions.semanticanalysis.problem"; //$NON-NLS-1$

	private final boolean isImport;
	private final String type;
	private final String validator;

	private PEXProblemIdentifier(boolean isImport, String type, String validator) {
		this.isImport = isImport;
		this.type = type;
		this.validator = validator;
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
		if (category == ProblemCategory.IMPORT && isImport) {
			return true;
		}

		return false;
	}

	@Override
	public String getMarkerType() {
		return MARKER_TYPE;
	}
}