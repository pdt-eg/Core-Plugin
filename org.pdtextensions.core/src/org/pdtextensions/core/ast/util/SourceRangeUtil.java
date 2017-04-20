package org.pdtextensions.core.ast.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.php.core.ast.nodes.ASTNode;

public class SourceRangeUtil {

	/**
	 * Returns true if the range covers the node
	 * 
	 * @param range
	 * @param node
	 * @return
	 */
	public static boolean covers(ISourceRange range, ASTNode node)
	{
		if(range == null || node == null) {
			return false;
		}
		
		return range.getOffset() <= node.getStart() && (range.getLength() + range.getOffset()) >= node.getEnd();
	}
	
	/**
	 * Returns true if range covers toBeCovered
	 * 
	 * @param range
	 * @param toBeCovered
	 * @return
	 */
	public static boolean covers(ISourceRange range, ISourceRange toBeCovered)
	{
		if(range == null || toBeCovered == null) {
			return false;
		}
		
		return range.getOffset() <= toBeCovered.getOffset() && (range.getLength() + range.getOffset()) >= toBeCovered.getLength() + toBeCovered.getOffset();
	
	}
	
	/**
	 * Returns true if the range is covered by the node
	 * 
	 * @param range
	 * @param node
	 * @return
	 */
	public static boolean isCovered(ISourceRange range, ASTNode node)
	{
		if(range == null || node == null) {
			return false;
		}
		
		return range.getOffset() >= node.getStart() && (range.getLength() + range.getOffset()) <=  node.getEnd();
	}
	
	public static ISourceRange createFrom(ASTNode node)
	{
		Assert.isNotNull(node);
		
		return new SourceRange(node.getStart(), node.getLength());
	}
}