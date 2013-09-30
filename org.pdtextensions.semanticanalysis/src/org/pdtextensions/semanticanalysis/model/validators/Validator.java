/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.pdtextensions.semanticanalysis.validation.IValidatorFactory;

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
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getCategory <em>Category</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Validator#getTypes <em>Types</em>}</li>
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

	/**
	 * Returns the value of the '<em><b>Types</b></em>' reference list.
	 * The list contents are of type {@link org.pdtextensions.semanticanalysis.model.validators.Type}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Types</em>' reference list.
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getValidator_Types()
	 * @model
	 * @generated
	 */
	EList<Type> getTypes();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Type getType(String id);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.pdtextensions.semanticanalysis.model.validators.ValidatorFactory"
	 * @generated
	 */
	IValidatorFactory getValidatorFactory();

} // Validator
