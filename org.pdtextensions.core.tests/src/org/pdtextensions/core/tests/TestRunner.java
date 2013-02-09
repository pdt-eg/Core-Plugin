package org.pdtextensions.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.pdtextensions.core.tests.codegeneration.GetterSetterTestCase;
import org.pdtextensions.core.tests.validation.ValidationTestSuite;

@RunWith(Suite.class)
@SuiteClasses({GetterSetterTestCase.class, ValidationTestSuite.class})
public class TestRunner {

}