/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.pdtextensions.semanticanalysis.IValidatorFactory;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Validator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * ******************************************************************************
 *  * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * which accompanies this distribution, and is available at
 *  * http://www.eclipse.org/legal/epl-v10.html
 *  *****************************************************************************
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getId <em>Id</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getLabel <em>Label</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDescription <em>Description</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDefaultSeverity <em>Default Severity</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getValidatorFactory <em>Validator Factory</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategories <em>Categories</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getValidators <em>Validators</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategory <em>Category</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator()
 * @model
 * @generated
 */
public interface Validator extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label</em>' attribute.
	 * @see #setLabel(String)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getLabel <em>Label</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Label</em>' attribute.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Default Severity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Severity</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Default Severity</em>' attribute.
	 * @see #setDefaultSeverity(ProblemSeverity)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_DefaultSeverity()
	 * @model dataType="org.pdtextensions.semanticanalysis.model.validators.Status"
	 * @generated
	 */
	ProblemSeverity getDefaultSeverity();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getDefaultSeverity <em>Default Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Severity</em>' attribute.
	 * @see #getDefaultSeverity()
	 * @generated
	 */
	void setDefaultSeverity(ProblemSeverity value);

	/**
	 * Returns the value of the '<em><b>Validator Factory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validator Factory</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validator Factory</em>' attribute.
	 * @see #setValidatorFactory(IValidatorFactory)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_ValidatorFactory()
	 * @model dataType="org.pdtextensions.semanticanalysis.model.validators.ValidatorFactory"
	 * @generated
	 */
	IValidatorFactory getValidatorFactory();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getValidatorFactory <em>Validator Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validator Factory</em>' attribute.
	 * @see #getValidatorFactory()
	 * @generated
	 */
	void setValidatorFactory(IValidatorFactory value);

	/**
	 * Returns the value of the '<em><b>Categories</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Categories</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Categories</em>' reference.
	 * @see #setCategories(Category)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Categories()
	 * @model
	 * @generated
	 */
	Category getCategories();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategories <em>Categories</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Categories</em>' reference.
	 * @see #getCategories()
	 * @generated
	 */
	void setCategories(Category value);

	/**
	 * Returns the value of the '<em><b>Validators</b></em>' reference list.
	 * The list contents are of type {@link org.pdtextensions.semanticanalysis.model.validators.Category}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validators</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validators</em>' reference list.
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Validators()
	 * @model
	 * @generated
	 */
	EList<Category> getValidators();

	/**
	 * Returns the value of the '<em><b>Category</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Category</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Category</em>' reference.
	 * @see #setCategory(Category)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Category()
	 * @model required="true"
	 * @generated
	 */
	Category getCategory();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategory <em>Category</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Category</em>' reference.
	 * @see #getCategory()
	 * @generated
	 */
	void setCategory(Category value);

} // Validator
