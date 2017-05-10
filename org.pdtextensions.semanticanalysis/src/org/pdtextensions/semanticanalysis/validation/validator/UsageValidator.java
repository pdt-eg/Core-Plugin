package org.pdtextensions.semanticanalysis.validation.validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.core.compiler.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.core.compiler.ast.nodes.PHPMethodDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.model.PHPModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.typeinference.PHPSimpleTypes;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.validation.AbstractValidator;
import org.pdtextensions.semanticanalysis.validation.IValidatorContext;

/**
 * Checks a PHP sourcemodule for unresolved type references.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 * @author Dawid zulus Pakula <zulus@w3des.net> 
 */
@SuppressWarnings("restriction")
public class UsageValidator extends AbstractValidator {
	final private static String BACK_SLASH = "\\"; //$NON-NLS-1$
	
	final private static String MESSAGE_CANNOT_RESOLVE_TYPE = "The type %s cannot be resolved";
	
	// final private static String MESSAGE_CANNOT_RESOLVE_CALL =
	// "The call %s cannot be resolved.";
	final private static String MESSAGE_CANNOT_RESOLVE_USE = "The type %s cannot be resolved or namespace is empty";
	final private static String MESSAGE_DUPLATE_USE = "%s is a duplicate";

	final public static String ID = "org.pdtextensions.semanticanalysis.validator.usageValidator"; //$NON-NLS-1$
	
	private class PartInfo {
		public UsePart part;
		//public UseStatement container;
		public String fullName;
		public PartInfo(UseStatement container, UsePart part) {
			this.part = part;
			//this.container = container;
			StringBuilder sb = new StringBuilder(part.getNamespace().getFullyQualifiedName());
			if (container.getNamespace() != null) {
				sb.insert(0, BACK_SLASH);
				sb.insert(0, container.getNamespace().getFullyQualifiedName());
			}
			if (sb.charAt(0) != '\\') {
				sb.insert(0, BACK_SLASH);
			}
			fullName = sb.toString();
		}
		
		public String getUseName() {
			return part.getAlias() != null ? part.getAlias().getName() : part.getNamespace().getName();
		}
	}

	Map<PartInfo, Boolean> parts;
	Map<String, Boolean> found;
	Map<String, Boolean> namespaces;
	String projectName;
	
	IType namespace = null;

	public UsageValidator() {
		super();
		parts = new HashMap<PartInfo, Boolean>();
		found = new HashMap<String, Boolean>();
		namespaces = new HashMap<String, Boolean>();
	}
	
	@Override
	public void validate(IValidatorContext context) throws Exception {
		namespace = PHPModelUtils.getCurrentNamespace(
				context.getSourceModule(), 0);
		super.validate(context);
		parts.clear();
		if (projectName == null || projectName.equals(context.getProject().getElementName())) {
			namespaces.clear();
			found.clear();
			projectName = context.getProject().getElementName();
		}
	}
	
	@Override
	public boolean visit(NamespaceDeclaration s) throws Exception {
		parts = new HashMap<PartInfo, Boolean>();
		namespace = PHPModelUtils.getCurrentNamespace(
				context.getSourceModule(), s.getNameEnd() + 2);
		return super.visit(s);
	}
	
	@Override
	public boolean endvisit(NamespaceDeclaration s) throws Exception {
		boolean res = super.endvisit(s);
		namespace = null;
		return res;
	}

	/**
	 * Adds the usestatement to the internal list of statements and checks if it
	 * can be resolved in the project.
	 * 
	 * @param s
	 */
	public boolean visit(UseStatement s) {
		for (UsePart part : s.getParts()) {
			if (part.getNamespace() == null) {
				continue;
			}
			PartInfo partInfo = new PartInfo(s, part);
			
			
			for (PartInfo existsPart : parts.keySet()) {
				if (existsPart.fullName.equals(partInfo.fullName)) {
					
					context.registerProblem(PEXProblemIdentifier.DUPLICATE, String.format(
									MESSAGE_DUPLATE_USE, part.getNamespace()
											.getFullyQualifiedName()), part
									.getNamespace().sourceStart(), part
									.getNamespace().sourceEnd());

					continue;
				}
			}

			boolean isRes = isResolved(partInfo.fullName);
			parts.put(partInfo, isRes);

			// add problem if namespace is empty
			if (!isRes) {
				IDLTKSearchScope searchScope = SearchEngine
						.createSearchScope(context.getProject());
				IType[] types = PHPModelAccess.getDefault().findTypes(
						partInfo.fullName.substring(1) + BACK_SLASH, MatchRule.PREFIX, 0, 0, //$NON-NLS-1$
						searchScope, new NullProgressMonitor());
				if (types.length == 0) {
					context.registerProblem(PEXProblemIdentifier.UNRESOVABLE, String.format(MESSAGE_CANNOT_RESOLVE_USE,
							partInfo.fullName.substring(1)), part.getNamespace().sourceStart(), part.getNamespace().sourceEnd());
				}
			}
		}

		return true;
	}
	
	@Override
	public boolean visit(PHPMethodDeclaration s) throws Exception {
		if (s.getReturnType() instanceof FullyQualifiedReference) {
			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getReturnType();
			if (!isResolved(fqr)) {
				context.registerProblem(PEXProblemIdentifier.USAGE_RELATED, String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getName()), fqr.sourceStart(), fqr.sourceEnd());
			}
		}
		return super.visit(s);
	}
	/**
	 * Checks if a FormalParameter can be resolved.
	 * 
	 * @param s
	 */
	public boolean visit(FormalParameter s) {

		if (s.getParameterType() == null) {
			return true;
		}

		if (s.getParameterType() instanceof TypeReference
				&& !(s.getParameterType() instanceof FullyQualifiedReference)) {
			return true;
		}

		if (s.getParameterType() instanceof FullyQualifiedReference) {

			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getParameterType();
			if (!isResolved(fqr)) {
				context.registerProblem(PEXProblemIdentifier.USAGE_RELATED, String.format(MESSAGE_CANNOT_RESOLVE_TYPE, s
						.getParameterType().getName()), s.getParameterType().sourceStart(), s.getParameterType().sourceEnd());
			}
		}

		return true;

	}

	/**
	 * Check if ClassInstanceCreations can be resolved.
	 * 
	 * @param s
	 */
	public boolean visit(ClassInstanceCreation s) {
		if (s.getClassName() instanceof FullyQualifiedReference) {
			markIfNotExists((FullyQualifiedReference) s.getClassName());
		}

		return true;
	}
	
	@Override
	public boolean visit(ClassDeclaration s) throws Exception {
		Collection<TypeReference> interfaceList = s.getInterfaceList();
		if (interfaceList == null) {
			return super.visit(s);
		}
		for (TypeReference fqr : s.getInterfaceList()) {
			markIfNotExists(fqr);
		}
		
		markIfNotExists(s.getSuperClass());
		
		return super.visit(s);
	}
	
	@Override
	public boolean visit(InterfaceDeclaration s) throws Exception {
		ASTListNode superClasses = s.getSuperClasses();
		if (superClasses != null) {
			for (Object ob : superClasses.getChilds()) {
				markIfNotExists(ob);
			}
		}
		return super.visit(s);
	}
	
	@Override
	public boolean visit(StaticConstantAccess s) throws Exception {
		if (s.getDispatcher() instanceof FullyQualifiedReference) {

			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getDispatcher();

			if (!"self".equals(fqr.getName()) //$NON-NLS-1$
					&& !"static".equals(fqr.getName())) { //$NON-NLS-1$
				markIfNotExists(fqr);
			}
		}

		return true;
	}

	@Override
	public boolean visit(StaticFieldAccess s) throws Exception {
		if (s.getDispatcher() instanceof FullyQualifiedReference) {
			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getDispatcher();

			if ("self".equals(fqr.getName()) || "static".equals(fqr.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			markIfNotExists(fqr);
		}

		return true;
	}

	@Override
	public boolean visit(StaticMethodInvocation s) throws Exception {
		if (s.getReceiver() instanceof FullyQualifiedReference) {
			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getReceiver();
			String fqn = fqr.getFullyQualifiedName();

			if ("parent".equals(fqn) || "self".equals(fqn)
					|| "static".equals(fqn)) {
				return true;
			}

			markIfNotExists(fqr);
		}

		return false;
	}

	/**
	 * Warn if resolve
	 * 
	 * @param fqr
	 * @return
	 */
	private boolean isResolved(FullyQualifiedReference fqr) {
		// ignore builtin
		if (PDTModelUtils.isBuiltinType(fqr.getFullyQualifiedName())) {
			return true;
		}
		if (PHPSimpleTypes.isHintable(fqr.getFullyQualifiedName(), context.getPHPVersion())) {
			return true;
		}
		
		// check this file
		
		if (fqr.getFullyQualifiedName().startsWith(BACK_SLASH)) {
			return isResolved(fqr.getFullyQualifiedName());
		}

		String check;
		String sFullName = fqr.getFullyQualifiedName();

		for (Entry<PartInfo, Boolean> entry : parts.entrySet()) {

			String useName = entry.getKey().getUseName();
			
			if (!sFullName.contains(BACK_SLASH) && !sFullName.equals(useName)) {
				continue;
			} else if (sFullName.contains(BACK_SLASH) && !sFullName.startsWith(useName + BACK_SLASH)) {
				continue;
			}
			StringBuilder builder = new StringBuilder(entry.getKey().fullName);
			if (sFullName.contains(BACK_SLASH)) {
				builder.append(sFullName.substring(useName.length()));
			}
			
			check = builder.toString();
			
			if (isResolved(check)) {
				return true;
			}
		}
		
		if (namespace != null) {
			check = BACK_SLASH + namespace.getFullyQualifiedName() + BACK_SLASH + sFullName;
		} else {
			check = BACK_SLASH + sFullName;
		}
		if (isResolved(check)) {
			return true;
		}

		return false;
	}

	/**
	 * Found item by absolute address
	 * 
	 * @param fullyQualifiedReference
	 * @return
	 */
	private boolean isResolved(String fullyQualifiedReference) {
		if (!fullyQualifiedReference.startsWith(BACK_SLASH)) {
			return false;
		}
		final String searchString = fullyQualifiedReference.substring(1);
		
		try {
			for (IType t : context.getSourceModule().getAllTypes()) {
				if (t.getFullyQualifiedName(BACK_SLASH).equals(searchString)) {
					return true;
				}
			}
		} catch (ModelException e) {
			PEXAnalysisPlugin.error(e);
		}
		
		if (found.containsKey(searchString)) {
			return found.get(searchString);
		}
		
		if (namespaces.containsKey(searchString)) {
			return namespaces.get(searchString);
		}
		
		if (PDTModelUtils.findTypes(context.getProject(), searchString).length > 0) {
			found.put(searchString, true);
			return true;
		}
		IDLTKSearchScope scope = SearchEngine.createSearchScope(context.getProject());
		if (PHPModelAccess.getDefault().findNamespaces(null, searchString, MatchRule.EXACT, 0, 0, scope, null).length > 0) {
			namespaces.put(searchString, true);
			return true;
		}
		
		if (PHPModelAccess.getDefault().findNamespaces(null, searchString + NamespaceReference.NAMESPACE_SEPARATOR, MatchRule.PREFIX, 0, 0, scope, null).length > 0) {
			namespaces.put(searchString, true);
			return true;
		}
		
		found.put(searchString, false);
		
		return false;
	}
	
	private void markIfNotExists(Object ref) {
		if (ref == null) {
			return;
		} else if (ref instanceof FullyQualifiedReference){
			markIfNotExists((FullyQualifiedReference) ref);
		}
	}
	
	private void markIfNotExists(FullyQualifiedReference fqr){
		if (!isResolved(fqr)) {
			context.registerProblem(
					PEXProblemIdentifier.USAGE_RELATED, 
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
					fqr.sourceStart(), 
					fqr.sourceEnd()
				);
		}
	}
}
