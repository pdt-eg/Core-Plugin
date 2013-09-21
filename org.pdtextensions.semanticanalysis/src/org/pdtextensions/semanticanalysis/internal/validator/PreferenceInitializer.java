/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.validator;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.pdtextensions.semanticanalysis.IValidatorManager;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.model.validators.Validator;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	private boolean init = false;

	@Inject
	private IValidatorManager manager;

	@Override
	public void initializeDefaultPreferences() {
		init();
		ScopedPreferenceStore prefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		prefs.setDefault(PreferenceConstants.ENABLED, true);
		for (Validator v : manager.getValidators()) {
			prefs.setDefault(v.getId(), v.getDefaultSeverity().toString());
		}
	}

	private void init() {
		if (init) {
			return;
		}
		ContextInjectionFactory.inject(this, PEXAnalysisPlugin.getEclipseContext());
		init = true;
	}
}
