/*******************************************************************************
 * Copyright (c) 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.codegenerator;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.Statement;

@SuppressWarnings("restriction")
public class InterfaceGenerator extends ElementGenerator {

	public InterfaceGenerator() throws Exception {
		super();
	}

	@Override
	protected Statement generateASTModel() {
		InterfaceDeclaration interfaceModel = ast.newInterfaceDeclaration();
		interfaceModel.setName(ast.newIdentifier(name));
		interfaceModel.setBody(ast.newBlock());

		for (String item : interfaces) {
			interfaceModel.interfaces().add(ast.newIdentifier(item));
		}

		if (namespace == null || namespace.isEmpty()) {

			return interfaceModel;
		} else {

			return generateNamespaceWithInterface(interfaceModel);
		}

	}

	private Statement generateNamespaceWithInterface(
			InterfaceDeclaration interfaceModel) {

		ArrayList<Identifier> namespaceIdentifier = new ArrayList<Identifier>();
		namespaceIdentifier.add(ast.newIdentifier(namespace));

		ArrayList<Statement> statement = new ArrayList<Statement>();
		statement.add(interfaceModel);
		
//		Comment comment = ast.newComment(Comment.TYPE_PHPDOC);
		
		Block block = ast.newBlock(statement);
		block.clearBodyStartSymbol();

		NamespaceDeclaration namespaceModel = ast.newNamespaceDeclaration(
				ast.newNamespaceName(namespaceIdentifier, false, false), block);
		namespaceModel.setBracketed(false);
		
		return namespaceModel;
	}
}
