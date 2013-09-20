/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage
 * @generated
 */
public interface ValidatorsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidatorsFactory eINSTANCE = org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Validator</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Validator</em>'.
	 * @generated
	 */
	Validator createValidator();

	/**
	 * Returns a new object of class '<em>Category</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Category</em>'.
	 * @generated
	 */
	Category createCategory();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ValidatorsPackage getValidatorsPackage();

} //ValidatorsFactory
