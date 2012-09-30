package org.pdtextensions.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemFactory;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptModelMarker;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.php.internal.core.PHPCoreConstants;
import org.eclipse.php.internal.core.PHPToolkitUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.tasks.TaskTag;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.validation.validator.ImplementationValidator;
import org.pdtextensions.core.validation.validator.UsageValidator;

/**
 * Main WST validator. Currently checks for:
 * 
 * - Missing methods for interface implementors
 * - Missing use statements for type references
 * 
 * @see ImplementationValidator
 * @see UsageValidator
 * 
 * @since 0.0.12
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class PEXValidator extends AbstractValidator {

	private IResource resource;
	private IStructuredDocument document;
	private ModuleDeclaration moduleDeclaration;
	private ISourceModule source;
	private IFile file;
	
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		
		// process only PHP files
		if (resource.getType() != IResource.FILE
				|| !(PHPToolkitUtil.isPhpFile((IFile) resource))) {
			return null;
		}

		this.resource = resource;
		ValidationResult result = new ValidationResult();
		IReporter reporter = result.getReporter(monitor);
		validateFile(reporter, (IFile) resource, kind);
		
		return result;
		
	}
	
	public void validateFile(IReporter reporter, IFile file, int kind) {
		
		try {
			file.deleteMarkers(PHPCoreConstants.PHP_MARKER_TYPE, false,
					IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
		}
		
		this.file = file;
		source = (ISourceModule) DLTKCore.create(file);
		moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(source);
		
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(file);
		document = model.getStructuredDocument();
		
		try {
			
			validateInterfaceImplementations();
			validateResolvableReferences();
			
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	private void validateResolvableReferences() throws Exception {
		
		UsageValidator validator = new UsageValidator(source);
		moduleDeclaration.traverse(validator);
		
		for (IProblem problem : validator.getProblems()) {
			reportProblem(problem, IPDTProblem.UsageRelated);
		}
	}
	

	private void validateInterfaceImplementations() throws Exception {
		
		ImplementationValidator visitor = new ImplementationValidator(source);
		moduleDeclaration.traverse(visitor);
		
		if (visitor.hasMissing() && visitor.getClassDeclaration() != null) {
			int start = visitor.getClassDeclaration().getNameStart();
			int end = visitor.getClassDeclaration().getNameEnd();
			
			String message = "The class " + visitor.getClassDeclaration().getName() + " must implement the inherited abstract method "
					+ visitor.getMissing().get(0).getFirstMethodName();
			reportProblem(IPDTProblem.InterfaceRelated, message, start, end - start, TaskTag.PRIORITY_HIGH);
		}
	}

	private String getProblemStr(IStructuredDocument document, int lineNumber,
			int offset, int length) throws BadLocationException {
		// get line info to identify the end of the task
		IRegion lineInformation = document.getLineInformation(lineNumber);
		int lineStart = lineInformation.getOffset();
		int lineEnd = lineStart + lineInformation.getLength();

		// identify the actual end of the task: either end of line or end of the
		// token
		// we could have 2 tasks in the same line
		int tokenEnd = offset + length;
		int taskEnd = Math.min(tokenEnd, lineEnd);

		String problemStr = document.get(offset, taskEnd - offset);
		problemStr = problemStr.trim();

		return problemStr;

	}
	
	private void reportProblem(IProblem problem, int type) throws CoreException, BadLocationException {
		
		int offset = problem.getSourceStart();
		int length = problem.getSourceEnd() - problem.getSourceStart();
		int lineNumber = document.getLineOfOffset(offset);
		
		String taskStr = getProblemStr(document, lineNumber, offset, length);
		int charEnd = offset + taskStr.length();

		// report the problem
		createMarker(type, file, taskStr, lineNumber, TaskTag.PRIORITY_HIGH, offset, charEnd, problem.getMessage());
	}
	
	private void reportProblem(int type, String message, int offset, int length, int priority)
			throws BadLocationException, CoreException {
		int lineNumber = document.getLineOfOffset(offset);

		String taskStr = getProblemStr(document, lineNumber, offset, length);
		// the end of the string to be highlighted
		int charEnd = offset + taskStr.length();

		// report the problem
		createMarker(type, file, taskStr, lineNumber, priority, offset, charEnd, message);
	}

	@SuppressWarnings("deprecation")
	private void createMarker(int type, IFile file, String taskStr, int lineNumber,
			int priority, int offset, int charEnd, String message) throws CoreException {
		
		DefaultProblemFactory factory = new DefaultProblemFactory();

		//TODO: make severity configurable
		IProblem problem = new DefaultProblem(message,
				type, new String[0], ProblemSeverity.WARNING, offset, charEnd, lineNumber + 1);
		
		IMarker marker = factory.createMarker(resource, problem);
//		IMarker marker = file.createMarker(PEXCoreConstants.MISSING_METHOD_MARKER);
		marker.setAttribute(IMarker.PROBLEM, true);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber + 1);
		marker.setAttribute(IMarker.CHAR_START, offset);
		marker.setAttribute(IMarker.CHAR_END, charEnd);
		marker.setAttribute(IMarker.MESSAGE, taskStr);
		marker.setAttribute(IMarker.USER_EDITABLE, false);
		marker.setAttribute(IMarker.PRIORITY, priority);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IScriptModelMarker.ID,
				DefaultProblemIdentifier.encode(problem.getID()));
		
	}
}
