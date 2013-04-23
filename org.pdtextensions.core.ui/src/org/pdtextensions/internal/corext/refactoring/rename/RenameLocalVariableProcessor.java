package org.pdtextensions.internal.corext.refactoring.rename;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.changes.DynamicValidationRefactoringChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.text.edits.ReplaceEdit;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.internal.corext.refactoring.Checks;

@SuppressWarnings("restriction")
public class RenameLocalVariableProcessor extends PHPRenameProcessor {

	public static final String IDENTIFIER = "org.pdtextensions.internal.corext.refactoring.rename.renameLocalVariableProcessor"; //$NON-NLS-1$
	private IField field;

	public RenameLocalVariableProcessor(IField field) {
		super(field);
		this.field = field;
	}

	@Override
	public RefactoringStatus checkNewElementName(String newName) throws CoreException {
		return Checks.checkFieldName(newName);
	}

	@Override
	protected String getRefactoringId() {
		return IScriptRefactorings.RENAME_LOCAL_VARIABLE;
	}

	@Override
	protected ReplaceEdit createReplaceEdit(SearchMatch match) {
		return new ReplaceEdit(match.getOffset(), currentName.length(), getNewElementName());
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameFieldRefactoring_name;
	}

	@Override
	public boolean isApplicable() throws CoreException {
		return Checks.isAvailable(modelElement);
	}

	protected RefactoringStatus doCheckFinalConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws CoreException, OperationCanceledException {
		pm.beginTask("", 1); //$NON-NLS-1$

		try {
			RefactoringStatus result = checkNewElementName(getNewElementName());
			if (result.hasFatalError()) {
				return result;
			}
			createLocalEdits(pm);
			return result;
		} catch (Exception e) {
			Logger.logException(e);
		} finally {
			pm.done();
		}

		return null;
	}

	private void createLocalEdits(IProgressMonitor pm) throws Exception {

		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(cu);
		IModelElement parent = field.getParent();
		ISourceRange range = null;

		if (parent.getElementType() == IModelElement.METHOD) {
			IMethod method = (IMethod) parent;
			range = method.getSourceRange();
		} else if (parent.getElementType() == IModelElement.SOURCE_MODULE) {
			ISourceModule module = (ISourceModule) parent;
			range = module.getSourceRange();
		}

		if (range == null) {
			return;
		}

		final int sourceStart = range.getOffset();
		final int sourceEnd = range.getOffset() + range.getLength();

		moduleDeclaration.traverse(new PHPASTVisitor() {
			@Override
			public boolean visit(VariableReference s) throws Exception {
				if (s.getName().equals(modelElement.getElementName())) {
					if (s.sourceStart() >= sourceStart && s.sourceEnd() <= sourceEnd) {
						ReplaceEdit replaceEdit = new ReplaceEdit(s.sourceStart(), currentName.length(), getNewElementName());
						addTextEdit(changeManager.get(cu), getProcessorName(), replaceEdit);
					}
				}
				return true;
			}
		});
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		monitor.beginTask(RefactoringCoreMessages.RenameFieldRefactoring_checking, 1);
		try {
			Change result = new DynamicValidationRefactoringChange(createRefactoringDescriptor(), getProcessorName(), changeManager.getAllChanges());
			monitor.worked(1);
			return result;
		} finally {
			changeManager.clear();
		}
	}
}
