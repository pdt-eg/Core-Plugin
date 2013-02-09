package org.pdtextensions.core.tests.validation;

import org.eclipse.php.core.tests.errors.PHP53ErrorReportingTests;
import org.eclipse.php.core.tests.errors.PHP54ErrorReportingTests;
import org.eclipse.php.core.tests.errors.PHP5ErrorReportingTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ValidationTestSuite extends TestSuite {
	
    public static Test suite() {
        TestSuite suite = new TestSuite("org.pdtextensions.core.tests.validation");
        
        suite.addTest(PHP5ErrorReportingTests.suite());
        suite.addTest(PHP53ErrorReportingTests.suite());
        suite.addTest(PHP54ErrorReportingTests.suite());
        
        return suite;
    }	
    
    

}
