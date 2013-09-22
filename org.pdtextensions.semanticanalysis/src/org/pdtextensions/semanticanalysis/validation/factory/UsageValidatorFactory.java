/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation.factory;

import org.eclipse.dltk.core.IScriptProject;
import org.pdtextensions.semanticanalysis.IValidatorFactory;
import org.pdtextensions.semanticanalysis.IValidatorParticipant;
import org.pdtextensions.semanticanalysis.validation.validator.UsageValidator;

public class UsageValidatorFactory implements IValidatorFactory {

	@Override
	public IValidatorParticipant getValidatorParticipant(IScriptProject scriptProject) {
		return new UsageValidator();
	}

}
