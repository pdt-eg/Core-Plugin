package org.pdtextensions.core.ast.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.php.core.ast.nodes.Assignment;

public class RangeAssignmentFinder extends AbstractRangeFinder {

	private List<Assignment> fFoundAssignments = new ArrayList<Assignment>();
	
	public List<Assignment> getFoundAssignments() {
		return fFoundAssignments;
	}
	
	public boolean visit(Assignment assignment)
	{
		if(isCovered(assignment)) {
			fFoundAssignments.add(assignment);
		}

		return true;
	}
	
}
