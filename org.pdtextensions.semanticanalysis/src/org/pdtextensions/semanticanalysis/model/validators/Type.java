/**
 */
package org.pdtextensions.semanticanalysis.model.validators;

import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type</b></em>'.
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
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Type#getLabel <em>Label</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDescription <em>Description</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDefaultSeverity <em>Default Severity</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Type#getValidator <em>Validator</em>}</li>
 *   <li>{@link org.pdtextensions.semanticanalysis.model.validators.Type#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getType()
 * @model
 * @generated
 */
public interface Type extends EObject {
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getType_Label()
	 * @model
	 * @generated
	 */
	String getLabel();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getLabel <em>Label</em>}' attribute.
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getType_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDescription <em>Description</em>}' attribute.
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
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getType_DefaultSeverity()
	 * @model dataType="org.pdtextensions.semanticanalysis.model.validators.Status"
	 * @generated
	 */
	ProblemSeverity getDefaultSeverity();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getDefaultSeverity <em>Default Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Default Severity</em>' attribute.
	 * @see #getDefaultSeverity()
	 * @generated
	 */
	void setDefaultSeverity(ProblemSeverity value);

	/**
	 * Returns the value of the '<em><b>Validator</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validator</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validator</em>' reference.
	 * @see #setValidator(Validator)
	 * @see org.pdtextensions.semanticanalysis.model.validators.ValidatorsPackage#getType_Validator()
	 * @model required="true"
	 * @generated
	 */
	Validator getValidator();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getValidator <em>Validator</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validator</em>' reference.
	 * @see #getValidator()
	 * @generated
	 */
	void setValidator(Validator value);

	/**
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @model kind="operation" dataType="org.pdtextensions.semanticanalysis.model.validators.Identifier"
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.pdtextensions.semanticanalysis.model.validators.Type#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // Type
