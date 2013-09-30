/**
 */
package org.pdtextensions.semanticanalysis.model.validators.impl;

import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.pdtextensions.semanticanalysis.model.validators.Type;
import org.pdtextensions.semanticanalysis.model.validators.Validator;
import org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl#getDefaultSeverity <em>Default Severity</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl#getValidator <em>Validator</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.impl.TypeImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TypeImpl extends MinimalEObjectImpl.Container implements Type {
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
	 * The cached value of the '{@link #getValidator() <em>Validator</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidator()
	 * @generated
	 * @ordered
	 */
	protected Validator validator;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidatorsPackage.Literals.TYPE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.TYPE__LABEL, oldLabel, label));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.TYPE__DESCRIPTION, oldDescription, description));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.TYPE__DEFAULT_SEVERITY, oldDefaultSeverity, defaultSeverity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Validator getValidator() {
		if (validator != null && validator.eIsProxy()) {
			InternalEObject oldValidator = (InternalEObject)validator;
			validator = (Validator)eResolveProxy(oldValidator);
			if (validator != oldValidator) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ValidatorsPackage.TYPE__VALIDATOR, oldValidator, validator));
			}
		}
		return validator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Validator basicGetValidator() {
		return validator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValidator(Validator newValidator) {
		Validator oldValidator = validator;
		validator = newValidator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.TYPE__VALIDATOR, oldValidator, validator));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.TYPE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidatorsPackage.TYPE__LABEL:
				return getLabel();
			case ValidatorsPackage.TYPE__DESCRIPTION:
				return getDescription();
			case ValidatorsPackage.TYPE__DEFAULT_SEVERITY:
				return getDefaultSeverity();
			case ValidatorsPackage.TYPE__VALIDATOR:
				if (resolve) return getValidator();
				return basicGetValidator();
			case ValidatorsPackage.TYPE__ID:
				return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ValidatorsPackage.TYPE__LABEL:
				setLabel((String)newValue);
				return;
			case ValidatorsPackage.TYPE__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case ValidatorsPackage.TYPE__DEFAULT_SEVERITY:
				setDefaultSeverity((ProblemSeverity)newValue);
				return;
			case ValidatorsPackage.TYPE__VALIDATOR:
				setValidator((Validator)newValue);
				return;
			case ValidatorsPackage.TYPE__ID:
				setId((String)newValue);
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
			case ValidatorsPackage.TYPE__LABEL:
				setLabel(LABEL_EDEFAULT);
				return;
			case ValidatorsPackage.TYPE__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case ValidatorsPackage.TYPE__DEFAULT_SEVERITY:
				setDefaultSeverity(DEFAULT_SEVERITY_EDEFAULT);
				return;
			case ValidatorsPackage.TYPE__VALIDATOR:
				setValidator((Validator)null);
				return;
			case ValidatorsPackage.TYPE__ID:
				setId(ID_EDEFAULT);
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
			case ValidatorsPackage.TYPE__LABEL:
				return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
			case ValidatorsPackage.TYPE__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case ValidatorsPackage.TYPE__DEFAULT_SEVERITY:
				return DEFAULT_SEVERITY_EDEFAULT == null ? defaultSeverity != null : !DEFAULT_SEVERITY_EDEFAULT.equals(defaultSeverity);
			case ValidatorsPackage.TYPE__VALIDATOR:
				return validator != null;
			case ValidatorsPackage.TYPE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (label: ");
		result.append(label);
		result.append(", description: ");
		result.append(description);
		result.append(", defaultSeverity: ");
		result.append(defaultSeverity);
		result.append(", id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //TypeImpl
