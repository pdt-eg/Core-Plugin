package org.pdtextensions.core.validation;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.pdtextensions.core.PEXCorePlugin;

/**
 * Not used yet. Need to find out how to check against an {@link IProblemIdentifier}
 * in org.eclipse.php.internal.ui.text.correction.IQuickFixProcessor.hasCorrections(ISourceModule, int)
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public enum PEXProblem implements IProblemIdentifier {
	
	INTERFACE_IMPLEMENTATION,
	MISSING_USE_STATEMENT;

	@Override
	public String contributor() {
		return PEXCorePlugin.PLUGIN_ID;
	}

}
