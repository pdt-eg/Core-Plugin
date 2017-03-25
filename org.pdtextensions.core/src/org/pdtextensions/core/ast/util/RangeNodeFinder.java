package org.pdtextensions.core.ast.util;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.php.core.ast.nodes.ASTNode;

@SuppressWarnings("restriction")
public class RangeNodeFinder extends GenericVisitor {

	protected ArrayList<ASTNode> fCoveredNodes = new ArrayList<ASTNode>();
	
	protected ISourceRange fSourceRange;
	
	public RangeNodeFinder(ISourceRange range)
	{
		setRange(range);
	}
	
	@Override
	protected boolean visitNode(ASTNode node) {
		
		if(SourceRangeUtil.covers(fSourceRange, node)) {
			fCoveredNodes.add(node);
			return false;
		}

		return true;
	}
		
	public void setRange(ISourceRange range)
	{
		Assert.isNotNull(range);
		fSourceRange = range;
	}
	
	public ArrayList<ASTNode> getNodes()
	{
		return fCoveredNodes;
	}

}
