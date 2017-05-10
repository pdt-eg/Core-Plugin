package org.pdtextensions.core.ui.util;

import java.util.ArrayList;

import java.util.List;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.model.PHPModelAccess;

@SuppressWarnings("restriction")
public class PHPContentProposalProvider implements IContentProposalProvider {

	private IModelElement element;
	
	public PHPContentProposalProvider(IModelElement element) {
		this.element = element;
	} 
	
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<PHPTypeContentProposal> props = new ArrayList<PHPTypeContentProposal>();
		IDLTKSearchScope scope = SearchEngine.createSearchScope(element);
		IType[] types = PHPModelAccess.getDefault().findTypes(contents, MatchRule.PREFIX,
				0, PHPFlags.AccNameSpace | PHPFlags.AccInterface, scope, null);

		for (IType type : types) {
			props.add(new PHPTypeContentProposal(type.getFullyQualifiedName().replace('$', '\\'), type));
		}
		return props.toArray(new PHPTypeContentProposal[props.size()]);
	}
	

}
