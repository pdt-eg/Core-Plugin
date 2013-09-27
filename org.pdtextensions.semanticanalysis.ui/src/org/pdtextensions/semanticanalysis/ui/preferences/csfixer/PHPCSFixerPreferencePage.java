/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.ui.preferences.csfixer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.php.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.pdtextensions.semanticanalysis.ui.PEXAnalysisUIPlugin;

@SuppressWarnings("restriction")
public class PHPCSFixerPreferencePage extends PropertyAndPreferencePage {

	public static final String PREF_ID = "org.pdtextensions.core.ui.preferences.PHPCSFixerPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.pdtextensions.core.ui.propertyPages.PHPCSFixerPreferencePage"; //$NON-NLS-1$
	private PHPCSFixerConfigurationBlock fConfigurationBlock;

	public PHPCSFixerPreferencePage() {
		setPreferenceStore(PEXAnalysisUIPlugin.getDefault()
				.getParentPreferenceStore());
		setDescription("");
	}

	@Override
	public void createControl(Composite parent) {

		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new PHPCSFixerConfigurationBlock(
				getNewStatusChangedListener(), getProject(), container,
				new FixerKeyBag());

		super.createControl(parent);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return PEXAnalysisUIPlugin.getDefault().getParentPreferenceStore();
	}

	protected void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		if (fConfigurationBlock != null) {
			fConfigurationBlock
					.useProjectSpecificSettings(useProjectSpecificSettings);
		}
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}

	@Override
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}
		return super.performOk();
	}

	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return fConfigurationBlock.hasProjectSpecificOptions(project);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	public void dispose() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.dispose();
		}
		super.dispose();
	}
}
