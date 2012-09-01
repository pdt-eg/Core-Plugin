package com.pex.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.pex.ui.PEXUIPlugin;

@SuppressWarnings("restriction")
public abstract class AbstractOptionsConfigurationBlock extends
		OptionsConfigurationBlock {

	public AbstractOptionsConfigurationBlock(IStatusChangeListener context,
			IProject project, Key[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}
	
	protected final static Key getPEXKey(String key) {
		return getKey(PEXUIPlugin.PLUGIN_ID, key);
	}

	abstract public Control createBlockContents(Composite parent);

	@Override
	protected Control createContents(Composite parent) {
		return null;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue,
			String newValue) {
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return new String[] {
				"To activate the new settings, a full build is required.",
				"Run a full build now?" };
	}

	protected Composite getDefaultComposite(Composite parent) {
		
		GridLayout defaultLayout = new GridLayout();
		defaultLayout.marginHeight = 5;
		defaultLayout.marginWidth = 0;
		defaultLayout.numColumns = 3;

		Group defaultGroup = new Group(parent, SWT.NULL);
		defaultGroup.setText("CS Fixer Settings");
		defaultGroup.setLayout(defaultLayout);
		defaultGroup.setFont(parent.getFont());
		
		return defaultGroup;

	}
}
