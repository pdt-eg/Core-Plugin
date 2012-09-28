/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.contentassist;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.php.internal.core.format.FormatPreferencesSupport;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposal;
import org.eclipse.php.internal.ui.editor.contentassist.UseStatementInjector;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.ITextEditor;
import org.pdtextensions.core.compiler.MissingMethodImplementation;
import org.pdtextensions.core.ui.PDTPluginImages;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.ast.Formatter;
import org.pdtextensions.core.ui.codemanipulation.MethodStub;
import org.pdtextensions.core.visitor.PDTVisitor;


/**
 *
 * A completion proposal which generates unimplemented interface methods
 * into the class body.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class InterfaceMethodCompletionProposal extends PHPCompletionProposal {

	public InterfaceMethodCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);

	}
	
	@Override
	public Image getImage() {

		return PEXUIPlugin.getImageDescriptorRegistry().get(PDTPluginImages.DESC_CORRECTION_CHANGE);
		
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {

		IDocument document = viewer.getDocument();
		ITextEditor textEditor = ((PHPStructuredTextViewer) viewer)
				.getTextEditor();

		if (textEditor instanceof PHPStructuredEditor) {
			IModelElement editorElement = ((PHPStructuredEditor) textEditor)
					.getModelElement();
			if (editorElement != null) {
				
				ISourceModule sourceModule = ((ModelElement) editorElement)
						.getSourceModule();
								
				try {

					if (sourceModule.getTypes().length != 1) {
						return;
					}

					ModuleDeclaration module = SourceParserUtil.getModuleDeclaration(sourceModule);
					PDTVisitor visitor = new PDTVisitor(sourceModule);
					module.traverse(visitor);
					
					String code = "";
										
					char indentChar = FormatPreferencesSupport.getInstance().getIndentationChar(document);
					String indent = String.valueOf(indentChar);
					
					for (MissingMethodImplementation miss : visitor.getUnimplementedMethods()) {
						
						for (IMethod method : miss.getMisses()) {
							code += MethodStub.getMethodStub(method.getParent().getElementName(), method, indent, TextUtilities.getDefaultLineDelimiter(document), false);
						}
											
						document.replace(miss.getInjectionOffset(), 0, code);
						
						UseStatementInjector injector = new UseStatementInjector(this);
						injector.inject(document, getTextViewer(), offset);
						
						Formatter formatter = new Formatter();
						formatter.format(document);						
						
					}					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
