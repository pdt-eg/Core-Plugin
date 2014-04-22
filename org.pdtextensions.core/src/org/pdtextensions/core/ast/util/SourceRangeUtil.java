package org.pdtextensions.core.ast.util;

import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;

@SuppressWarnings("restriction")
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
		
		return range.getOffset() <= node.getStart() && (range.getLength() + range.getOffset()) >= node.getLength() + node.getStart();
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
		
		return range.getOffset() >= node.getStart() && (range.getLength() + range.getOffset()) <= node.getLength() + node.getStart();
	}
}