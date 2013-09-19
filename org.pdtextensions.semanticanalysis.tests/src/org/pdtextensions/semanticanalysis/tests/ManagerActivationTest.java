/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.tests.sample.Service;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class ManagerActivationTest {
	@Test
	public void testDI() throws InvalidSyntaxException {
		Service testService = ContextInjectionFactory.make(Service.class,
				EclipseContextFactory.getServiceContext(FrameworkUtil
						.getBundle(PEXAnalysisTestPlugin.class)
						.getBundleContext()));
		assertNotNull(testService.getManager());
	}
}
