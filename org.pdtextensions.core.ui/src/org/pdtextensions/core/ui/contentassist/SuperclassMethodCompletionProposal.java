/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.ui.contentassist;


import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.php.internal.core.format.FormatPreferencesSupport;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pdtextensions.core.ui.codemanipulation.MethodStub;


/**
 *
 */
@SuppressWarnings("restriction")
public class SuperclassMethodCompletionProposal extends PHPCompletionProposal {

	private final IMethod method;
	private final ISourceModule source;
	private boolean replacementComputed = false;
	

	/**
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param image
	 * @param displayString
	 * @param relevance
	 * @param iMethod 
	 * @param iSourceModule 
	 */
	public SuperclassMethodCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance, IMethod iMethod, ISourceModule iSourceModule) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
		
		method = iMethod;
		source = iSourceModule;

	}


	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#getReplacementString()
	 */
	@Override
	public String getReplacementString() {
	
		if (!replacementComputed) {
			return computeReplacementString();
		}
		return super.getReplacementString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#apply(org.eclipse.jface.text.ITextViewer, char, int, int)
	 */
	@Override
	public void apply(final ITextViewer viewer, char trigger, int stateMask,
			int offset) {

		super.apply(viewer, trigger, stateMask, offset);
		
		// usestatement injection must be added manually,
		// as the PDT injector uses the model element from the proposal
		// to inject the statements. in this case, the modelelement
		// is the method from the parent class, so nothing is being 
		// injected...
		
//		final UseStatementInjector injector = new UseStatementInjector(this);
		

	}

	private String computeReplacementString() {

		ITextViewer viewer = getTextViewer();
		IDocument document = viewer.getDocument();
		ITextEditor textEditor = ((PHPStructuredTextViewer) viewer)
				.getTextEditor();

		try {

			if (textEditor instanceof PHPStructuredEditor) {
				IModelElement editorElement = ((PHPStructuredEditor) textEditor)
						.getModelElement();
				if (editorElement != null) {

					char indentChar = FormatPreferencesSupport.getInstance().getIndentationChar(document);
					String indent = String.valueOf(indentChar);

					String code = "";
					code += MethodStub.getMethodStub(method.getElementName(), method, indent, TextUtilities.getDefaultLineDelimiter(document), true);
					return code;

				}
			}		

		} catch (Exception e) {

			e.printStackTrace();
		}		
		
		return "";
	}
}
