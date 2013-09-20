/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsFactory
 * @model kind="package"
 * @generated
 */
public interface ValidatorsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "validators";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://validators/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "validators";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidatorsPackage eINSTANCE = org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl <em>Validator</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getValidator()
	 * @generated
	 */
	int VALIDATOR = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__ID = 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__LABEL = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Default Severity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__DEFAULT_SEVERITY = 3;

	/**
	 * The feature id for the '<em><b>Validator Factory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__VALIDATOR_FACTORY = 4;

	/**
	 * The feature id for the '<em><b>Categories</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__CATEGORIES = 5;

	/**
	 * The feature id for the '<em><b>Validators</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__VALIDATORS = 6;

	/**
	 * The feature id for the '<em><b>Category</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__CATEGORY = 7;

	/**
	 * The number of structural features of the '<em>Validator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Validator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.CategoryImpl <em>Category</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.CategoryImpl
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getCategory()
	 * @generated
	 */
	int CATEGORY = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__ID = 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__LABEL = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Validators</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__VALIDATORS = 3;

	/**
	 * The number of structural features of the '<em>Category</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Category</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '<em>Validator Factory</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.IValidatorFactory
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getValidatorFactory()
	 * @generated
	 */
	int VALIDATOR_FACTORY = 2;

	/**
	 * The meta object id for the '<em>Status</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.compiler.problem.ProblemSeverity
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getStatus()
	 * @generated
	 */
	int STATUS = 3;


	/**
	 * Returns the meta object for class '{@link org.pdtextensions.semanticanalysis.model.validators.Validator <em>Validator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validator</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator
	 * @generated
	 */
	EClass getValidator();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getId()
	 * @see #getValidator()
	 * @generated
	 */
	EAttribute getValidator_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getLabel()
	 * @see #getValidator()
	 * @generated
	 */
	EAttribute getValidator_Label();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getDescription()
	 * @see #getValidator()
	 * @generated
	 */
	EAttribute getValidator_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDefaultSeverity <em>Default Severity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Severity</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getDefaultSeverity()
	 * @see #getValidator()
	 * @generated
	 */
	EAttribute getValidator_DefaultSeverity();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getValidatorFactory <em>Validator Factory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Validator Factory</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getValidatorFactory()
	 * @see #getValidator()
	 * @generated
	 */
	EAttribute getValidator_ValidatorFactory();

	/**
	 * Returns the meta object for the reference '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategories <em>Categories</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Categories</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getCategories()
	 * @see #getValidator()
	 * @generated
	 */
	EReference getValidator_Categories();

	/**
	 * Returns the meta object for the reference list '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getValidators <em>Validators</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Validators</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getValidators()
	 * @see #getValidator()
	 * @generated
	 */
	EReference getValidator_Validators();

	/**
	 * Returns the meta object for the reference '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategory <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Category</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getCategory()
	 * @see #getValidator()
	 * @generated
	 */
	EReference getValidator_Category();

	/**
	 * Returns the meta object for class '{@link org.pdtextensions.semanticanalysis.model.validators.Category <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Category</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category
	 * @generated
	 */
	EClass getCategory();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category#getId()
	 * @see #getCategory()
	 * @generated
	 */
	EAttribute getCategory_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category#getLabel()
	 * @see #getCategory()
	 * @generated
	 */
	EAttribute getCategory_Label();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category#getDescription()
	 * @see #getCategory()
	 * @generated
	 */
	EAttribute getCategory_Description();

	/**
	 * Returns the meta object for the containment reference list '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getValidators <em>Validators</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Validators</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category#getValidators()
	 * @see #getCategory()
	 * @generated
	 */
	EReference getCategory_Validators();

	/**
	 * Returns the meta object for data type '{@link org.pdtextensions.semanticanalysis.IValidatorFactory <em>Validator Factory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Validator Factory</em>'.
	 * @see org.pdtextensions.semanticanalysis.IValidatorFactory
	 * @model instanceClass="org.pdtextensions.semanticanalysis.IValidatorFactory"
	 * @generated
	 */
	EDataType getValidatorFactory();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.dltk.compiler.problem.ProblemSeverity <em>Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Status</em>'.
	 * @see org.eclipse.dltk.compiler.problem.ProblemSeverity
	 * @model instanceClass="org.eclipse.dltk.compiler.problem.ProblemSeverity"
	 * @generated
	 */
	EDataType getStatus();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ValidatorsFactory getValidatorsFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl <em>Validator</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getValidator()
		 * @generated
		 */
		EClass VALIDATOR = eINSTANCE.getValidator();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__ID = eINSTANCE.getValidator_Id();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__LABEL = eINSTANCE.getValidator_Label();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__DESCRIPTION = eINSTANCE.getValidator_Description();

		/**
		 * The meta object literal for the '<em><b>Default Severity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__DEFAULT_SEVERITY = eINSTANCE.getValidator_DefaultSeverity();

		/**
		 * The meta object literal for the '<em><b>Validator Factory</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__VALIDATOR_FACTORY = eINSTANCE.getValidator_ValidatorFactory();

		/**
		 * The meta object literal for the '<em><b>Categories</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR__CATEGORIES = eINSTANCE.getValidator_Categories();

		/**
		 * The meta object literal for the '<em><b>Validators</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR__VALIDATORS = eINSTANCE.getValidator_Validators();

		/**
		 * The meta object literal for the '<em><b>Category</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR__CATEGORY = eINSTANCE.getValidator_Category();

		/**
		 * The meta object literal for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.CategoryImpl <em>Category</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.CategoryImpl
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getCategory()
		 * @generated
		 */
		EClass CATEGORY = eINSTANCE.getCategory();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATEGORY__ID = eINSTANCE.getCategory_Id();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATEGORY__LABEL = eINSTANCE.getCategory_Label();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATEGORY__DESCRIPTION = eINSTANCE.getCategory_Description();

		/**
		 * The meta object literal for the '<em><b>Validators</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATEGORY__VALIDATORS = eINSTANCE.getCategory_Validators();

		/**
		 * The meta object literal for the '<em>Validator Factory</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.pdtextensions.semanticanalysis.IValidatorFactory
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getValidatorFactory()
		 * @generated
		 */
		EDataType VALIDATOR_FACTORY = eINSTANCE.getValidatorFactory();

		/**
		 * The meta object literal for the '<em>Status</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.compiler.problem.ProblemSeverity
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getStatus()
		 * @generated
		 */
		EDataType STATUS = eINSTANCE.getStatus();

	}

} //ValidatorsPackage
