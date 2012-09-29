/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.pdtextensions.core.compiler.MissingMethodImplementation;
import org.pdtextensions.core.visitor.interfacevalidation.ImplementationValidator;
import org.pdtextensions.core.visitor.namespaceValidation.UsageValidator;

@SuppressWarnings("restriction")
public class PDTVisitor extends PHPASTVisitor {

	private final ISourceModule context;
	private List<MissingMethodImplementation> missingInterfaceImplemetations = new ArrayList<MissingMethodImplementation>();
	private int nameStart;
	private int nameEnd;

	private UsageValidator usageValidator;
	private IBuildContext buildContext;

	public PDTVisitor(ISourceModule sourceModule, IBuildContext buildContext) {

		this.context = sourceModule;
		this.buildContext = buildContext;
		usageValidator = new UsageValidator(buildContext);
	}

	public PDTVisitor(ISourceModule sourceModule) {
		context = sourceModule;
		usageValidator = new UsageValidator(null);
	}

	@Override
	public boolean visit(UseStatement s) throws Exception {

		usageValidator.visit(s);
		return false;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation s) throws Exception {
		usageValidator.visit(s);
		return false;
	}

	@Override
	public boolean visit(FormalParameter s) throws Exception {

		usageValidator.visit(s);
		return false;
	}
	
	@Override
	public boolean visit(StaticConstantAccess s) throws Exception {
		
		usageValidator.visit(s);
		return false;
	}
	
	
	@Override
	public boolean visit(StaticFieldAccess s) throws Exception {
		
		usageValidator.visit(s);
		return false;
	}
	
	@Override
	public boolean visit(StaticMethodInvocation s) throws Exception {
		
		usageValidator.visit(s);
		return false;
	}

	public boolean endvisit(ClassDeclaration s) throws Exception {

		nameStart = s.getNameStart();
		nameEnd = s.getNameEnd();

		
		// TODO: Refactor to use PHPModelUtils.getUnimplementedMethods 
		// i actually found this here... org.eclipse.php.internal.core.typeinference.PHPModelUtils.getUnimplementedMethods(IType, IModelAccessCache, IProgressMonitor)
		ImplementationValidator impVal = new ImplementationValidator(
				buildContext, context, s);
		impVal.reportUnimplementedMethods();

		return false;
	}

	public List<MissingMethodImplementation> getUnimplementedMethods() {

		return missingInterfaceImplemetations;
	}

	public int getNameEnd() {
		return nameEnd;
	}

	public int getNameStart() {
		return nameStart;
	}
}
