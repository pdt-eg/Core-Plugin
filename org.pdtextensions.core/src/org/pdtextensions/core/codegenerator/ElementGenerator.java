/*******************************************************************************
 * Copyright (c) 2014 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.codegenerator;

import java.util.ArrayList;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.rewrite.ASTRewriteFlattener;
import org.eclipse.php.internal.core.ast.rewrite.RewriteEventStore;

@SuppressWarnings("restriction")
abstract public class ElementGenerator {

	protected String name;
	protected String namespace;

	protected Program program;
	protected AST ast;
	protected ArrayList<String> interfaces;

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

	public void setName(String name) {
		this.name = name;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String generateCode() {
		ASTRewriteFlattener astRewriteFlattener = new ASTRewriteFlattener(new RewriteEventStore());
		program.statements().add(generateASTModel());
		astRewriteFlattener.visit(program);

		return astRewriteFlattener.getResult();
	};

	protected abstract Statement generateASTModel();

	public void addInterface(String interfaceFullName) {
		interfaces.add(interfaceFullName);
	}
}
