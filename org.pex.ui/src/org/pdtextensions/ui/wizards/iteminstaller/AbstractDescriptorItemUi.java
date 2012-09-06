package org.pdtextensions.ui.wizards.iteminstaller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.pdtextensions.core.model.InstallableItem;

public class AbstractDescriptorItemUi implements PropertyChangeListener,
		Runnable {

	protected final InstallableItem installableItem;

    protected final Display display;
    
    protected final ItemInstaller installer;
    
    protected Button checkbox;

    protected Label iconLabel;

    protected Label nameLabel;

    protected Label providerLabel;

    protected Label description;

    protected Composite checkboxContainer;

    protected Composite itemContainer;

    protected Color background;
	
	protected final Composite parent;
	
	
//    /** */
//    private ToolItem infoButton;
	
//    private Image iconImage;

//    /** */
//    private Image warningIconImage;

    /**
     * Constructor.
     * @param installableItem
     * @param parent
     * @param background
     */
    public AbstractDescriptorItemUi(final ItemInstaller installer, InstallableItem installItem, Composite parent,
            Color background) {
    	
        this.installer = installer;
        this.installableItem = installItem;
        this.background = background;
        this.display = parent.getDisplay();
        this.parent = parent;
        
        createBody(parent);

    }
    
    protected void createBody(Composite parent) {
    	
        // TODO installableItem.addPropertyChangeListener(this);

        itemContainer = new Composite(parent, SWT.NULL);

        installer.configureLook(itemContainer, background);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(itemContainer);
        GridLayout layout = new GridLayout(4, false);
        layout.marginLeft = 7;
        layout.marginTop = 2;
        layout.marginBottom = 2;
        itemContainer.setLayout(layout);

        checkboxContainer = new Composite(itemContainer, SWT.NULL);
        installer.configureLook(checkboxContainer, background);
        GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(checkboxContainer);
        GridLayoutFactory.fillDefaults().spacing(1, 1).numColumns(2).applyTo(checkboxContainer);

        
        checkbox = installer.getItemButton(checkboxContainer);
        checkbox.setText(" "); //$NON-NLS-1$
        // help UI tests
        checkbox.setData("connectorId", installableItem); //$NON-NLS-1$
        installer.configureLook(checkbox, background);
        checkbox.setSelection(false);
        checkbox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
            	installer.itemGainedFocus(AbstractDescriptorItemUi.this);
            }
        });

        GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(getCheckbox());

        iconLabel = new Label(checkboxContainer, SWT.NULL);
        installer.configureLook(iconLabel, background);
        GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(iconLabel);

//        if (connector.getIcon() != null) {
//            iconImage = computeIconImage(connector.getSource(), connector.getIcon(), 32, false);
//            if (iconImage != null) {
//                iconLabel.setImage(iconImage);
//            }
//        }

        nameLabel = new Label(itemContainer, SWT.NULL);
        installer.configureLook(nameLabel, background);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
        nameLabel.setFont(installer.getH2Font());
        nameLabel.setText(installableItem.getName());

        providerLabel = new Label(itemContainer, SWT.NULL);
        installer.configureLook(providerLabel, background);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(providerLabel);
        // TODO
//        this.providerLabel.setText(SVNUIMessages.format(SVNUIMessages.ConnectorDiscoveryWizardMainPage_provider_and_license,
//                new Object[]{connector.getProvider(), connector.getLicense()}));

//        if (hasTooltip(connector)) {
//            ToolBar toolBar = new ToolBar(itemContainer, SWT.FLAT);
//            toolBar.setBackground(background);
//
//            infoButton = new ToolItem(toolBar, SWT.PUSH);
//            infoButton.setImage(infoImage);
//            infoButton.setToolTipText(SVNUIMessages.ConnectorDiscoveryWizardMainPage_tooltip_showOverview);
//            hookTooltip(toolBar, infoButton, itemContainer, nameLabel, connector.getSource(),
//                    connector.getOverview());
//            GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(toolBar);
//        } else {
            new Label(itemContainer, SWT.NULL).setText(" "); //$NON-NLS-1$
//        }

        description = new Label(itemContainer, SWT.NULL | SWT.WRAP);
        installer.configureLook(description, background);

        GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(100, SWT.DEFAULT).applyTo(description);
        String descriptionText = installableItem.getDescription();
        int maxDescriptionLength = 162;
        if (descriptionText.length() > maxDescriptionLength) {
            descriptionText = descriptionText.substring(0, maxDescriptionLength);
        }
        description.setText(descriptionText.replaceAll("(\\r\\n)|\\n|\\r", " ")); //$NON-NLS-1$ //$NON-NLS-2$

        // always disabled color to make it less prominent
        providerLabel.setForeground(installer.getDisabledColor());

        getCheckbox().addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
            	
                maybeModifySelection(AbstractDescriptorItemUi.this.checkbox.getSelection());
            }
        });
        
        MouseListener connectorItemMouseListener = new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
            	
                boolean selected = !AbstractDescriptorItemUi.this.checkbox.getSelection();
                if (maybeModifySelection(selected)) {
                	AbstractDescriptorItemUi.this.checkbox.setSelection(selected);
                }
            }
        };
        checkboxContainer.addMouseListener(connectorItemMouseListener);
        itemContainer.addMouseListener(connectorItemMouseListener);
        iconLabel.addMouseListener(connectorItemMouseListener);
        nameLabel.addMouseListener(connectorItemMouseListener);
        providerLabel.addMouseListener(connectorItemMouseListener);
        description.addMouseListener(connectorItemMouseListener);    	
    	
    }

    /**
     * Tests if the selected checkboy can be set.
     * @param selected selected checkbox
     * @return true if the modification can be set.
     */
    protected boolean maybeModifySelection(boolean selected) {
    	
    	return installer.modifySelection(this, selected);
    }

    /**
     * Updates the availability.
     */
    public void updateAvailability() {
    	
        boolean enabled = /*connector.getAvailable() != null && connector.getAvailable()*/ true;

        if (checkbox == null) {
        	return;
        }
        
        checkbox.setEnabled(enabled);
        nameLabel.setEnabled(enabled);
        providerLabel.setEnabled(enabled);
        description.setEnabled(enabled);
        Color foreground;
        if (enabled) {
            foreground = itemContainer.getForeground();
        } else {
            foreground = installer.getDisabledColor();
        }
        nameLabel.setForeground(foreground);
        description.setForeground(foreground);

//        if (iconImage != null) {
//            boolean unavailable = !enabled && connector.getAvailable() != null;
//            if (unavailable) {
//                if (warningIconImage == null) {
//                    warningIconImage = new DecorationOverlayIcon(iconImage, SVNTeamUIPlugin.instance().getImageDescriptor("icons/discovery/message_warning.gif"),
//                            IDecoration.TOP_LEFT).createImage();
//                    disposables.add(warningIconImage);
//                }
//                iconLabel.setImage(warningIconImage);
//            } else if (warningIconImage != null) {
//                iconLabel.setImage(iconImage);
//            }
//        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        display.asyncExec(this);
    }

    @Override
    public void run() {
        if (!itemContainer.isDisposed()) {
            updateAvailability();
        }
    }

	public Button getCheckbox() {
		return checkbox;
	}

	public InstallableItem getItem() {
		return installableItem;
	}

	public Control getItemContainer()
	{
		return itemContainer;
	}
}
