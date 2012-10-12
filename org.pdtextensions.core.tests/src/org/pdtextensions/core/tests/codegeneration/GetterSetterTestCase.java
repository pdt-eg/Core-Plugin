package org.pdtextensions.core.tests.codegeneration;

import junit.framework.TestCase;

import org.junit.Test;
import org.pdtextensions.core.util.Inflector;

public class GetterSetterTestCase extends TestCase {
	
	@Test
	public void testInflector()
	{
		assertEquals("TransactionCode", Inflector.camelCase("_transaction_code"));
		assertEquals("transactionCode", Inflector.camelCase("transaction_code"));
		
		assertEquals("transaction_code", Inflector.underscore("transactionCode"));
		assertEquals("_transaction_code", Inflector.underscore("TransactionCode"));
	}

}
