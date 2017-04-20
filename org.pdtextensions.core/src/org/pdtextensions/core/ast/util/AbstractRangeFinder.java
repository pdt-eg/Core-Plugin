package org.pdtextensions.core.ast.util;

import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;

public abstract class AbstractRangeFinder extends AbstractVisitor {

	protected ISourceRange fSourceRange;
	
	/**
	 * Checks whether the node is covered by the range
	 * 
	 * @param node
	 * @return
	 */
	protected boolean isCovered(ASTNode node)
	{
		if(fSourceRange == null)
		{
			// we're checking whether the range covers the node.
			// this must be false, but in this case we want to
			// retrieve alle nodes...
			return true;
		} else {
			return SourceRangeUtil.covers(fSourceRange, node);	
		}
	}
	
	/**
	 * Checks whether the node covers the range
	 * 
	 * @param node
	 * @return
	 */
	protected boolean covers(ASTNode node)
	{
		if(fSourceRange == null)
		{
			// we're checking whether the node covers a "null range"
			// this is obviously true
			return true;
		} else {
			return SourceRangeUtil.isCovered(fSourceRange, node);	
		}
	}
	
	public void setRange(ISourceRange range)
	{
		fSourceRange = range;
	}
	
}
