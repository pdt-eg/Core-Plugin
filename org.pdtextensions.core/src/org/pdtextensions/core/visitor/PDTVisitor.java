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
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.pdtextensions.core.compiler.MissingMethodImplementation;
import org.pdtextensions.core.util.PDTModelUtils;


@SuppressWarnings("restriction")
public class PDTVisitor extends PHPASTVisitor {

	private final ISourceModule context;	
	private List<MissingMethodImplementation> missingInterfaceImplemetations = new ArrayList<MissingMethodImplementation>();	
	private int nameStart;
	private int nameEnd;

	public PDTVisitor(ISourceModule sourceModule) {

		this.context = sourceModule;
	}
	
	public List<MissingMethodImplementation> getUnimplementedMethods() {
		
		return missingInterfaceImplemetations;
	}
	
	public boolean endvisit(ClassDeclaration s) throws Exception {

		nameStart = s.getNameStart();
		nameEnd = s.getNameEnd();
		checkMethodImplementations(s);
		return super.endvisit(s);
		
	}
	
	protected void checkMethodImplementations(ClassDeclaration s) {

		if (s.isAbstract()) {
			return;
		}
		
		Collection<TypeReference> interfaces = s.getInterfaceList();
		IScriptProject project = context.getScriptProject();
		List<IMethod> unimplemented = new ArrayList<IMethod>();		
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);		
		PhpModelAccess model = PhpModelAccess.getDefault();		
		IType classType = null;
		IType nss = PHPModelUtils.getCurrentNamespace(context, nameStart);
				
		// namespaced class
		if (nss != null) {			
			IType[] ts = model.findTypes(nss.getElementName(), s.getName(), MatchRule.EXACT, 0, 0, scope, null);
			if (ts.length != 1) {
				return;
			}			
			classType = ts[0];
			
		} else {
			IType[] ts = model.findTypes(s.getName(), MatchRule.EXACT, 0, 0, scope, null);
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
						
						for (MethodDeclaration typeMethod : s.getMethods()) {					
						
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
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}					
			}				
		}
		
		if (unimplemented.size() > 0) {			
			MissingMethodImplementation missing = new MissingMethodImplementation(s, unimplemented);
			missingInterfaceImplemetations.add(missing);
		}
	}

	public int getNameEnd() {
		return nameEnd;
	}

	public int getNameStart() {
		return nameStart;
	}
}
