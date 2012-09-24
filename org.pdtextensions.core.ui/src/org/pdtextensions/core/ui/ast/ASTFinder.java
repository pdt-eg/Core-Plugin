package org.pdtextensions.core.ui.ast;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.Program;

public class ASTFinder extends RunThroughVisitor {

	private int offset;
	private ASTNode astNode;

	protected ASTFinder(int offset) {
		this.offset = offset;
		this.astNode = null;
	}

	@Override
	public void postVisit(ASTNode node) {
		if (node.getStart() <= offset && offset < node.getEnd()) {
			if (astNode == null) {
				astNode = node;
			} else if (astNode.getStart() < node.getStart()) {
				astNode = node;
			}
		}
		super.postVisit(node);
	}

	public static ASTNode findNode(Program program, int offset) {
		ASTFinder finder = new ASTFinder(offset);
		program.accept(finder);
		return finder.astNode;
	}
}
