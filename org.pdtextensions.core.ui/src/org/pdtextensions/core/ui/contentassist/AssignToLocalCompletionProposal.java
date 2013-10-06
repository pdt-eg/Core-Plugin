/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.contentassist;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.rewrite.ASTRewrite;
import org.eclipse.php.internal.ui.corext.fix.LinkedProposalModel;
import org.eclipse.php.internal.ui.text.correction.proposals.ASTRewriteCorrectionProposal;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.text.edits.TextEditGroup;
import org.pdtextensions.core.ui.actions.CorrectionCommandHandler;

/**
 * Create local variable for function/method call
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class AssignToLocalCompletionProposal extends ASTRewriteCorrectionProposal {
	public static final String ASSIGN_TO_LOCAL_ID = "org.pdtextensions.core.ui.correction.assignToLocal.assist"; //$NON-NLS-1$
	public static final String KEY_NAME = "name"; //$NON-NLS-1$
	public static final String KEY_EXPR = "value"; //$NON-NLS-1$
	public static final String DEFAULT_NAME = "localVar"; //$NON-NLS-1$
	
	protected ASTNode context;
	
	public AssignToLocalCompletionProposal(ISourceModule cu, ASTNode context) {
		super("Assign statement to new local variable", cu, null, 0, PHPPluginImages.DESC_FIELD_DEFAULT.createImage());
		this.context = context;
		setCommandId(ASSIGN_TO_LOCAL_ID);
	}
	
	public static ExpressionStatement getStatement(ASTNode node) {
		if (node == null) { 
			return null; 
		} else if (node instanceof ExpressionStatement) {
			return (ExpressionStatement) node;
		}
		
		return node.getParent() == null ? null : getStatement(node.getParent());
	}
	
	public static Expression getMainExpression(ASTNode node) {
		ExpressionStatement statement = getStatement(node);
		
		return statement == null || statement.getExpression() == null ? null : statement.getExpression();
	}
	
	public static boolean isEnd(ASTNode coveringNode) {
		return coveringNode instanceof ExpressionStatement;
	}
	
	public static boolean isFunctionCall(ASTNode coveringNode) {
		if (coveringNode instanceof FunctionInvocation) {
			return true;
		} 

		return isEnd(coveringNode) || coveringNode.getParent() == null ? false : isFunctionCall(coveringNode.getParent());
	}

	public static boolean isAssigned(ASTNode coveringNode) {
		if (coveringNode instanceof Assignment) {
			return true;
		}

		return isEnd(coveringNode) || coveringNode.getParent() == null ? false : isAssigned(coveringNode.getParent());
	}
	
	
	@Override
	protected ASTRewrite getRewrite() throws CoreException {
		LinkedProposalModel linkedModel = getLinkedProposalModel();
		TextEditGroup editGroup = new TextEditGroup(ASSIGN_TO_LOCAL_ID);
	
		ExpressionStatement statement = getStatement(context);
		Expression expression = statement.getExpression();
		
		AST ast = statement.getAST();
		ASTRewrite astRewrite = ASTRewrite.create(ast);
		String[] names = possibleNames(expression);
		for (int i = 0; i < names.length; i++) {
			linkedModel.getPositionGroup(KEY_NAME, true).addProposal(names[0], null, 10);
		}
		Variable variable = ast.newVariable(names[0]);
		
		Assignment assign = ast.newAssignment(variable, Assignment.OP_EQUAL, (Expression) astRewrite.createCopyTarget(expression));
		astRewrite.replace(expression, assign, editGroup);

		linkedModel.getPositionGroup(KEY_NAME, true).addPosition(astRewrite.track(variable.getName()), true);
		linkedModel.getPositionGroup(KEY_EXPR, true).addPosition(astRewrite.track(assign.getRightHandSide()), false);
		linkedModel.setEndPosition(astRewrite.track(statement));
		
		return astRewrite;
	}
	
	protected String[] possibleNames(ASTNode node) {
		String basic = getBasicName(node);
		if (basic == null || basic.length() < 1) {
			basic = DEFAULT_NAME;
		}
		
		return new String[] {Character.toLowerCase(basic.charAt(0)) + basic.substring(1)};
	}

	private String getBasicName(ASTNode node) {
		if (node != null) {
			switch (node.getType()) {
			case ASTNode.METHOD_INVOCATION:
				MethodInvocation inv = (MethodInvocation) node;
				return getBasicName(inv.getMethod());
			case ASTNode.FUNCTION_INVOCATION:
				FunctionInvocation func = (FunctionInvocation) node;
				if (func.getFunctionName() == null || func.getFunctionName().getName() == null) {
					return DEFAULT_NAME;
				}
				
				return getBasicName(func.getFunctionName().getName());
			case ASTNode.STATIC_METHOD_INVOCATION:
				StaticMethodInvocation st = (StaticMethodInvocation) node;
				return getBasicName(st.getMethod());
			case ASTNode.CLASS_INSTANCE_CREATION:
				ClassInstanceCreation ci = (ClassInstanceCreation) node;
				if (ci.getClassName() != null) {
					return getBasicName(ci.getClassName().getName());
				}
			case ASTNode.VARIABLE:
				return getBasicName(((Variable)node).getName());
			case ASTNode.IDENTIFIER:
			case ASTNode.NAMESPACE_NAME:
				return ((Identifier)node).getName();
			}
		}
		
		return DEFAULT_NAME;
	}
	
	@Override
	public StyledString getStyledDisplayString() {
		return CorrectionCommandHandler.appendStyledDisplay(super.getStyledDisplayString(), this); 
	}
}
