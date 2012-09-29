package org.pdtextensions.core.visitor.interfacevalidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.pdtextensions.core.compiler.IPDTProblem;
import org.pdtextensions.core.compiler.MissingMethodImplementation;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.util.PDTModelUtils;

@SuppressWarnings("restriction")
public class ImplementationValidator {

	private ISourceModule context;
	private ClassDeclaration classDeclaration;
	private List<MissingMethodImplementation> missingMethods;
	private IBuildContext buildContext;

	public ImplementationValidator(IBuildContext buildContext, ISourceModule context, ClassDeclaration s) {
		
		this.buildContext = buildContext;
		this.context = context;
		this.classDeclaration = s;
		
		missingMethods = new ArrayList<MissingMethodImplementation>();
		init();
	}
	
	protected void init() {
		
		if (classDeclaration.isAbstract()) {
			return;
		}
		
		Collection<TypeReference> interfaces = classDeclaration.getInterfaceList();
		IScriptProject project = context.getScriptProject();
		List<IMethod> unimplemented = new ArrayList<IMethod>();		
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);		
		PhpModelAccess model = PhpModelAccess.getDefault();		
		IType classType = null;
		IType nss = PHPModelUtils.getCurrentNamespace(context, classDeclaration.getNameStart());
				
		// namespaced class
		if (nss != null) {			
			IType[] ts = model.findTypes(nss.getElementName(), classDeclaration.getName(), MatchRule.EXACT, 0, 0, scope, null);
			if (ts.length != 1) {
				return;
			}			
			classType = ts[0];
			
		} else {
			IType[] ts = model.findTypes(classDeclaration.getName(), MatchRule.EXACT, 0, 0, scope, null);
			if (ts.length != 1) {
				return;
			}			
			classType = ts[0];			
		}
		
		// iterate over all interfaces and check if the current class
		// or any of the superclasses implements the method
		for (TypeReference interf : interfaces) {
			
			if (interf instanceof FullyQualifiedReference) {
				
				FullyQualifiedReference fqr = (FullyQualifiedReference) interf;				
				String name = null;
				
				// we have a namespace
				if (fqr.getNamespace() != null) {
					name = fqr.getNamespace().getName() + "\\" + fqr.getName();					
				} else {
					IEvaluatedType eval = PHPTypeInferenceUtils.resolveExpression(context, fqr);				
					String separator = "\\";				
					name = eval.getTypeName();
					if (eval.getTypeName().startsWith(separator)) {
						name = eval.getTypeName().replaceFirst("\\\\", "");
					}					
				}
				
				if (name == null) {
					continue;
				}
				IType[] types = model.findTypes(name, MatchRule.EXACT, 0, 0, scope, new NullProgressMonitor());
				
				if (types.length != 1) {
					continue;
				}
				
				IType type = types[0];
				
				try {
					for (IMethod method : type.getMethods()) {

						boolean implemented = false;
						String methodSignature = PDTModelUtils.getMethodSignature(method);					
						IMethod[] ms = PHPModelUtils.getSuperTypeHierarchyMethod(classType, method.getElementName(), true, null);
						
						
						for (IMethod me : ms) {						
							if (me.getParent().getElementName().equals(fqr.getName())) {
								continue;
							}
							implemented = true;
						}
						
						for (MethodDeclaration typeMethod : classDeclaration.getMethods()) {					
						
							String signature = PDTModelUtils.getMethodSignature(typeMethod, project);						
							if (methodSignature.equals(signature)) {
								implemented = true;
								break;
							}
						}
											
						if (implemented == false) {
							unimplemented.add(method);
						}
					}
				} catch (ModelException e) {
					Logger.debug(e.getMessage());
					e.printStackTrace();
				} catch (CoreException e) {
					Logger.debug(e.getMessage());
				}					
			}				
		}
		
		if (unimplemented.size() > 0) {			
			MissingMethodImplementation missing = new MissingMethodImplementation(classDeclaration, unimplemented);
			getMissing().add(missing);
		}
	}

	public boolean hasMissing() {
		return missingMethods != null && missingMethods.size() > 0;
	}
	
	public List<MissingMethodImplementation> getMissing() {
		return missingMethods;
	}

	public void setMissingInterfaceImplemetations(
			List<MissingMethodImplementation> missingInterfaceImplemetations) {
		this.missingMethods = missingInterfaceImplemetations;
	}
	
	@SuppressWarnings("deprecation")
	public void reportUnimplementedMethods()
	{

		if (buildContext == null) {
			Logger.debug("Unable to report unimplemented methods without buildContext");
			return;
		}
		
		ProblemSeverity severity = ProblemSeverity.WARNING;

		for (MissingMethodImplementation miss : missingMethods) {

			int lineNo = buildContext.getLineTracker().getLineInformationOfOffset(miss.getStart()).getOffset();
			String message = "The type " + miss.getTypeName() + " must implement the inherited method ";

			for (IMethod m : miss.getMisses()) {							
				message += m.getElementName() + ", ";							
			}

			//TODO: how can we check against ints to use the proper constructor here
			// in InterfaceMethodQuickFixProcessor.hasCorrections
//			IProblem problem = new DefaultProblem(message, PEXProblem.INTERFACE_IMPLEMENTATION,
//					new String[0], severity, miss.getStart(), miss.getEnd(),lineNo);

			IProblem problem = new DefaultProblem(message, IPDTProblem.InterfaceRelated,
					new String[0], severity, miss.getStart(), miss.getEnd(),lineNo);

			buildContext.getProblemReporter().reportProblem(problem);

		}
	}
}
