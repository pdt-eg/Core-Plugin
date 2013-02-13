package org.pdtextensions.core.validation.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
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
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.core.validation.MissingMethodImplementation;

/**
 * 
 * Checks ClassDeclarations for missing method implementations.
 * 
 * TODO: Currently assumes one class per file. Refactor to handle multiple classes per file.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class ImplementationValidator extends PHPASTVisitor {

	private ISourceModule context;
	private ClassDeclaration classDeclaration;
	private List<MissingMethodImplementation> missingMethods;

	public ImplementationValidator(ISourceModule context) {
		this.context = context;
		missingMethods = new ArrayList<MissingMethodImplementation>();
	}
	
	@Override
	public boolean visit(ClassDeclaration s) throws Exception {
		
		this.classDeclaration = s;
		
		if (getClassDeclaration().isAbstract()) {
			return false;
		}
		
		Collection<TypeReference> interfaces = getClassDeclaration().getInterfaceList();
		
		IScriptProject project = context.getScriptProject();
		List<IMethod> unimplemented = new ArrayList<IMethod>();		
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);		
		PhpModelAccess model = PhpModelAccess.getDefault();		
		IType classType = null;
		
		IType nss = PHPModelUtils.getCurrentNamespace(context, getClassDeclaration().getNameStart());
				
		// namespaced class
		if (nss != null) {			
			IType[] ts = model.findTypes(nss.getElementName(), getClassDeclaration().getName(), MatchRule.EXACT, 0, 0, scope, null);
			if (ts.length != 1) {
				return false;
			}			
			classType = ts[0];
			
		} else {
			IType[] ts = model.findTypes(getClassDeclaration().getName(), MatchRule.EXACT, 0, 0, scope, null);
			if (ts.length != 1) {
				return false;
			}			
			classType = ts[0];			
		}
		Map<String, IMethod> listImported = PDTModelUtils.getImportedMethods(classType);
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
						
						for (MethodDeclaration typeMethod : getClassDeclaration().getMethods()) {					
							String signature = PDTModelUtils.getMethodSignature(typeMethod, project);						
							if (methodSignature.equals(signature)) {
								implemented = true;
								break;
							}
						}
						
						if (!implemented) {
							/*
							 * Trait searching, currently the best method that I found in PDT code //@zulus 
							 * 
							 * TODO Check real method signature (withoutName)
							 */
							for (Entry<String, IMethod> entry : listImported.entrySet()) {
								if (entry.getKey().toLowerCase().equals(method.getElementName().toLowerCase())) {
									implemented = true;
								}
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
			for (TypeDeclaration r : getClassDeclaration().getTypes()) {
				Logger.log(Logger.INFO, r.getName() + " | " + r.getClass().getName());
			}
			MissingMethodImplementation missing = new MissingMethodImplementation(getClassDeclaration(), unimplemented);
			getMissing().add(missing);
		}		
		
		return true;
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

	public ClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}
}
