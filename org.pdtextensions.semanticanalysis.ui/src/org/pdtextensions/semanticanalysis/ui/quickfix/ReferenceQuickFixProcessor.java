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
import org.pdtextensions.semanticanalysis.ui.contentassist.InjectUseStatementCompletionProposal;

@SuppressWarnings("restriction")
public class ReferenceQuickFixProcessor implements IQuickFixProcessor {


	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId) {
		return problemId == PEXProblemIdentifier.USAGE_RELATED.id();
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();
		List<String> existing = new ArrayList<String>();
		
		for (IProblemLocation location : locations) {
			String offset = Integer.toString(location.getOffset());
			if (location.getProblemId() == PEXProblemIdentifier.USAGE_RELATED.id() && ! existing.contains(offset)) {

				InjectUseStatementCompletionProposal prop = new InjectUseStatementCompletionProposal("", location.getOffset(), location.getLength(), null, "Inject missing use statement", 100); 
				corrections.add(prop);
				existing.add(offset);
				
			}
		}
		
		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);
	}
}
