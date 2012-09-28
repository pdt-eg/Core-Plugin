/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.builder;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.pdtextensions.core.compiler.IPDTProblem;
import org.pdtextensions.core.compiler.MissingMethodImplementation;
import org.pdtextensions.core.visitor.PDTVisitor;


public class PDTBuildParticipant implements IBuildParticipant {

	private IBuildContext context;
	
	@Override
	public void build(IBuildContext context) throws CoreException
	{

		try {
			this.context = context;
			ISourceModule sourceModule = context.getSourceModule();		
			ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
			
			PDTVisitor visitor = new PDTVisitor(sourceModule);
			moduleDeclaration.traverse(visitor);			
			
			if (visitor.getUnimplementedMethods().size() > 0) {
				reportUnimplementedMethods(visitor.getUnimplementedMethods(), sourceModule);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}

	@SuppressWarnings("deprecation")
	private void reportUnimplementedMethods(List<MissingMethodImplementation> misses, ISourceModule sourceModule)
	{

		ProblemSeverity severity = ProblemSeverity.WARNING;

		for (MissingMethodImplementation miss : misses) {

			int lineNo = context.getLineTracker().getLineInformationOfOffset(miss.getStart()).getOffset();
			String message = "The type " + miss.getTypeName() + " must implement the inherited method ";

			for (IMethod m : miss.getMisses()) {							
				message += m.getElementName() + ", ";							
			}

			IProblem problem = new DefaultProblem(context.getFileName(), message, IPDTProblem.InterfaceRelated,
					new String[0], severity, miss.getStart(), miss.getEnd(),lineNo);

			context.getProblemReporter().reportProblem(problem);

		}
	}
}
