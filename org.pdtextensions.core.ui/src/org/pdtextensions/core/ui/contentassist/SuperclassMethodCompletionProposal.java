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
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.php.internal.core.format.FormatterUtils;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.ui.codemanipulation.MethodStub;
import org.pdtextensions.core.util.PDTFormatterUtils;

/**
 *
 */
@SuppressWarnings("restriction")
public class SuperclassMethodCompletionProposal extends PHPCompletionProposal {

	private final IMethod method;
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
	public SuperclassMethodCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			Image image, String displayString, int relevance, IMethod iMethod, ISourceModule iSourceModule) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);

		method = iMethod;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#
	 * getReplacementString()
	 */
	@Override
	public String getReplacementString() {

		if (!replacementComputed) {
			return computeReplacementString();
		}
		return super.getReplacementString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#
	 * apply(org.eclipse.jface.text.ITextViewer, char, int, int)
	 */
	@Override
	public void apply(final ITextViewer viewer, char trigger, int stateMask, int offset) {

		super.apply(viewer, trigger, stateMask, offset);
		IModelElement modelElement = ((PHPStructuredEditor) ((PHPStructuredTextViewer) viewer).getTextEditor())
				.getModelElement();
		try {
			int off = viewer.getDocument().getLineInformationOfOffset(getReplacementOffset()).getOffset();
			Region region = new Region(off, getReplacementString().length() + (offset - off));
			PDTFormatterUtils.format(viewer.getDocument(),
					region,
					modelElement.getScriptProject().getProject());
			
			ISourceModule module = modelElement.getAncestor(ISourceModule.class);
			module.makeConsistent(null);
	
			IModelElement elementAt = module.getElementAt(offset);
			ISourceRange sourceRange = (elementAt instanceof IType ? ((IType)elementAt).getMethod(method.getElementName()) : (IMethod)elementAt).getSourceRange();
			String string = viewer.getDocument().get(sourceRange.getOffset(), sourceRange.getLength());
			int pos = string.indexOf('{') + 1;
			while (Character.isWhitespace(string.charAt(pos))) {
				pos++;
			}
			viewer.getDocument().getLineInformationOfOffset(sourceRange.getOffset() + pos);
			setCursorPosition(sourceRange.getOffset() - getReplacementOffset() + pos);
			
			// usestatement injection must be added manually,
			// as the PDT injector uses the model element from the proposal
			// to inject the statements. in this case, the modelelement
			// is the method from the parent class, so nothing is being
			// injected...
		} catch (BadLocationException e) {
			Logger.logException(e);
		} catch (ModelException e) {
			Logger.logException(e);
		}
		

	}
	
	
	private String computeReplacementString() {

		ITextViewer viewer = getTextViewer();
		IDocument document = viewer.getDocument();
		ITextEditor textEditor = ((PHPStructuredTextViewer) viewer).getTextEditor();

		try {

			if (textEditor instanceof PHPStructuredEditor) {
				IModelElement editorElement = ((PHPStructuredEditor) textEditor).getModelElement();
				if (editorElement != null) {

					char indentChar = FormatterUtils.getFormatterCommonPreferences().getIndentationChar(document);
					String indent = String.valueOf(indentChar);

					String code = "";
					code += MethodStub.getMethodStub(method.getElementName(), method, method, indent,
							TextUtilities.getDefaultLineDelimiter(document), true);
					return code;
				}
			}

		} catch (Exception e) {
			Logger.logException(e);
		}

		return "";
	}
}
