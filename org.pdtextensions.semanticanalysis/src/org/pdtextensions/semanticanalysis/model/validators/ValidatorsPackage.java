/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
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
	 * The feature id for the '<em><b>Validator Factory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__VALIDATOR_FACTORY = 1;

	/**
	 * The feature id for the '<em><b>Category</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__CATEGORY = 2;

	/**
	 * The feature id for the '<em><b>Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR__TYPES = 3;

	/**
	 * The number of structural features of the '<em>Validator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_FEATURE_COUNT = 4;

	/**
	 * The operation id for the '<em>Get Type</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR___GET_TYPE__STRING = 0;

	/**
	 * The number of operations of the '<em>Validator</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_OPERATION_COUNT = 1;

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
	 * The feature id for the '<em><b>Validators</b></em>' reference list.
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
	 * The meta object id for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl <em>Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getType()
	 * @generated
	 */
	int TYPE = 2;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__LABEL = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Default Severity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__DEFAULT_SEVERITY = 2;

	/**
	 * The feature id for the '<em><b>Validator</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__VALIDATOR = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__ID = 4;

	/**
	 * The feature id for the '<em><b>Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__NUM = 5;

	/**
	 * The feature id for the '<em><b>Import</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__IMPORT = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE__NAME = 7;

	/**
	 * The number of structural features of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_FEATURE_COUNT = 8;

	/**
	 * The number of operations of the '<em>Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TYPE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '<em>Validator Factory</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.IValidatorFactory
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getValidatorFactory()
	 * @generated
	 */
	int VALIDATOR_FACTORY = 3;

	/**
	 * The meta object id for the '<em>Status</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.compiler.problem.ProblemSeverity
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getStatus()
	 * @generated
	 */
	int STATUS = 4;


	/**
	 * The meta object id for the '<em>Identifier</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.pdtextensions.semanticanalysis.validation.Identifier
	 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getIdentifier()
	 * @generated
	 */
	int IDENTIFIER = 5;


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
	 * Returns the meta object for the reference list '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getTypes <em>Types</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Types</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getTypes()
	 * @see #getValidator()
	 * @generated
	 */
	EReference getValidator_Types();

	/**
	 * Returns the meta object for the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getType(java.lang.String) <em>Get Type</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Type</em>' operation.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Validator#getType(java.lang.String)
	 * @generated
	 */
	EOperation getValidator__GetType__String();

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
	 * Returns the meta object for the reference list '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getValidators <em>Validators</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Validators</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Category#getValidators()
	 * @see #getCategory()
	 * @generated
	 */
	EReference getCategory_Validators();

	/**
	 * Returns the meta object for class '{@link org.pdtextensions.semanticanalysis.model.validators.Type <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Type</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type
	 * @generated
	 */
	EClass getType();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getLabel()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Label();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getDescription()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDefaultSeverity <em>Default Severity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Severity</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getDefaultSeverity()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_DefaultSeverity();

	/**
	 * Returns the meta object for the reference '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getValidator <em>Validator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Validator</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getValidator()
	 * @see #getType()
	 * @generated
	 */
	EReference getType_Validator();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getId()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getNum <em>Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Num</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getNum()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Num();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#isImport <em>Import</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Import</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#isImport()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Import();

	/**
	 * Returns the meta object for the attribute '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.pdtextensions.semanticanalysis.model.validators.Type#getName()
	 * @see #getType()
	 * @generated
	 */
	EAttribute getType_Name();

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
	 * Returns the meta object for data type '{@link org.pdtextensions.semanticanalysis.validation.Identifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Identifier</em>'.
	 * @see org.pdtextensions.semanticanalysis.validation.Identifier
	 * @model instanceClass="org.pdtextensions.semanticanalysis.validation.Identifier"
	 * @generated
	 */
	EDataType getIdentifier();

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
		 * The meta object literal for the '<em><b>Validator Factory</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR__VALIDATOR_FACTORY = eINSTANCE.getValidator_ValidatorFactory();

		/**
		 * The meta object literal for the '<em><b>Category</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR__CATEGORY = eINSTANCE.getValidator_Category();

		/**
		 * The meta object literal for the '<em><b>Types</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR__TYPES = eINSTANCE.getValidator_Types();

		/**
		 * The meta object literal for the '<em><b>Get Type</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation VALIDATOR___GET_TYPE__STRING = eINSTANCE.getValidator__GetType__String();

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
		 * The meta object literal for the '<em><b>Validators</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATEGORY__VALIDATORS = eINSTANCE.getCategory_Validators();

		/**
		 * The meta object literal for the '{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl <em>Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getType()
		 * @generated
		 */
		EClass TYPE = eINSTANCE.getType();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__LABEL = eINSTANCE.getType_Label();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__DESCRIPTION = eINSTANCE.getType_Description();

		/**
		 * The meta object literal for the '<em><b>Default Severity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__DEFAULT_SEVERITY = eINSTANCE.getType_DefaultSeverity();

		/**
		 * The meta object literal for the '<em><b>Validator</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TYPE__VALIDATOR = eINSTANCE.getType_Validator();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__ID = eINSTANCE.getType_Id();

		/**
		 * The meta object literal for the '<em><b>Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__NUM = eINSTANCE.getType_Num();

		/**
		 * The meta object literal for the '<em><b>Import</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__IMPORT = eINSTANCE.getType_Import();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TYPE__NAME = eINSTANCE.getType_Name();

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

		/**
		 * The meta object literal for the '<em>Identifier</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.pdtextensions.semanticanalysis.validation.Identifier
		 * @see org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorsPackageImpl#getIdentifier()
		 * @generated
		 */
		EDataType IDENTIFIER = eINSTANCE.getIdentifier();

	}

} //ValidatorsPackage
