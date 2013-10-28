/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.quickassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.ui.text.correction.IInvocationContext;
import org.eclipse.php.internal.ui.text.correction.IProblemLocation;
import org.eclipse.php.internal.ui.text.correction.IQuickAssistProcessor;
import org.pdtextensions.core.ui.contentassist.AssignToLocalCompletionProposal;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class AssignToLocalQuickAssistProcessor implements IQuickAssistProcessor {
	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		if (context.getCoveringNode() == null) {
			return false;
		}
		final Expression mainExpression = AssignToLocalCompletionProposal.getMainExpression(context.getCoveringNode());
		if (mainExpression == null) {
			return false;
		}
		
		switch (mainExpression.getType()) {
		case ASTNode.METHOD_INVOCATION:
		case ASTNode.STATIC_METHOD_INVOCATION:
		case ASTNode.FUNCTION_INVOCATION:
		case ASTNode.CLASS_INSTANCE_CREATION:
			return true;
		}
		
		return false;
	}

	@Override
	public IScriptCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		final ASTNode coveringNode = context.getCoveringNode();
		if (hasAssists(context)) {
			return new IScriptCompletionProposal[]{
				new AssignToLocalCompletionProposal(context.getCompilationUnit(), coveringNode)
			};
		}

		return null;
	}
}
