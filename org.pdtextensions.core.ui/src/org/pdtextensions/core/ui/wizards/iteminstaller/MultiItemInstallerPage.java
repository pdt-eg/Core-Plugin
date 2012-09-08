package org.pdtextensions.core.ui.wizards.iteminstaller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pdtextensions.core.model.InstallableItem;

public abstract class MultiItemInstallerPage extends AbstractItemInstallerPage {

	protected List<InstallableItem> selectedItems = new ArrayList<InstallableItem>();
	
	protected MultiItemInstallerPage(String pageName) {
		super(pageName);
	}

	@Override
	abstract protected void createRefreshJob();

	public boolean modifySelection(AbstractDescriptorItemUi item,
			boolean selected) {

		InstallableItem installableItem = item.getItem();
		
		if (installableItem != null) {
			if (selected && !selectedItems.contains(installableItem)) {
				selectedItems.add(installableItem);
			} else if (!selected && selectedItems.contains(installableItem)) {
				selectedItems.remove(installableItem);
			}
		}
		
		this.lastSelected = selected ? item : null;
		this.setPageComplete(selectedItems.size() > 0);

		return true;
	}

	@Override
	public Button getItemButton(Composite checkboxContainer) {
		return new Button(checkboxContainer, SWT.CHECK);
	}
}
