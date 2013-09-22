/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.IProblemCategory;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierExtension;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierExtension3;
import org.eclipse.dltk.compiler.problem.ProblemCategory;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;

/**
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
final public class Identifier implements IProblemIdentifier, IProblemIdentifierExtension, IProblemIdentifierExtension3 {
	
	//final public static Identifier INTERFACE_IMPLEMENTATION = new Identifier(false, 0x01100000);
	//final public static Identifier USAGE_RELATED = new Identifier(true, 0x01200000);
	//final public static Identifier UNRESOVABLE = new Identifier(true, 0x01300000);
	//final public static Identifier DUPLICATED_USE = new Identifier(false, 0x01400000);
	public static final String ID_SEPARATOR = "/"; //$NON-NLS-1$
	private final boolean isImport;
	private final Integer id;
	
	public Identifier(final boolean isImport, final int id) {
		this.isImport = isImport;
		this.id = id;
	}
	
	@Override
	public String contributor() {
		return PEXAnalysisPlugin.PLUGIN_ID;
	}
	
	@Override
	public final String name() {
		return id.toString();
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
		return Problem.MARKER_TYPE;
	}
	
}