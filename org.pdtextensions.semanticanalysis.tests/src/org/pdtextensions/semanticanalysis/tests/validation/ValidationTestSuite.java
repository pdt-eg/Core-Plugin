package org.pdtextensions.semanticanalysis.tests.validation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ValidationTestSuite extends TestSuite {
	
    public static Test suite() {
        TestSuite suite = new TestSuite("org.pdtextensions.semanticanalysis.tests.validation");
        //suite.addTest(PHP5ErrorReportingTests.suite());
        //suite.addTest(PHP53ErrorReportingTests.suite());
        suite.addTest(PHP54ValidationReportingTests.suite());

        return suite;
    }	
    
    

}
