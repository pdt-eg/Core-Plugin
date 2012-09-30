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
package org.pdtextensions.core.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.internal.ui.text.correction.IInvocationContext;
import org.eclipse.php.internal.ui.text.correction.IProblemLocation;
import org.eclipse.php.internal.ui.text.correction.IQuickFixProcessor;
import org.pdtextensions.core.compiler.IPDTProblem;
import org.pdtextensions.core.ui.contentassist.InterfaceMethodCompletionProposal;


/**
 * 
 * Provides quick fixes for missing interface methods.
 *
 */
@SuppressWarnings("restriction")
public class InterfaceMethodQuickFixProcessor implements IQuickFixProcessor {

	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId)
	{
		if (problemId == IPDTProblem.InterfaceRelated) {
			return true;
		}
	
		return false;
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(
			IInvocationContext context, IProblemLocation[] locations)
			throws CoreException
	{
		
		if (locations.length == 0)
			return null;

		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();
		List<String> existing = new ArrayList<String>();
		
		for (IProblemLocation location : locations) {
			
			String offset = Integer.toString(location.getOffset());
			if (location.getProblemId() == IPDTProblem.InterfaceRelated && ! existing.contains(offset)) {

				InterfaceMethodCompletionProposal prop = new InterfaceMethodCompletionProposal("", 0, 100, null, "Add unimplemented methods", 100);
				corrections.add(prop);
				existing.add(offset);
				
			}
		}
		
		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);		
		
	}
}
