package org.pdtextensions.core.ui.ast;

import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.documentModel.provisional.contenttype.ContentTypeIdForPHP;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.eclipse.php.ui.format.PHPFormatProcessorProxy;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.StructuredTextMultiPassContentFormatter;
import org.pdtextensions.core.ui.TokenHolder;
import org.pdtextensions.core.ui.formatter.CodeFormatterOptions;

@SuppressWarnings({"restriction", "rawtypes"})
public class Formatter implements IContentFormatter {

	private ASTFormatter formatter;
	private Program program;
	private TokenHolder holder;
	private CodeFormatterOptions options;
	private boolean preset;

	private boolean createMarker = true;
	private boolean persistMarker = false;
	private int severity = IMarker.SEVERITY_WARNING;

	public Formatter() {
		options = null;
		preset = false;
	}

	
	public Formatter(Map map) {
		options = new CodeFormatterOptions(map);
		preset = true;
	}

	public void format(IDocument document) {
		
		Region region = new Region(0, document.getLength());
		format(document, region);

	}

	private void error(IFile file, String msg) {
		PEXUIPlugin.log(IStatus.ERROR, msg + ": "
				+ file.getFullPath().toString());
		if (createMarker) {
			PEXUIPlugin.getDefault().createMarker(file, severity, msg,
					persistMarker);
		}
	}

	public static IDocument createPHPDocument() {
		return StructuredModelManager.getModelManager()
				.createStructuredDocumentFor(
						ContentTypeIdForPHP.ContentTypeID_PHP);
	}

	public void format(IDocument document, IRegion region) {

		IFile file = PEXUIPlugin.getDefault().getFile(document);
		IProject project = null;
		if (file != null) {
			project = file.getProject();
		}

		if (!preset) {
			Map optionsMap = PEXUIPlugin.getDefault().getOptions(project);
			options = new CodeFormatterOptions(optionsMap);
		}
		if (file != null && options.format_html_region) {
			if (isFormatFile()) {
				MultiPassContentFormatter pdtFormatter = new MultiPassContentFormatter(
						IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING,
						IHTMLPartitions.HTML_DEFAULT);
				pdtFormatter
						.setMasterStrategy(new StructuredFormattingStrategy(
								new HTMLFormatProcessorImpl()));
				pdtFormatter.format(document, region);
			} else {
				StructuredTextMultiPassContentFormatter pdtFormatter = new StructuredTextMultiPassContentFormatter(
						IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING,
						IHTMLPartitions.HTML_DEFAULT);
				pdtFormatter
						.setMasterStrategy(new StructuredFormattingStrategy(
								new HTMLFormatProcessorImpl()));
				pdtFormatter.format(document, region);
				pdtFormatter.format(document, region);
			}
		}

		PHPVersion version = PHPVersion.PHP5_3;
		boolean useShortTags = true;
		if (file != null) {
			version = ProjectOptions.getPhpVersion(project);
			useShortTags = ProjectOptions.useShortTags(project);
		}
		
		program = null;
//		String toFormat = document.get().substring(region.getOffset(), 
//				region.getOffset() + region.getLength());
		
		String toFormat = document.get();		
		
		try {
			ASTParser parser = ASTParser.newParser(
					new StringReader(toFormat), version, useShortTags);
			program = parser.createAST(new NullProgressMonitor());
						
		} catch (Exception e) {
			PEXUIPlugin.log(e);
		}
		if (program == null) {
			error(file, "Could not get AST");
			return;
		}
		
//		IDocument tmp = createPHPDocument();		
//		tmp.set(toFormat);
		holder = new TokenHolder(document, project);
		if (holder == null) {
			error(file, "Could not get Tokens");
			return;
		}
		
		
		formatter = new ASTFormatter(program, holder, options);
		String result = formatter.format();
		if (result == null) {
			
			if (file != null)
				error(file, "Could not format (ast error)");
			
			return;
		}
		if (file == null) { // preview for preferences
			document.set(result);
			return;
		}
		if (document.get().equals(result)) {
			return;
		}
		IDocument formatted = createPHPDocument();
		formatted.set(result);
		
		if (TokenHolder.verify(document, formatted, project)) {			
			
			
//			try {
//				document.replace(region.getOffset(), region.getLength(), result);
//			} catch (BadLocationException e1) {
//				e1.printStackTrace();
//			}
			document.set(result);
			
			
			if (createMarker) {
				try {
					file.deleteMarkers(PEXUIPlugin.MARKER_ID, false,
							IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
				}
			}
		} else {
			error(file, "Could not format (verify error)");
		}		
	}

	public IFormattingStrategy getFormattingStrategy(String contentType) {
		return null;
	}

	private boolean isFormatFile() {
		StackTraceElement[] stackTraces = new Exception().getStackTrace();
		for (StackTraceElement stackTrace : stackTraces) {
			if (stackTrace.getClassName().equals(
					PHPFormatProcessorProxy.class.getName())) {
				if (stackTrace.getMethodName().equals("formatFile")) {
					return true;
				}
			}
		}
		return false;
	}
}
