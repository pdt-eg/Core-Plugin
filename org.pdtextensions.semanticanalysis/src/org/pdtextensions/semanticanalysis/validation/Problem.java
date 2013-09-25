/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.CategorizedProblem;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;

public class Problem extends CategorizedProblem {

	private int categoryId = CategorizedProblem.CAT_UNSPECIFIED;
	private final IValidatorIdentifier identifier;
	
	private String filename;
	private String message;
	private String[] arguments;
	private int sourceStart;
	private int sourceEnd;
	private int lineNumber;
	private ProblemSeverity severity;
	

	public Problem(final IValidatorIdentifier identifier, ProblemSeverity severity,
			int categoryId, String[] arguments, String message,
			String filename, int sourceStart, int sourceEnd, int lineNumber) {
		super();
		//super(filename, message, identifier, arguments, severity, sourceStart, sourceEnd, lineNumber, 0);
		//super(filename, message, identifier.id(), arguments, severity, sourceStart, sourceEnd, lineNumber);

		this.identifier = identifier;
		this.categoryId = categoryId;
		this.filename = filename;
		this.message = message;
		this.arguments = arguments;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.lineNumber = lineNumber;
		this.severity = severity;
	}

	@Override
	public boolean isTask() {
		return false;
	}

	@Override
	public int getCategoryID() {
		return categoryId;
	}

	@Override
	public String getMarkerType() {
		return identifier.getMarkerType();
	}

	@Override
	public String[] getArguments() {
		return arguments;
	}

	@Override
	public IProblemIdentifier getID() {
		return identifier;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getOriginatingFileName() {
		return filename;
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
		this.severity = severity;
	}

	@Override
	public boolean isError() {
		return severity == ProblemSeverity.ERROR;
	}

	@Override
	public boolean isWarning() {
		return severity == ProblemSeverity.WARNING;
	}

	@Override
	public void setSourceEnd(int sourceEnd) {
		this.sourceEnd = sourceEnd;
	}

	@Override
	public void setSourceLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public void setSourceStart(int sourceStart) {
		this.sourceStart = sourceStart;
	}
}
