package org.pdtextensions.core.visitor.namespaceValidation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.pdtextensions.core.compiler.IPDTProblem;

@SuppressWarnings("restriction")
public class UsageValidator extends PHPASTVisitor {

	protected List<UseStatement> statements;
	private IBuildContext buildContext;

	public UsageValidator() {

		statements = new ArrayList<UseStatement>();
	}

	public UsageValidator(IBuildContext buildContext) {
		this.buildContext = buildContext;
		statements = new ArrayList<UseStatement>();

	}

	public void addUseStatement(UseStatement s) {
		statements.add(s);
	}

	@SuppressWarnings("deprecation")
	public void checkParameter(FormalParameter s) {

		if (s.getParameterType() == null) {
			return;
		}
		
		if (s.getParameterType() instanceof FullyQualifiedReference) {
			
			for (UseStatement statement : statements) {
				if (isReferenced((FullyQualifiedReference)s.getParameterType(), statement)) {
					return;
				}
			}
		}

		for (UseStatement statement : statements) {
			if (isReferenced(s, statement)) {
				return;
			}
		}

		ProblemSeverity severity = ProblemSeverity.WARNING;

		int lineNo = buildContext.getLineTracker()
				.getLineInformationOfOffset(s.getParameterType().sourceStart())
				.getOffset();
		String message = "The type " + s.getParameterType().getName()
				+ " cannot be resolved.";

		// TODO: how can we check against ints to use the proper constructor
		// here
		// in InterfaceMethodQuickFixProcessor.hasCorrections
		// IProblem problem = new DefaultProblem(message,
		// PEXProblem.INTERFACE_IMPLEMENTATION,
		// new String[0], severity, miss.getStart(), miss.getEnd(),lineNo);

		IProblem problem = new DefaultProblem(message,
				IPDTProblem.UsageRelated, new String[0], severity, s
						.getParameterType().sourceStart(), s.getParameterType()
						.sourceEnd(), lineNo);

		buildContext.getProblemReporter().reportProblem(problem);

	}
	
	public void checkClassUsage(ClassInstanceCreation s) {
		
		if (s.getClassName() instanceof FullyQualifiedReference) {
			FullyQualifiedReference ref = (FullyQualifiedReference) s.getClassName();
		}
	}
	

	protected boolean isReferenced(FormalParameter param, UseStatement statement) {

		for (UsePart part : statement.getParts()) {

			if (part.getNamespace() != null
					&& param.getParameterType().getName()
							.equals(part.getNamespace().getName())) {
				return true;
			}

			if (part.getAlias() != null
					&& param.getParameterType().getName()
							.equals(part.getAlias().getName())) {
				return true;
			}
		}

		return false;
	}
	
	private boolean isReferenced(FullyQualifiedReference fqr,
			UseStatement statement) {
		
		for (UsePart part : statement.getParts()) {

			if (part.getNamespace() != null
					&& fqr.getFullyQualifiedName()
							.equals(part.getNamespace().getFullyQualifiedName())) {
				return true;
			}
		}

		return false;
	}
}
