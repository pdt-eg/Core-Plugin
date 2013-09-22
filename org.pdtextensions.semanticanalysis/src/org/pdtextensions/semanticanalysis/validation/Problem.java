/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.problem.CategorizedProblem;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;

public class Problem extends CategorizedProblem {

	public static final String MARKER_TYPE_PREFIX = "org.pdtextensions.semanticanalysis"; //$NON-NLS-1$

	public static final String MARKER_TYPE = "org.pdtextensions.semanticanalysis.problem";

	private IProblemIdentifier id;
	private String[] arguments;
	private String message;
	private String fileName;
	private int sourceEnd;
	private int sourceStart;
	private int lineNumber;
	private ProblemSeverity severity;
	private int categoryId = CategorizedProblem.CAT_UNSPECIFIED;
	private String validator;

	public Problem(String validator, IProblemIdentifier id, ProblemSeverity severity,
			int categoryId, String[] arguments, String message,
			String filename, int sourceStart, int sourceEnd, int lineNumber) {
		super();
		this.id = id;
		this.severity = severity;
		this.categoryId = categoryId;
		this.arguments = arguments;
		this.message = message;
		this.fileName = filename;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.lineNumber = lineNumber;
		this.validator = validator;
	}

	@Override
	public String[] getArguments() {
		return arguments;
	}

	@Override
	public IProblemIdentifier getID() {
		return id;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String getOriginatingFileName() {
		return fileName;
	}

	@Override
	public int getSourceEnd() {
		return sourceEnd;
	}

	@Override
	public int getSourceLineNumber() {
		return lineNumber;
	}

	@Override
	public int getSourceStart() {
		return sourceStart;
	}

	@Override
	public void setSeverity(ProblemSeverity severity) {
		Assert.isNotNull(severity);
		this.severity = severity;
	}
	
	@Override
	public ProblemSeverity getSeverity() {
		return this.severity;
	}

	@Override
	public boolean isError() {
		return ProblemSeverity.ERROR.equals(severity);
	}

	@Override
	public boolean isWarning() {
		return ProblemSeverity.WARNING.equals(severity);
	}

	@Override
	public boolean isTask() {
		return false;
	}

	@Override
	public void setSourceEnd(int sourceEnd) {
		Assert.isLegal(sourceEnd >= 0);
		this.sourceEnd = sourceEnd;

	}

	@Override
	public void setSourceLineNumber(int lineNumber) {
		Assert.isLegal(lineNumber >= 0);
		this.lineNumber = lineNumber;

	}

	@Override
	public void setSourceStart(int sourceStart) {
		Assert.isLegal(sourceStart >= 0);
		this.sourceStart = sourceStart;
	}

	@Override
	public int getCategoryID() {
		return categoryId;
	}

	@Override
	public String getMarkerType() {
		return MARKER_TYPE;
	}
	
	@Override
	public String[] getExtraMarkerAttributeNames() {
		return new String[] {"validator"};
	}
	
	@Override
	public Object[] getExtraMarkerAttributeValues() {
		return new String[] {validator};
	}
	
	public String getValidator() {
		return validator;
	}

}
