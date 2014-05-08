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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.php.core.tests.AbstractPDTTTest;
import org.eclipse.php.core.tests.PHPCoreTests;
import org.eclipse.php.core.tests.PdttFile;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.project.PHPNature;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;


@SuppressWarnings("restriction")
/**
 * Little modified version of org.eclipse.php.core.tests.errors.PHP54ErrorReportingTests class
 */
public class PHP54ValidationReportingTests extends AbstractPDTTTest {

	protected static final String[] TEST_DIRS = { "/workspace/errors/php54",
			"/workspace/errors/php53" };

	protected static Map<PdttFile, IFile> filesMap = new LinkedHashMap<PdttFile, IFile>();
	protected static IProject project;
	protected static int count;

	public static void setUpSuite() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("ErrorReportingTests");
		if (project.exists()) {
			return;
		}

		project.create(null);
		project.open(null);

		// configure nature
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { PHPNature.ID });
		project.setDescription(desc, null);

		for (PdttFile pdttFile : filesMap.keySet()) {
			IFile file = createFile(pdttFile.getFile().trim());
			filesMap.put(pdttFile, file);
		}

		PHPCoreTests.setProjectPhpVersion(project, PHPVersion.PHP5_4);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		PHPCoreTests.waitForIndexer();
		PHPCoreTests.waitForAutoBuild();
	}

	public static void tearDownSuite() throws Exception {
		project.close(null);
		project.delete(true, true, null);
		project = null;
	}

	public PHP54ValidationReportingTests(String description) {
		super(description);
	}
	
	public static Test suite() {

		TestSuite suite = new TestSuite("php54");

		for (String testsDirectory : TEST_DIRS) {

			for (final String fileName : getPDTTFiles(testsDirectory)) {
				try {
					final PdttFile pdttFile = new PdttFile(fileName);
					filesMap.put(pdttFile, null);

					suite.addTest(new PHP54ValidationReportingTests("/" + fileName) {

						protected void runTest() throws Throwable {
							IFile file = filesMap.get(pdttFile);
							Thread.sleep(4);

							StringBuilder buf = new StringBuilder();

							List<IMarker> markers = new ArrayList<IMarker>(
									Arrays.asList(file.findMarkers(
											PEXProblemIdentifier.MARKER_TYPE, true,
											IResource.DEPTH_ZERO)));
							Collections.sort(markers,
									new Comparator<IMarker>() {
										@Override
										public int compare(IMarker o1,
												IMarker o2) {
											try {
												int charStart = (Integer) o1
														.getAttribute(IMarker.CHAR_START);
												int o2CharStart = (Integer) o2
														.getAttribute(IMarker.CHAR_START);
												return Integer.compare(charStart, o2CharStart);
											} catch (CoreException e) {
												return -1;
											}
										}
									});
							for (IMarker marker : markers) {
								buf.append("\n[line=");
								buf.append(marker
										.getAttribute(IMarker.LINE_NUMBER));
								buf.append(", start=");
								buf.append(marker
										.getAttribute(IMarker.CHAR_START));
								buf.append(", end=");
								buf.append(marker
										.getAttribute(IMarker.CHAR_END));
								buf.append("] ");
								buf.append(marker.getAttribute(IMarker.MESSAGE))
										.append('\n');
							}

							assertContents(pdttFile.getExpected(),
									buf.toString());
						}
					});
				} catch (final Exception e) {
					suite.addTest(new TestCase(fileName) { // dummy test
															// indicating PDTT
															// file parsing
															// failure
						protected void runTest() throws Throwable {
							throw e;
						}
					});
				}
			}
		}

		// Create a setup wrapper
		TestSetup setup = new TestSetup(suite) {
			protected void setUp() throws Exception {
				setUpSuite();
			}

			protected void tearDown() throws Exception {
				tearDownSuite();
			}
		};

		return setup;
	}

	protected static IFile createFile(String data) throws Exception {
		IFile testFile = project.getFile("test" + (++count) + ".php");
		testFile.create(new ByteArrayInputStream(data.getBytes()), true, null);
		return testFile;
	}
}