/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.ui.contentassist;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposalComputer;

@SuppressWarnings("restriction")
public class ScriptCompletionProposalComputer extends
		PHPCompletionProposalComputer {

	@Override
	protected ScriptCompletionProposalCollector createCollector(
			ScriptContentAssistInvocationContext context) {

		return new PDTScriptCompletionProposalCollector(context.getDocument(), context.getSourceModule(), true);

	}

}
