/*******************************************************************************
 * Copyright (c) 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.codegenerator;

import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Statement;

@SuppressWarnings("restriction")
public class ClassGenerator extends ElementGenerator {

	public ClassGenerator() throws Exception {
		super();
	}

	@Override
	protected Statement generateElementNode() {
		ClassDeclaration classModel = ast.newClassDeclaration();
		classModel.setName(ast.newIdentifier(name));
		classModel.setBody(ast.newBlock());

		if (getSuperclass() != null) {
			classModel.setSuperClass(ast.newIdentifier(getSuperclass()));
		}

		for (String item : interfaces) {
			classModel.interfaces().add(ast.newIdentifier(item));
		}

		return classModel;

	}
}
