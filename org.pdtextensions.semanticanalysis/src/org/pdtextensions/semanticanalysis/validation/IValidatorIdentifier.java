/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierExtension;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierExtension3;

/**
 * Validation identifiers
 * 
 * Force users to select own marker (should should be subtype of org.eclipse.dltk.core.problem marker)
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public interface IValidatorIdentifier extends IProblemIdentifier, IProblemIdentifierExtension, IProblemIdentifierExtension3 {
	/**
	 * Due PDT limitation int representation of IProblemIdentifier is required :(
	 * @return
	 */
	int id();
	
	String type();
	
	String validator();
}
