/**
 */
package org.pdtextensions.semanticanalysis.model.validators.impl;

import java.util.Collection;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.pdtextensions.semanticanalysis.IValidatorFactory;
import org.pdtextensions.semanticanalysis.model.validators.Category;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getDefaultSeverity <em>Default Severity</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getValidatorFactory <em>Validator Factory</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getValidators <em>Validators</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.ValidatorImpl#getCategory <em>Category</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValidatorImpl extends MinimalEObjectImpl.Container implements Validator {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected static final String LABEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLabel()
	 * @generated
	 * @ordered
	 */
	protected String label = LABEL_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultSeverity() <em>Default Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultSeverity()
	 * @generated
	 * @ordered
	 */
	protected static final ProblemSeverity DEFAULT_SEVERITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultSeverity() <em>Default Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultSeverity()
	 * @generated
	 * @ordered
	 */
	protected ProblemSeverity defaultSeverity = DEFAULT_SEVERITY_EDEFAULT;

	/**
	 * The default value of the '{@link #getValidatorFactory() <em>Validator Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorFactory()
	 * @generated
	 * @ordered
	 */
	protected static final IValidatorFactory VALIDATOR_FACTORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValidatorFactory() <em>Validator Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorFactory()
	 * @generated
	 * @ordered
	 */
	protected IValidatorFactory validatorFactory = VALIDATOR_FACTORY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getCategories() <em>Categories</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCategories()
	 * @generated
	 * @ordered
	 */
	protected Category categories;

	/**
	 * The cached value of the '{@link #getValidators() <em>Validators</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidators()
	 * @generated
	 * @ordered
	 */
	protected EList<Category> validators;

	/**
	 * The cached value of the '{@link #getCategory() <em>Category</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCategory()
	 * @generated
	 * @ordered
	 */
	protected Category category;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidatorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidatorsPackage.Literals.VALIDATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		String oldLabel = label;
		label = newLabel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__LABEL, oldLabel, label));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProblemSeverity getDefaultSeverity() {
		return defaultSeverity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefaultSeverity(ProblemSeverity newDefaultSeverity) {
		ProblemSeverity oldDefaultSeverity = defaultSeverity;
		defaultSeverity = newDefaultSeverity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__DEFAULT_SEVERITY, oldDefaultSeverity, defaultSeverity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IValidatorFactory getValidatorFactory() {
		return validatorFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValidatorFactory(IValidatorFactory newValidatorFactory) {
		IValidatorFactory oldValidatorFactory = validatorFactory;
		validatorFactory = newValidatorFactory;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__VALIDATOR_FACTORY, oldValidatorFactory, validatorFactory));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Category getCategories() {
		if (categories != null && categories.eIsProxy()) {
			InternalEObject oldCategories = (InternalEObject)categories;
			categories = (Category)eResolveProxy(oldCategories);
			if (categories != oldCategories) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ValidatorsPackage.VALIDATOR__CATEGORIES, oldCategories, categories));
			}
		}
		return categories;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Category basicGetCategories() {
		return categories;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCategories(Category newCategories) {
		Category oldCategories = categories;
		categories = newCategories;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__CATEGORIES, oldCategories, categories));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Category> getValidators() {
		if (validators == null) {
			validators = new EObjectResolvingEList<Category>(Category.class, this, ValidatorsPackage.VALIDATOR__VALIDATORS);
		}
		return validators;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Category getCategory() {
		if (category != null && category.eIsProxy()) {
			InternalEObject oldCategory = (InternalEObject)category;
			category = (Category)eResolveProxy(oldCategory);
			if (category != oldCategory) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ValidatorsPackage.VALIDATOR__CATEGORY, oldCategory, category));
			}
		}
		return category;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Category basicGetCategory() {
		return category;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCategory(Category newCategory) {
		Category oldCategory = category;
		category = newCategory;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR__CATEGORY, oldCategory, category));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR__ID:
				return getId();
			case ValidatorsPackage.VALIDATOR__LABEL:
				return getLabel();
			case ValidatorsPackage.VALIDATOR__DESCRIPTION:
				return getDescription();
			case ValidatorsPackage.VALIDATOR__DEFAULT_SEVERITY:
				return getDefaultSeverity();
			case ValidatorsPackage.VALIDATOR__VALIDATOR_FACTORY:
				return getValidatorFactory();
			case ValidatorsPackage.VALIDATOR__CATEGORIES:
				if (resolve) return getCategories();
				return basicGetCategories();
			case ValidatorsPackage.VALIDATOR__VALIDATORS:
				return getValidators();
			case ValidatorsPackage.VALIDATOR__CATEGORY:
				if (resolve) return getCategory();
				return basicGetCategory();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR__ID:
				setId((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__LABEL:
				setLabel((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__DEFAULT_SEVERITY:
				setDefaultSeverity((ProblemSeverity)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__VALIDATOR_FACTORY:
				setValidatorFactory((IValidatorFactory)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__CATEGORIES:
				setCategories((Category)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__VALIDATORS:
				getValidators().clear();
				getValidators().addAll((Collection<? extends Category>)newValue);
				return;
			case ValidatorsPackage.VALIDATOR__CATEGORY:
				setCategory((Category)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR__ID:
				setId(ID_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR__LABEL:
				setLabel(LABEL_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR__DEFAULT_SEVERITY:
				setDefaultSeverity(DEFAULT_SEVERITY_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR__VALIDATOR_FACTORY:
				setValidatorFactory(VALIDATOR_FACTORY_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR__CATEGORIES:
				setCategories((Category)null);
				return;
			case ValidatorsPackage.VALIDATOR__VALIDATORS:
				getValidators().clear();
				return;
			case ValidatorsPackage.VALIDATOR__CATEGORY:
				setCategory((Category)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ValidatorsPackage.VALIDATOR__LABEL:
				return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
			case ValidatorsPackage.VALIDATOR__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case ValidatorsPackage.VALIDATOR__DEFAULT_SEVERITY:
				return DEFAULT_SEVERITY_EDEFAULT == null ? defaultSeverity != null : !DEFAULT_SEVERITY_EDEFAULT.equals(defaultSeverity);
			case ValidatorsPackage.VALIDATOR__VALIDATOR_FACTORY:
				return VALIDATOR_FACTORY_EDEFAULT == null ? validatorFactory != null : !VALIDATOR_FACTORY_EDEFAULT.equals(validatorFactory);
			case ValidatorsPackage.VALIDATOR__CATEGORIES:
				return categories != null;
			case ValidatorsPackage.VALIDATOR__VALIDATORS:
				return validators != null && !validators.isEmpty();
			case ValidatorsPackage.VALIDATOR__CATEGORY:
				return category != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(", label: ");
		result.append(label);
		result.append(", description: ");
		result.append(description);
		result.append(", defaultSeverity: ");
		result.append(defaultSeverity);
		result.append(", validatorFactory: ");
		result.append(validatorFactory);
		result.append(')');
		return result.toString();
	}

} //ValidatorImpl
