/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pex.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


@SuppressWarnings("restriction")
abstract public class AbstractPropertyAndPreferencePage extends PropertyAndPreferencePage {

	protected AbstractOptionsConfigurationBlock fConfigurationBlock;

	@Override
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
		// Order is important!
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (fConfigurationBlock != null) {
			fConfigurationBlock.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}

	@Override
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}

		return super.performOk();
	}
	
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}
	
	@Override
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}
	}

	@Override
	public void dispose() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.dispose();
		}
		super.dispose();
	}
	
	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return fConfigurationBlock.hasProjectSpecificOptions(project);
	}
	
	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createBlockContents(composite);
	}

	@Override
	abstract protected String getPreferencePageID();

	@Override
	abstract protected String getPropertyPageID();

}
