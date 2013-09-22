package org.pdtextensions.semanticanalysis.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.internal.ui.text.correction.IInvocationContext;
import org.eclipse.php.internal.ui.text.correction.IProblemLocation;
import org.eclipse.php.internal.ui.text.correction.IQuickFixProcessor;
import org.pdtextensions.semanticanalysis.ui.contentassist.InjectUseStatementCompletionProposal;

@SuppressWarnings("restriction")
public class ReferenceQuickFixProcessor implements IQuickFixProcessor {

	public ReferenceQuickFixProcessor() {
	}

	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId) {
		return false;
		//return problemId == IPDTProblem.UsageRelated;
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(
			IInvocationContext context, IProblemLocation[] locations)
			throws CoreException {
		
		if (locations.length == 0)
			return null;

		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();
		List<String> existing = new ArrayList<String>();
		/*
		for (IProblemLocation location : locations) {
			
			String offset = Integer.toString(location.getOffset());
			if (location.getProblemId() == IPDTProblem.UsageRelated && ! existing.contains(offset)) {

				InjectUseStatementCompletionProposal prop = new InjectUseStatementCompletionProposal("", location.getOffset(), location.getLength(), null, "Inject missing use statement", 100);
				corrections.add(prop);
				existing.add(offset);
				
			}
		}
		*/
		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);		
		
	}
}
