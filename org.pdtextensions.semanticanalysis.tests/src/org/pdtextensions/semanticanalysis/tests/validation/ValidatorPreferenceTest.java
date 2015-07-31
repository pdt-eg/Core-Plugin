/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.php.internal.core.project.PHPNature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.validation.IValidatorManager;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class ValidatorPreferenceTest {
	private static IValidatorManager manager;
	private IProject project;
	private IScriptProject scriptProject;

	@BeforeClass
	public static void beforeClass() {
		manager = PEXAnalysisPlugin.getDefault().getValidatorManager();
	}

	@AfterClass
	public static void afterClass() {
		manager = null;
	}
	
	@Before
	public void before() throws CoreException {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("validator_preference"); //$NON-NLS-1$
		project.create(null);
		project.open(null);
		
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] {
				PHPNature.ID
		});
		project.setDescription(desc, null);
		
		scriptProject = DLTKCore.create(project);
	}
	
	@After
	public void after() throws CoreException, BackingStoreException {
		Preferences node = Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		node.clear();
		node.flush();
		
		project.delete(true, true, null);
	}

	@Test
	public void enablingValidators() throws BackingStoreException {
		IEclipsePreferences rootNode = Platform.getPreferencesService().getRootNode();
		assertTrue(manager.isEnabled(scriptProject));

		final Preferences globalNode = rootNode.node(InstanceScope.SCOPE).node(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		globalNode.putBoolean(PreferenceConstants.ENABLED, false);
		assertFalse(manager.isEnabled(scriptProject));

		Preferences projectNode = rootNode.node(ProjectScope.SCOPE).node(project.getName()).node(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID);
		projectNode.putBoolean(PreferenceConstants.ENABLED, true);
		assertTrue(manager.isEnabled(scriptProject));

		projectNode.remove(PreferenceConstants.ENABLED);
		
		assertFalse(manager.isEnabled(scriptProject));
		
		globalNode.putBoolean(PreferenceConstants.ENABLED, true);
	}
}
