package org.pdtextensions.core.ui.util;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;

public class PHPContentProposalAdapter extends ContentProposalAdapter {

	public PHPContentProposalAdapter(Control control, IControlContentAdapter controlContentAdapter,
			PHPContentProposalProvider phpContentProposalProvider, KeyStroke keyStroke, char[] autoActivationCharacters) {
		super(control, controlContentAdapter, (IContentProposalProvider) phpContentProposalProvider, keyStroke, autoActivationCharacters);
		// TODO Auto-generated constructor stub
	}
	

}
