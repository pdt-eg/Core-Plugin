package org.pdtextensions.core.ast.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.php.core.ast.match.ASTMatcher;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.Block;
import org.eclipse.php.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;

@SuppressWarnings("restriction")
public class BlockContainsFinder extends GenericVisitor {

	private Block fStartNode;
	private ASTNode[] fToSearch;
	
	private int fIndex;
	private Match fMatch;
	private ASTMatcher fMatcher;
	private List<Match> fResult;
	
	public static class Match {
		private List<ASTNode> fNodes;

		public Match() {
			fNodes= new ArrayList<ASTNode>();
		}
		
		public void add(ASTNode node) {
			fNodes.add(node);
		}
		
		public ASTNode[] getNodes() {
			return fNodes.toArray(new ASTNode[fNodes.size()]);
		}

		public boolean isEmpty() {
			return fNodes.isEmpty();
		}
	}
	
	public BlockContainsFinder(Block coveringNode, ASTNode[] toSearchIsomorphic)
	{
		Assert.isNotNull(coveringNode);
		Assert.isLegal(toSearchIsomorphic.length > 0);
		
		fStartNode = coveringNode;
		fToSearch = toSearchIsomorphic;
	}
	
	public void perform()
	{
		fResult = new ArrayList<Match>();
		fMatcher = new ASTMatcher();
		reset();
		fStartNode.accept(this);
	}
	
	protected boolean visitNode(ASTNode node)
	{
		// check whether the node matches
		if(matches(node)) {
			return false;
			
		// if the node did not match and it wasn't the first
	    // to search node, then reset and try to match again.
		} else if(!isResetted()) {
			
			reset();
			if(matches(node)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isResetted() {
		return fIndex == 0 && fMatch.isEmpty();
	}
	
	private boolean matches(ASTNode node) {
		if (fToSearch[fIndex].subtreeMatch(fMatcher, node) ) {
			fMatch.add(node);
			fIndex++;
			// all search nodes found, add it to result and 
			// reset, s.t we can search for the next nodes...
			if (fIndex == fToSearch.length) {
				fResult.add(fMatch);
				reset();
			}
			return true;
		}
		return false;
	}
	
	private void reset()
	{
		fIndex = 0;
		fMatch = new Match();
	}
	
	public List<Match> getMatches()
	{
		return fResult;
	}
}
