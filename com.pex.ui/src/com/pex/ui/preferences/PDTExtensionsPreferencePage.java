package com.pex.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;

@SuppressWarnings("restriction")
public class PDTExtensionsPreferencePage extends PropertyAndPreferencePage
		implements IWorkbenchPreferencePage {

	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);

		Label descLabel = new Label(comp, SWT.NONE);
		descLabel.setText("Expand the tree to edit PDT Extensions preferences");

		return comp;
	}
	
	protected Control createPreferenceContent(Composite composite) {
		return null;
	}

	@Override
	protected String getPreferencePageID() {
		return null;
	}

	@Override
	protected String getPropertyPageID() {
		return null;
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return false;
	}
}
