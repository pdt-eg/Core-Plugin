/*******************************************************************************
 * Copyright (c) 2005, 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.core.tests.formatter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.pdtextensions.core.tests.PEXCoreTestPlugin;
import org.pdtextensions.core.ui.ast.Formatter;
import org.pdtextensions.core.ui.formatter.profile.PSR2FormatterOptions;

/**
 * @since 0.17.0
 */
public class FormatterTest extends TestCase {
	
	@Test
	@SuppressWarnings({ "rawtypes" })
	public void testPsr2Formatter() throws IOException, URISyntaxException {
		
		Map options = PSR2FormatterOptions.getDefaultSettings().getMap();
		IDocument document = Formatter.createPHPDocument();
		Bundle bundle = Platform.getBundle(PEXCoreTestPlugin.PLUGIN_ID);
		URL unformatted = bundle.getEntry("workspace/formatter/psr2/unformatted.php");
		URL formatted = bundle.getEntry("workspace/formatter/psr2/formatted.php");
		
		document.set(IOUtils.toString(unformatted.openStream()));
		Formatter formatter = new Formatter(options);
		formatter.format(document);
		
		String formattedCode = IOUtils.toString(formatted.openStream());
		assertEquals(formattedCode, document.get());
	}
}
