/**
 */
package org.pdtextensions.semanticanalysis.model.validators.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Validator</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ValidatorTest extends TestCase {

	/**
	 * The fixture for this Validator test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Validator fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ValidatorTest.class);
	}

	/**
	 * Constructs a new Validator test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValidatorTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Validator test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Validator fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Validator test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Validator getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ValidatorsFactory.eINSTANCE.createValidator());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //ValidatorTest
