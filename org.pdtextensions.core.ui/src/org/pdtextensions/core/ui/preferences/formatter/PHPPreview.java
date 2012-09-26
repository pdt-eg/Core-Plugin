/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.MarginPainter;
import org.eclipse.jface.text.WhitespaceCharacterPainter;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.php.internal.core.documentModel.provisional.contenttype.ContentTypeIdForPHP;
import org.eclipse.php.internal.ui.ColorManager;
import org.eclipse.php.internal.ui.editor.PHPStructuredTextViewer;
import org.eclipse.php.internal.ui.editor.configuration.PHPStructuredTextViewerConfiguration;
import org.eclipse.php.internal.ui.preferences.PHPSourcePreviewerUpdater;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.ast.Formatter;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;

@SuppressWarnings("restriction")
public abstract class PHPPreview {

	protected final StructuredTextViewer fSourceViewer;
	protected final StructuredTextViewerConfiguration fViewerConfiguration;
	protected final IStructuredDocument fPreviewDocument;
	protected final IPreferenceStore fPreferenceStore;
	protected final ColorManager fColorManager;
	protected final MarginPainter fMarginPainter;

	protected Map fWorkingValues;

	private int fTabSize = 0;
	private WhitespaceCharacterPainter fWhitespaceCharacterPainter;

	public PHPPreview(Map workingValues, Composite parent) {
		fPreviewDocument = StructuredModelManager.getModelManager()
				.createStructuredDocumentFor(
						ContentTypeIdForPHP.ContentTypeID_PHP);
		fWorkingValues = workingValues;

		IPreferenceStore[] chain = { PEXUIPlugin.getDefault()
				.getCombinedPreferenceStore() };
		fPreferenceStore = new ChainedPreferenceStore(chain);
		fSourceViewer = new PHPStructuredTextViewer(parent, null, null, false,
				SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fSourceViewer.setEditable(false);
		fViewerConfiguration = new PHPStructuredTextViewerSimpleConfiguration();
		fSourceViewer.configure(fViewerConfiguration);
		StyledText fText = fSourceViewer.getTextWidget();
		fText.setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont")); //$NON-NLS-1$

		fColorManager = new ColorManager();
		fMarginPainter = new MarginPainter(fSourceViewer);
		final RGB rgb = PreferenceConverter
				.getColor(
						fPreferenceStore,
						AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR);
		fMarginPainter.setMarginRulerColor(fColorManager.getColor(rgb));
		fSourceViewer.addPainter(fMarginPainter);

		new PHPSourcePreviewerUpdater(fSourceViewer, fViewerConfiguration,
				fPreferenceStore);
		fSourceViewer.setDocument(fPreviewDocument);
	}

	public Control getControl() {
		return fSourceViewer.getControl();
	}

	public void update() {
		if (fWorkingValues == null) {
			fPreviewDocument.set(""); //$NON-NLS-1$
			return;
		}

		// update the print margin
		final String value = (String) fWorkingValues
				.get(CodeFormatterConstants.FORMATTER_LINE_SPLIT);
		final int lineWidth = getPositiveIntValue(value, 0);
		fMarginPainter.setMarginRulerColumn(lineWidth);

		// update the tab size
		final int tabSize = getPositiveIntValue(
				(String) fWorkingValues
						.get(CodeFormatterConstants.FORMATTER_TAB_SIZE),
				0);
		if (tabSize != fTabSize)
			fSourceViewer.getTextWidget().setTabs(tabSize);
		fTabSize = tabSize;

		final StyledText widget = (StyledText) fSourceViewer.getControl();
		final int height = widget.getClientArea().height;
		final int top0 = widget.getTopPixel();

		final int totalPixels0 = getHeightOfAllLines(widget);
		final int topPixelRange0 = totalPixels0 > height ? totalPixels0
				- height : 0;

		widget.setRedraw(false);
		doFormatPreview();
		fSourceViewer.setSelection(null);

		final int totalPixels1 = getHeightOfAllLines(widget);
		final int topPixelRange1 = totalPixels1 > height ? totalPixels1
				- height : 0;

		final int top1 = topPixelRange0 > 0 ? (int) (topPixelRange1 * top0 / (double) topPixelRange0)
				: 0;
		widget.setTopPixel(top1);
		widget.setRedraw(true);
	}

	private int getHeightOfAllLines(StyledText styledText) {
		int height = 0;
		int lineCount = styledText.getLineCount();
		for (int i = 0; i < lineCount; i++)
			height = height
					+ styledText.getLineHeight(styledText.getOffsetAtLine(i));
		return height;
	}

	protected abstract void doFormatPreview();

	private static int getPositiveIntValue(String string, int defaultValue) {
		try {
			int i = Integer.parseInt(string);
			if (i >= 0) {
				return i;
			}
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}

	public Map getWorkingValues() {
		return fWorkingValues;
	}

	public void setWorkingValues(Map workingValues) {
		fWorkingValues = workingValues;
	}

	public void showInvisibleCharacters(boolean enable) {
		if (enable) {
			if (fWhitespaceCharacterPainter == null) {
				fWhitespaceCharacterPainter = new WhitespaceCharacterPainter(
						fSourceViewer);
				fSourceViewer.addPainter(fWhitespaceCharacterPainter);
			}
		} else {
			fSourceViewer.removePainter(fWhitespaceCharacterPainter);
			fWhitespaceCharacterPainter = null;
		}
	}

	private class PHPStructuredTextViewerSimpleConfiguration extends
			PHPStructuredTextViewerConfiguration {

		@Override
		public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
			return new Formatter();
		}

		@Override
		public int[] getConfiguredTextHoverStateMasks(
				ISourceViewer sourceViewer, String contentType) {
			return new int[0];
		}

		@Override
		public ITextHover getTextHover(ISourceViewer sourceViewer,
				String contentType, int stateMask) {
			return null;
		}

		@Override
		public ITextHover getTextHover(ISourceViewer sourceViewer,
				String contentType) {
			return null;
		}

		@Override
		public IContentAssistProcessor[] getContentAssistProcessors(
				ISourceViewer sourceViewer, String partitionType) {
			return null;
		}

		@Override
		public IContentAssistant getPHPContentAssistant(
				ISourceViewer sourceViewer) {
			return null;
		}

		@Override
		public IContentAssistant getPHPContentAssistant(
				ISourceViewer sourceViewer, boolean reSet) {
			return null;
		}

		@Override
		public IInformationPresenter getHierarchyPresenter(
				PHPStructuredTextViewer viewer, boolean doCodeResolve) {
			return null;
		}

		@Override
		public IQuickAssistAssistant getQuickAssistAssistant(
				ISourceViewer sourceViewer) {
			return null;
		}
	}
}
