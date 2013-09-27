/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.semanticanalysis.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.internal.ui.text.correction.IInvocationContext;
import org.eclipse.php.internal.ui.text.correction.IProblemLocation;
import org.eclipse.php.internal.ui.text.correction.IQuickFixProcessor;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.ui.contentassist.InterfaceMethodCompletionProposal;


/**
 * 
 * Provides quick fixes for missing interface methods.
 *
 */
@SuppressWarnings("restriction")
public class InterfaceMethodQuickFixProcessor implements IQuickFixProcessor {
	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId) {
		return problemId == PEXProblemIdentifier.INTERFACE_RELATED.id();
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();
		
		for (IProblemLocation location : locations) {
			if (location.getProblemId() == PEXProblemIdentifier.INTERFACE_RELATED.id()) {
				corrections.add(new InterfaceMethodCompletionProposal("", 0, 100, null, "Add unimplemented methods", 100));
			}
		}
			
		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);
	}
}
