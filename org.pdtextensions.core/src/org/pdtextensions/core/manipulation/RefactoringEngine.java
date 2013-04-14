/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.manipulation;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.manipulation.IRefactoringEngine;
import org.eclipse.dltk.internal.corext.refactoring.rename.ScriptRenameProcessor;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameMethodProcessor;
import org.pdtextensions.internal.corext.refactoring.rename.RenameTypeProcessor;

/**
 * @since 0.17.0
 */
public class RefactoringEngine implements IRefactoringEngine {
	public boolean isRenameAvailable(IModelElement element) throws ModelException {
		return Checks.isAvailable(element);
	}

	public ScriptRenameProcessor createRenameProcessor(IModelElement element) {
		switch (element.getElementType()) {
		case IModelElement.FIELD:
			return new RenameFieldProcessor((IField) element);
		case IModelElement.METHOD:
			return new RenameMethodProcessor((IMethod) element);
		case IModelElement.TYPE:
			return new RenameTypeProcessor((IType) element);
		default:
			return null;
		}
	}
}
