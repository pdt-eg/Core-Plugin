/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.tests.sample.Service;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class ManagerTest {
	private static Service testService;

	@BeforeClass
	public static void beforeClass() {
		testService = ContextInjectionFactory.make(Service.class,
				EclipseContextFactory.getServiceContext(FrameworkUtil
						.getBundle(PEXAnalysisTestPlugin.class)
						.getBundleContext()));
	}

	@AfterClass
	public static void afterClass() {
		testService = null;
	}

	@Test
	public void DI() throws InvalidSyntaxException {
		assertNotNull(testService.getManager());
	}

	@Test
	public void initializationCategories() {
		String defaultId = "org.pdtextensions.semanticanalysis.defaultCategory";
		Category cat = testService.getManager().getCategory(defaultId);

		assertNotNull(cat);
		assertEquals(cat.getId(), defaultId);
	}

	@Test
	public void initializeValidator() {
		String validatorId = "org.pdtextensions.semanticanalysis.tests.validator";
		Validator validator = testService.getManager()
				.getValidator(validatorId);

		assertNotNull(validator);
		assertEquals(ProblemSeverity.INFO, validator.getDefaultSeverity());
		assertEquals("org.pdtextensions.semanticanalysis.defaultCategory",
				validator.getCategory().getId());
	}

}
