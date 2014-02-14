/*******************************************************************************
 * Copyright (c) 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.codegenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.NamespaceName;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.ast.nodes.UseStatementPart;
import org.eclipse.php.internal.core.ast.rewrite.ASTRewriteFlattener;
import org.eclipse.php.internal.core.ast.rewrite.RewriteEventStore;

@SuppressWarnings("restriction")
abstract public class ElementGenerator implements IElementGenerator {

	protected String name;
	private String namespace;
	protected ArrayList<String> interfaces;
	private String superclass;

	private Program program;
	protected AST ast;
	private NamespaceDeclaration namespaceNode;

	public ElementGenerator() throws Exception {
		program = ASTParser.newParser(PHPVersion.getLatestVersion()).createAST(null);
		ast = program.getAST();
		interfaces = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return generateCode();
	}

	public String getNamespace() {
		return namespace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pdtextensions.core.codegenerator.IElementGenerat#setName(java.lang
	 * .String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	protected String getSuperclass() {
		return superclass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pdtextensions.core.codegenerator.IElementGenerat#setSuperclass(java
	 * .lang.String)
	 */
	@Override
	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pdtextensions.core.codegenerator.IElementGenerat#setNamespace(java
	 * .lang.String)
	 */
	@Override
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pdtextensions.core.codegenerator.IElementGenerat#generateCode()
	 */
	@Override
	public String generateCode() {
		ASTRewriteFlattener astRewriteFlattener = new ASTRewriteFlattener(new RewriteEventStore());

		if (namespace == null) {
			program.statements().addAll(getUseStatements());
			program.statements().add(generateElementNode());
		} else {
			program.statements().add(generateNamespaceNode(generateElementNode()));
		}

		astRewriteFlattener.visit(program);

		return astRewriteFlattener.getResult();
	};

	private ArrayList<UseStatement> getUseStatements() {
		ArrayList<UseStatement> statements = new ArrayList<UseStatement>();
		if (interfaces != null) {
			for (String interfaceName : interfaces) {
				interfaceName = interfaceName.trim();
				if (interfaceName.startsWith("\\")) {
					interfaceName = interfaceName.substring(1);
				}
				if (interfaceName.lastIndexOf("\\") != -1) {
					ArrayList<UseStatementPart> useParts = new ArrayList<UseStatementPart>();
					Collection<Identifier> segments = new Vector<Identifier>();
					segments.add(ast.newIdentifier(interfaceName));
					NamespaceName name = ast.newNamespaceName(segments, false, false);
					useParts.add(ast.newUseStatementPart(name, null));
					statements.add(ast.newUseStatement(useParts));
				}

			}
		}

		return statements;
	}

	protected abstract Statement generateElementNode();

	/*
	 * (non-Javadoc)
	 * 
	 * Qualified name is treated as fully qualified name for now.
	 * 
	 * @see
	 * org.pdtextensions.core.codegenerator.IElementGenerat#addInterface(java
	 * .lang.String)
	 */
	public void addInterface(String interfaceFullName) {
		interfaces.add(interfaceFullName);
	}

	/**
	 * Generate <code>NamespaceDeclaration</code> object with
	 * class/interface/trait declaration in it.
	 * 
	 * @param elementDeclaration
	 * @return
	 */
	protected Statement generateNamespaceNode(Statement elementDeclaration) {

		List<Identifier> namespaceIdentifier = new ArrayList<Identifier>();
		namespaceIdentifier.add(ast.newIdentifier(namespace));

		ArrayList<Statement> statement = new ArrayList<Statement>();

		statement.addAll(getUseStatements());
		statement.add(elementDeclaration);

		// Comment comment = ast.newComment(Comment.TYPE_PHPDOC);

		Block block = ast.newBlock(statement);
		block.clearBodyStartSymbol();

		namespaceNode = ast.newNamespaceDeclaration(ast.newNamespaceName(namespaceIdentifier, false, false), block);
		namespaceNode.setBracketed(false);

		return namespaceNode;
	}

	public void addTrait(String traitFullQualifiName) {
		// TODO Auto-generated method stub

	}
}
