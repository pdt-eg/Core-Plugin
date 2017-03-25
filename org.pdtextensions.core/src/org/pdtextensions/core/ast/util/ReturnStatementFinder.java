package org.pdtextensions.core.ast.util;

import org.eclipse.php.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;

@SuppressWarnings("restriction")
public class ReturnStatementFinder extends AbstractRangeFinder {

	protected boolean hasFound = false;
	
	public boolean hasFoundReturnStatement()
	{
		return hasFound;
	}
	
	public boolean visit(ReturnStatement returnStatement)
	{
		if(isCovered(returnStatement)) {
			hasFound = true;
			return false;
		}
		return true;
	}
	
}
