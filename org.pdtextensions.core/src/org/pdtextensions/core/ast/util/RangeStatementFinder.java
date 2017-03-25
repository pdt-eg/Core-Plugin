package org.pdtextensions.core.ast.util;

import java.util.ArrayList;

import org.eclipse.php.core.ast.nodes.Block;
import org.eclipse.php.core.ast.nodes.Statement;

@SuppressWarnings("restriction")
public class RangeStatementFinder extends AbstractRangeFinder {

	ArrayList<Statement> fCoveredStatements = new ArrayList<Statement>();
	
	public boolean visit(Block block)
	{
		if(covers(block)) {
			
			for(Statement statement : block.statements()) 
			{
				if(isCovered(statement)) {
					fCoveredStatements.add(statement);
				}
			}
		}
		
		return true;
	}
	
	public ArrayList<Statement> getCoveredStatements()
	{
		return fCoveredStatements;
	}
}
