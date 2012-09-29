package org.pdtextensions.core.visitor;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.pdtextensions.core.PEXCorePlugin;

public enum PEXProblem implements IProblemIdentifier {
	
	INTERFACE_IMPLEMENTATION;

	@Override
	public String contributor() {
		return PEXCorePlugin.PLUGIN_ID;
	}

}
