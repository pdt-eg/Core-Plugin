package org.pdtextensions.semanticanalysis.validation.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.validation.AbstractValidator;
import org.pdtextensions.semanticanalysis.validation.IValidatorContext;
import org.pdtextensions.semanticanalysis.validation.MissingMethodImplementation;
import org.pdtextensions.semanticanalysis.validation.Problem;

/**
 * 
 * Checks ClassDeclarations for missing method implementations.
 * 
 * TODO: Currently assumes one class per file. Refactor to handle multiple classes per file.
 * TODO: Optimisation
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class ImplementationValidator extends AbstractValidator{
	public static final String ID = "org.pdtextensions.semanticanalysis.validator.implementationValidator"; //$NON-NLS-1$
	private static final String BACK_SLASH = "\\"; //$NON-NLS-1$
	private IScriptProject project;
	private ISourceModule sourceModule;
	
	private ClassDeclaration classDeclaration;
	private List<MissingMethodImplementation> missingMethods;
	
	public ImplementationValidator() {
	}
	
	public ImplementationValidator(ISourceModule module) {
		this.sourceModule = module;
		this.project = module.getScriptProject();
	}
	
	@Override
	public boolean visit(ClassDeclaration s) throws Exception {
		this.classDeclaration = s;
		missingMethods = new ArrayList<MissingMethodImplementation>();
		
		if (getClassDeclaration().isAbstract()) {
			return false;
		}
		
		Collection<TypeReference> interfaces = getClassDeclaration().getInterfaceList();
		
		List<IMethod> unimplemented = new ArrayList<IMethod>();		
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);		
		PhpModelAccess model = PhpModelAccess.getDefault();		
		IType nss = PHPModelUtils.getCurrentNamespace(sourceModule, getClassDeclaration().getNameStart());
		String search = nss != null ? nss.getElementName() + BACK_SLASH + getClassDeclaration().getName() : getClassDeclaration().getName();
		
		IType classType = context != null ? PDTModelUtils.findType(sourceModule, search) : null;
				
		if (classType == null) {
			for (IType t : PDTModelUtils.findTypes(project, search)) {
				classType = t;
				break;
			}
			if (classType == null) {
				return false;
			}
		}
		
		Map<String, IMethod> listImported = PDTModelUtils.getImportedMethods(classType);
		// iterate over all interfaces and check if the current class
		// or any of the superclasses implements the method
		if (listImported == null) {
			return true;
		}
		for (TypeReference interf : interfaces) {
			
			if (interf instanceof FullyQualifiedReference) {
				
				FullyQualifiedReference fqr = (FullyQualifiedReference) interf;
				String name = null;
				// we have a namespace
				if (fqr.getNamespace() != null) {
					name = fqr.getNamespace().getName() + BACK_SLASH + fqr.getName();
				} else {
					IEvaluatedType eval = PHPTypeInferenceUtils.resolveExpression(sourceModule, fqr);
					name = eval.getTypeName();
					if (eval.getTypeName().startsWith(BACK_SLASH)) {
						name = eval.getTypeName().replaceFirst("\\\\", "");
					}					
				}
				
				if (name == null) {
					continue;
				}
				IType[] types;
				if (PDTModelUtils.findType(sourceModule, name) != null) {
					types = new IType[] {PDTModelUtils.findType(sourceModule, name)};
				} else {
					types = model.findTypes(name, MatchRule.EXACT, 0, 0, scope, new NullProgressMonitor());
				}
				if (types.length != 1) {
					continue;
				}
				
				IType type = types[0];
				
				try {
					for (IMethod method : type.getMethods()) {

						boolean implemented = false;
						String methodSignature = PDTModelUtils.getMethodSignature(method);
						if (methodSignature == null) {
							continue;
						}
						IMethod[] ms = PDTModelUtils.getSuperTypeHierarchyMethod(classType, method.getElementName(), true, null);
						
						for (IMethod me : ms) {
							if (me.getParent().getElementName().equals(fqr.getName())) {
								continue;
							}
							if (!PHPFlags.isAbstract(me.getFlags())) {
								implemented = true;
							}
						}

						for (MethodDeclaration typeMethod : getClassDeclaration().getMethods()) {
							String signature = PDTModelUtils.getMethodSignature(typeMethod, project);
							if (methodSignature.equals(signature) && !typeMethod.isAbstract()) {
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
								if (entry.getKey().toLowerCase().equals(method.getElementName().toLowerCase()) && !PHPFlags.isAbstract(entry.getValue().getFlags())) {
									implemented = true;
									break;
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
	
	@Override
	public void validate(IValidatorContext context) throws Exception {
		project = context.getProject();
		sourceModule = context.getSourceModule();
		super.validate(context);
		if (hasMissing() && getClassDeclaration() != null) {
			int start = getClassDeclaration().getNameStart();
			int stop = getClassDeclaration().getNameEnd();

			String message = "The class " + getClassDeclaration().getName() + " must implement the inherited abstract method "
			+ getMissing().get(0).getFirstMethodName();
			context.registerProblem(PEXProblemIdentifier.INTERFACE_RELATED, Problem.CAT_RESTRICTION, message, start, stop);
		}
	}
}
