package org.pdtextensions.semanticanalysis.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.php.ui.text.correction.IInvocationContext;
import org.eclipse.php.ui.text.correction.IProblemLocation;
import org.eclipse.php.ui.text.correction.IQuickFixProcessor;
import org.eclipse.php.ui.text.correction.IQuickFixProcessorExtension;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.ui.contentassist.InjectUseStatementCompletionProposal;

public class ReferenceQuickFixProcessor implements IQuickFixProcessor, IQuickFixProcessorExtension {


	@Override
	public boolean hasCorrections(ISourceModule unit, int problemId) {
		return false;
	}

	@Override
	public boolean hasCorrections(ISourceModule unit, IProblemIdentifier identifier) {
		return identifier == PEXProblemIdentifier.USAGE_RELATED;
	}

	@Override
	public IScriptCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		List<IScriptCompletionProposal> corrections = new ArrayList<IScriptCompletionProposal>();
		List<String> existing = new ArrayList<String>();

		for (IProblemLocation location : locations) {
			String offset = Integer.toString(location.getOffset());
			if (location.getProblemIdentifier() == PEXProblemIdentifier.USAGE_RELATED && ! existing.contains(offset)) {

				InjectUseStatementCompletionProposal prop = new InjectUseStatementCompletionProposal("", location.getOffset(), location.getLength(), null, "Inject missing use statement", 100);
				corrections.add(prop);
				existing.add(offset);

			}
		}

		return corrections.toArray(new IScriptCompletionProposal[corrections.size()]);
	}
}
