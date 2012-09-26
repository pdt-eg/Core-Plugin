/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.preferences.csfixer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.pdtextensions.core.ui.preferences.AbstractPropertyAndPreferencePage;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;


@SuppressWarnings("restriction")
public class PHPCSFixerPreferencePage extends AbstractPropertyAndPreferencePage {

	public static final String PREF_ID = "org.pdtextensions.core.ui.preferences.PHPCSFixerPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.pdtextensions.core.ui.propertyPages.PHPCSFixerPreferencePage"; //$NON-NLS-1$

	public PHPCSFixerPreferencePage() {
		setPreferenceStore(PEXAnalysisPlugin.getDefault().getPreferenceStore());
		setDescription("Configure PHP CS-Fixer phars and fixer options");
	}

	@Override
	public void createControl(Composite parent) {

		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new PHPCSFixerConfigurationBlock(
				getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}
}
