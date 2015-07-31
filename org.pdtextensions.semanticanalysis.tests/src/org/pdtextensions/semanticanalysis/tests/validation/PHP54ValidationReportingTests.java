/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *     
 *******************************************************************************/
package org.pdtextensions.semanticanalysis.tests.validation;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.core.tests.errors.AbstractErrorReportingTests;
import org.eclipse.php.core.tests.runner.AbstractPDTTRunner.Context;
import org.eclipse.php.core.tests.runner.PDTTList;
import org.eclipse.php.core.tests.runner.PDTTList.BeforeList;
import org.eclipse.php.core.tests.runner.PDTTList.Parameters;
import org.eclipse.php.internal.core.PHPVersion;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.PreferenceConstants;
import org.pdtextensions.semanticanalysis.tests.PEXAnalysisTestPlugin;

@SuppressWarnings("restriction")

@RunWith(PDTTList.class)
public class PHP54ValidationReportingTests extends AbstractErrorReportingTests {

	@Parameters
	public static final String[] TEST_DIRS = { "/workspace/errors/php54", "/workspace/errors/php53" };

	@Context
	public static Bundle getBundle() {
		return PEXAnalysisTestPlugin.getDefault().getBundle();
	}

	public PHP54ValidationReportingTests(String[] fileNames) {
		super(fileNames);
	}

	@Override
	protected String getMarkerType() {
		return PEXProblemIdentifier.MARKER_TYPE;
	}

	@Override
	protected PHPVersion getPHPVersion() {
		return PHPVersion.PHP5_4;
	}

	@BeforeList
	@Override
	public void setUpSuite() throws Exception {
		Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(PEXAnalysisPlugin.VALIDATORS_PREFERENCES_NODE_ID)
				.remove(PreferenceConstants.ENABLED);
		super.setUpSuite();
	}
}