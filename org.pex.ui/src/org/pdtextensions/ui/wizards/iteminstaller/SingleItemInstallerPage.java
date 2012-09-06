package org.pdtextensions.ui.wizards.iteminstaller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

abstract public class SingleItemInstallerPage extends AbstractItemInstallerPage {

	protected SingleItemInstallerPage(String pageName) {
		super(pageName);
	}

	@Override
	public boolean modifySelection(AbstractDescriptorItemUi item,
			boolean selected) {

		if (selected && this.lastSelected != null && item != this.lastSelected) {
			this.lastSelected.getCheckbox().setSelection(false);
		}

		this.lastSelected = selected ? item : null;
		this.setPageComplete(this.lastSelected != null);

		return true;
	}

	@Override
	public Button getItemButton(Composite checkboxContainer) {
		return new Button(checkboxContainer, SWT.RADIO);
	}
}
