/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.pdtextensions.semanticanalysis.IValidatorContext;
import org.pdtextensions.semanticanalysis.IValidatorParticipant;

/**
 * Basic Validator implementation
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
abstract public class AbstractValidator extends PHPASTVisitor implements IValidatorParticipant {
	
	protected IValidatorContext context;
	
	@Override
	public boolean allowDerived() {
		return false;
	}

	@Override
	public void validate(IValidatorContext context) throws Exception {
		this.context = context;
		context.getModuleDeclaration().traverse(this);
	}
	
}
