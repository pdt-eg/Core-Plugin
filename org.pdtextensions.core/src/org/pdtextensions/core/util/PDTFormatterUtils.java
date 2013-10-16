/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.util;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.php.internal.core.format.DefaultCodeFormattingProcessor;
import org.eclipse.php.internal.core.format.ICodeFormattingProcessor;
import org.eclipse.php.internal.core.format.IFormatterProcessorFactory;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.pdtextensions.core.log.Logger;
import org.eclipse.php.internal.core.PHPVersion;

@SuppressWarnings("restriction")
public class PDTFormatterUtils {
	private final static String extensionName = "org.eclipse.php.ui.phpFormatterProcessor"; //$NON-NLS-1$
	private static IFormatterProcessorFactory formatterFactory;

	private static IFormatterProcessorFactory getFormatterFactory() {
		if (formatterFactory == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionName);
			for (int i = 0; i < elements.length; i++) {
				final IConfigurationElement element = elements[i];
				if (element.getName().equals("processor")) { //$NON-NLS-1$
					SafeRunner.run(new ISafeRunnable() {
						public void run() throws Exception {
							final Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
							if (object instanceof IFormatterProcessorFactory) {
								formatterFactory = (IFormatterProcessorFactory) object;
							}
						}

						public void handleException(Throwable exception) {
							Logger.logException(exception);
						}
					});

				}
			}
		}

		return formatterFactory;
	}
	
	public static ICodeFormattingProcessor createCodeFormatter(IDocument document, IRegion region, PHPVersion phpVersion, boolean useShortTags) throws Exception {
		if (getFormatterFactory() != null) {
			return formatterFactory.getCodeFormattingProcessor(document, phpVersion, useShortTags, region);
		}
		
		return new DefaultCodeFormattingProcessor(new HashMap());
	}
	
	public static void format(IDocument document, IRegion region, IProject project) {
		try {
			ICodeFormattingProcessor formatter = createCodeFormatter(document, region, ProjectOptions.getPhpVersion(project), ProjectOptions.useShortTags(project));
			formatter.getTextEdits().apply(document);
			
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}
