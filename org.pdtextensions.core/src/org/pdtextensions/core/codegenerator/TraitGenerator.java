/*******************************************************************************
 * Copyright (c) 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.codegenerator;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.TraitDeclaration;

@SuppressWarnings("restriction")
public class TraitGenerator extends ElementGenerator {

	public TraitGenerator() throws Exception {
		super();
	}

	@Override
	protected Statement generateElementNode() {

		TraitDeclaration traitModel = ast.newTraitDeclaration();
		traitModel.setName(ast.newIdentifier(name));
		traitModel.setBody(ast.newBlock());

		if (getSuperclass() != null) {
			traitModel.setSuperClass(ast.newIdentifier(getSuperclass()));
		}

		for (String item : interfaces) {
			traitModel.interfaces().add(ast.newIdentifier(item));
		}

		return traitModel;

	}


}
