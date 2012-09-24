/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.swt.widgets.Composite;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.ast.Formatter;

public class SnippetPreview extends PHPPreview {

	public final static class PreviewSnippet {

		public String header;
		public final String source;
		public final int kind;

		public PreviewSnippet(int kind, String source) {
			this.kind = kind;
			this.source = source;
		}
	}

	private ArrayList fSnippets;

	public SnippetPreview(Map workingValues, Composite parent) {
		super(workingValues, parent);
		fSnippets = new ArrayList();
	}

	protected void doFormatPreview_previous() {
		if (fSnippets.isEmpty()) {
			fPreviewDocument.set(""); //$NON-NLS-1$
			return;
		}

		//This delimiter looks best for invisible characters
		final String delimiter = "\n"; //$NON-NLS-1$

		final StringBuffer buffer = new StringBuffer();
		for (final Iterator iter = fSnippets.iterator(); iter.hasNext();) {
			final PreviewSnippet snippet = (PreviewSnippet) iter.next();
			String formattedSource;
			try {
				final IContentFormatter formatter = new Formatter(
						fWorkingValues);
				Document snippetDocument = new Document();
				snippetDocument.set("<?php\n" + snippet.source);
				formatter.format(snippetDocument, new Region(0,
						fPreviewDocument.getLength()));
				formattedSource = snippetDocument.get();
				if (formattedSource.startsWith("<?php")) {
					formattedSource = formattedSource.substring(5);
				}
			} catch (Exception e) {
				final IStatus status = new Status(IStatus.ERROR,
						PEXUIPlugin.PLUGIN_ID, IStatus.ERROR,
						FormatterMessages.JavaPreview_formatter_exception, e);
				PEXUIPlugin.log(status);
				continue;
			}
			buffer.append(delimiter);
			buffer.append(formattedSource);
			//buffer.append(delimiter);
			//buffer.append(delimiter);
		}
		fPreviewDocument.set("<?php" + buffer.toString());
	}

	protected void doFormatPreview() {
		if (fSnippets.isEmpty()) {
			fPreviewDocument.set(""); //$NON-NLS-1$
			return;
		}

		//This delimiter looks best for invisible characters
		final String delimiter = "\n"; //$NON-NLS-1$

		final StringBuffer buffer = new StringBuffer();
		final Iterator iter = fSnippets.iterator();
		while (iter.hasNext()) {
			final PreviewSnippet snippet = (PreviewSnippet) iter.next();
			buffer.append(snippet.source);
			if (!snippet.source.endsWith("\n")) {
				buffer.append(delimiter);
			}
			buffer.append(delimiter);
			buffer.append(delimiter);
		}

		final IContentFormatter formatter = new Formatter(fWorkingValues);
		final Document snippetDocument = new Document();
		snippetDocument.set("<?php\n\n" + buffer.toString());
		formatter.format(snippetDocument,
				new Region(0, snippetDocument.getLength()));
		fPreviewDocument.set(snippetDocument.get());
	}

	public void add(PreviewSnippet snippet) {
		fSnippets.add(snippet);
	}

	public void remove(PreviewSnippet snippet) {
		fSnippets.remove(snippet);
	}

	public void addAll(Collection snippets) {
		fSnippets.addAll(snippets);
	}

	public void clear() {
		fSnippets.clear();
	}
}
