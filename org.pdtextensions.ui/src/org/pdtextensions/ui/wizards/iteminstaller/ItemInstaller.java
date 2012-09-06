package org.pdtextensions.ui.wizards.iteminstaller;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ItemInstaller {

	void configureLook(Control control, Color background);	

	Font getH1Font();
	
	Font getH2Font();
	
	Color getDisabledColor();

	void itemGainedFocus(AbstractDescriptorItemUi abstractDescriptorItemUi);

	boolean modifySelection(AbstractDescriptorItemUi abstractDescriptorItemUi, boolean selected);

	Button getItemButton(Composite checkboxContainer);
	
	boolean doFinish();

}
