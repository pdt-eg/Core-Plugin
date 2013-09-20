/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests.sample;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.pdtextensions.semanticanalysis.IValidatorFactory;
import org.pdtextensions.semanticanalysis.IValidatorParticipant;

public class ValidatorFactory implements IValidatorFactory {
	@Inject
	private IEclipseContext context;
	
	@Override
	public IValidatorParticipant getValidatorParticipant(IScriptProject scriptProject) {
		return null;
	}
	
	@PostConstruct
	public void test() {
		System.out.println("kurwa");
	}
	
	public IEclipseContext getContext() {
		return context;
	}
	
	
}
