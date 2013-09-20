/**
 */
package org.pdtextensions.semanticanalysis.model.validators.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Category</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class CategoryTest extends TestCase {

	/**
	 * The fixture for this Category test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Category fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(CategoryTest.class);
	}

	/**
	 * Constructs a new Category test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CategoryTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Category test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Category fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Category test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Category getFixture() {
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
		setFixture(ValidatorsFactory.eINSTANCE.createCategory());
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

} //CategoryTest
