/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierFactory;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class PEXProblemIdentifierFactory implements IProblemIdentifierFactory {

	@Override
	public IProblemIdentifier valueOf(String localName) throws IllegalArgumentException {
		return PEXProblemIdentifier.valueOf(localName);
	}

	@Override
	public IProblemIdentifier[] values() {
		return PEXProblemIdentifier.values();
	}

}
