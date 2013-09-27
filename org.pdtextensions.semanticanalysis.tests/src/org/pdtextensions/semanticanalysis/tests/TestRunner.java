/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.pdtextensions.semanticanalysis.tests.validation.ManagerTest;
import org.pdtextensions.semanticanalysis.tests.validation.ValidationTestSuite;
import org.pdtextensions.semanticanalysis.tests.validation.ValidatorPreferenceTest;

@RunWith(Suite.class)
@SuiteClasses({ManagerTest.class, ValidatorPreferenceTest.class, ValidationTestSuite.class})
public class TestRunner {
}