/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.validation;

/**
 * Interface for validator participant
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public interface IValidatorParticipant {
	public boolean allowDerived();
	
	public void validate(IValidatorContext context) throws Exception;
}
