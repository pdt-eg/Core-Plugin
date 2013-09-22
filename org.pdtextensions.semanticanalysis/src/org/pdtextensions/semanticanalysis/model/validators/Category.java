/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Category</b></em>'.
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
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Category#getId <em>Id</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Category#getLabel <em>Label</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Category#getDescription <em>Description</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Category#getValidators <em>Validators</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getCategory()
 * @model
 * @generated
 */
public interface Category extends EObject {
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getCategory_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getId <em>Id</em>}' attribute.
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getCategory_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getLabel <em>Label</em>}' attribute.
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getCategory_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Category#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Validators</b></em>' reference list.
	 * The list contents are of type {@link org.pdtextensions.semanticanalysis.model.validators.Validator}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validators</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validators</em>' reference list.
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getCategory_Validators()
	 * @model
	 * @generated
	 */
	EList<Validator> getValidators();

} // Category
