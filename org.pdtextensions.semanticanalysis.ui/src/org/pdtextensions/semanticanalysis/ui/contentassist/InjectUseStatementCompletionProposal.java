package org.pdtextensions.semanticanalysis.ui.contentassist;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.ICodeAssist;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.php.internal.ui.editor.contentassist.PHPCompletionProposal;
import org.eclipse.php.internal.ui.editor.contentassist.UseStatementInjector;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.pdtextensions.core.log.Logger;

@SuppressWarnings("restriction")
public class InjectUseStatementCompletionProposal extends PHPCompletionProposal {

	private IType type = null;
	
	public InjectUseStatementCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
	}
	
	public Image getImage() {
	
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_IMPDECL);
	
	}
	
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {

		
		IDocument document = viewer.getDocument();
		PHPStructuredEditor textEditor = (PHPStructuredEditor) ((PHPStructuredTextViewer) viewer)
				.getTextEditor();

		if (textEditor instanceof PHPStructuredEditor) {
			IModelElement editorElement = ((PHPStructuredEditor) textEditor)
					.getModelElement();
			if (editorElement != null) {

				IModelElement input = EditorUtility.getEditorInputModelElement(textEditor, false);

				if (input instanceof ICodeAssist) {
					
					try {
						
						int repOffset = getReplacementOffset();
						int length = getReplacementLength();
						IDLTKSearchScope scope = SearchEngine.createSearchScope(input.getScriptProject());
						String type = document.get(repOffset, length);
						IType[] types = PhpModelAccess.getDefault().findTypes(null, type, MatchRule.EXACT, 0, 0, scope, new NullProgressMonitor());
						
						if (types.length == 1) {
							this.type = types[0];
							UseStatementInjector injector = new UseStatementInjector(this);
							injector.inject(document, viewer, offset);
						} else if (types.length > 1) {
							
							Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
							
							ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new TypeLabelProvider());
							dialog.setElements(types);
							
							if (dialog.open() == Window.OK) {
								
								Object[] result = dialog.getResult();
								
								if (result != null && result.length == 1) {
									this.type = (IType) result[0];
									UseStatementInjector injector = new UseStatementInjector(this);
									injector.inject(document, viewer, offset);
								}
							}
						}
						
					} catch (BadLocationException e) {
						Logger.logException(e);
					}
				}
			}
		}
	}
	
	public IModelElement getModelElement() {

		return type;
		
	};
	
	private static class TypeLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			
			if (element instanceof IType) {
				return ((IType)element).getFullyQualifiedName("\\");
			}
			return super.getText(element);
		}
		
		@Override
		public Image getImage(Object element) {
			return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_CLASS);
		}
	}
}
