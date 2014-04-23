/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.refactoring;

import org.eclipse.osgi.util.NLS;

/**
 * @since 0.17.0
 */
public class RefactoringMessages extends NLS {
	private static final String BUNDLE_NAME = "org.pdtextensions.core.ui.refactoring.refactoringui"; //$NON-NLS-1$

	public static String RenameAction_text;
	public static String RenamePHPElementAction_exception;
	public static String RenamePHPElementAction_name;
	public static String RenamePHPElementAction_not_available;
	
	public static String ExtractMethod_name;
	public static String ExtractMethod_must_select_code;
	public static String ExtractMethod_must_select_code_in_method;
	public static String ExtractMethodPage;
	public static String ExtractMethodPage_LabelMethod;
	
	public static String ExtractMethodInputPage_access_Modifiers;
	public static String ExtractMethodInputPage_access_public;
	public static String ExtractMethodInputPage_access_protected;
	public static String ExtractMethodInputPage_access_private;
	public static String ExtractMethodInputPage_signature_preview;
	public static String ExtractMethodInputPage_description;
	public static String ExtractMethodInputPage_generatePhpdocComment;
	public static String ExtractMethodInputPage_duplicates_none;
	public static String ExtractMethodInputPage_duplicates_single;
	public static String ExtractMethodInputPage_duplicates_multi;
	public static String ExtractMethodInputPage_returnMultipleVariables;
	
	public static String ExtractMethodInputPage_errorCouldNotParseSourceCode;
	public static String ExtractMethodInputPage_errorCouldNotParseSelectedCode;
	public static String ExtractMethodInputPage_errorMethodCannotReturnMultipleVariables;
	public static String ExtractMethodInputPage_errorCouldNotRetrieveCoveringMethodDeclaration;
	public static String ExtractMethodInputPage_errorNoValidMethodName;
	public static String ExtractMethodInputPage_errorNoValidParameterName;
	public static String ExtractMethodInputPage_errorContainsReturnStatement;
	public static String ExtractMethodInputPage_errorMultipleReturnValuesNotAllowed;
	
	public static String ExtractMethodPreviewPage_TextChangeName;
	public static String ExtractMethodPreviewPage_TextChangeNewMethod;
	public static String ExtractMethodPreviewPage_TextChangeSubstituteStatements;
	public static String ExtractMethodPreviewPage_TextChangeSubsituteDuplicateStatements;
	public static String ExtractMethodPreviewPage_NoSignaturePreviewAvailable;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, RefactoringMessages.class);
	}

	private RefactoringMessages() {
	}
}
