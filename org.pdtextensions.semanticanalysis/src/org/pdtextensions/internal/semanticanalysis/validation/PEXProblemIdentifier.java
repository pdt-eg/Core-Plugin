/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.dltk.compiler.problem.IProblemCategory;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemCategory;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.validation.IValidatorIdentifier;
import org.pdtextensions.semanticanalysis.validation.validator.ImplementationValidator;
import org.pdtextensions.semanticanalysis.validation.validator.UsageValidator;

/**
 * Currently is not possible to use ProblemIdentifier as enum :(
 * 
 * The reason is PDT limitation
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class PEXProblemIdentifier implements IValidatorIdentifier {
	public static final String MARKER_TYPE = "org.pdtextensions.semanticanalysis.problem"; //$NON-NLS-1$
	private static Set<PEXProblemIdentifier> values = new HashSet<PEXProblemIdentifier>();
	
	private static int curr = 100000;
	public static final PEXProblemIdentifier INTERFACE_RELATED = new PEXProblemIdentifier(true, "methods", ImplementationValidator.ID); //$NON-NLS-1$
	public static final PEXProblemIdentifier USAGE_RELATED = new PEXProblemIdentifier(true, "use", UsageValidator.ID); //$NON-NLS-1$
	public static final PEXProblemIdentifier UNRESOVABLE = new PEXProblemIdentifier(true, "use", UsageValidator.ID); //$NON-NLS-1$
	public static final PEXProblemIdentifier DUPLICATE = new PEXProblemIdentifier(false, "duplicate", UsageValidator.ID); //$NON-NLS-1$

	private final boolean isImport;
	private final int id;
	private final String type;
	private final String validator;

	protected PEXProblemIdentifier(boolean isImport, String type, String validator) {
		this.isImport = isImport;
		this.id = curr++;
		this.type = type;
		this.validator = validator;
		values.add(this);
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
	public int hashCode() {
		return id;
	}
	@Override
	public int id() {
		return id;
	}

	@Override
	public boolean belongsTo(IProblemCategory category) {
		if (category.equals(ProblemCategory.IMPORT) && isImport) {
			return true;
		}

		return false;
	}

	@Override
	public String getMarkerType() {
		return MARKER_TYPE;
	}

	@Override
	public String name() {
		return String.valueOf(id); //$NON-NLS-1$
	}

	public static IProblemIdentifier valueOf(String localName) throws IllegalArgumentException {
		for (IProblemIdentifier ident : values) {
			if (ident.name().equals(localName)) {
				return ident;
			}
		}
		
		return null;
	}

	public static IProblemIdentifier[] values() {
		return values.toArray(new IProblemIdentifier[values.size()]);
	}
}