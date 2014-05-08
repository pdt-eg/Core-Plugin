package org.pdtextensions.semanticanalysis.validation.validator;

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
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
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

	Map<UsePart, Boolean> parts;
	Map<String, Boolean> found;

	public UsageValidator() {
		super();
		parts = new HashMap<UsePart, Boolean>();
		found = new HashMap<String, Boolean>();
	}
	
	@Override
	public void validate(IValidatorContext context) throws Exception {
		super.validate(context);
		parts.clear();
		found.clear();
	}
	
	@Override
	public boolean visit(NamespaceDeclaration s) throws Exception {
		parts = new HashMap<UsePart, Boolean>();
		return super.visit(s);
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
			String sFullName = part.getNamespace().getFullyQualifiedName();
			if (!sFullName.startsWith(BACK_SLASH)) { //$NON-NLS-1$
				sFullName = BACK_SLASH + sFullName; //$NON-NLS-1$
			}
			for (UsePart existsPart : parts.keySet()) {
				if (existsPart.toString().equals(part.toString())) {
					
					context.registerProblem(PEXProblemIdentifier.DUPLICATE, String.format(
									MESSAGE_DUPLATE_USE, part.getNamespace()
											.getFullyQualifiedName()), part
									.getNamespace().sourceStart(), part
									.getNamespace().sourceEnd());

					continue;
				}
			}

			boolean isRes = isResolved(sFullName);
			parts.put(part, isRes);

			// add problem if namespace is empty
			if (!isRes) {
				IDLTKSearchScope searchScope = SearchEngine
						.createSearchScope(context.getProject());
				IType[] types = PhpModelAccess.getDefault().findTypes(
						sFullName.substring(1) + BACK_SLASH, MatchRule.PREFIX, 0, 0, //$NON-NLS-1$
						searchScope, new NullProgressMonitor());
				if (types.length == 0) {
					context.registerProblem(PEXProblemIdentifier.UNRESOVABLE, String.format(MESSAGE_CANNOT_RESOLVE_USE,
							part.getNamespace().getFullyQualifiedName()), part.getNamespace().sourceStart(), part.getNamespace().sourceEnd());
				}
			}
		}

		return true;
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
		
		// check this file
		
		if (fqr.getFullyQualifiedName().startsWith(BACK_SLASH)) {
			return isResolved(fqr.getFullyQualifiedName());
		}

		String check;
		String sFullName = fqr.getFullyQualifiedName();
		IType namespace = PHPModelUtils.getCurrentNamespace(
				context.getSourceModule(), fqr.sourceStart());

		if (namespace != null) {
			check = BACK_SLASH + namespace.getFullyQualifiedName() + BACK_SLASH + sFullName;
		} else {
			check = BACK_SLASH + sFullName;
		}
		if (isResolved(check)) {
			return true;
		}
		for (Entry<UsePart, Boolean> entry : parts.entrySet()) {

			String useName = entry.getKey().getAlias() != null ? entry.getKey().getAlias().getName() :entry.getKey().getNamespace().getName();
			String realName = entry.getKey().getNamespace()
					.getFullyQualifiedName();
			
			if (!sFullName.contains(BACK_SLASH) && !sFullName.equals(useName)) {
				continue;
			} else if (sFullName.contains(BACK_SLASH) && !sFullName.startsWith(useName + BACK_SLASH)) {
				continue;
			}
			StringBuilder builder = new StringBuilder(!realName.startsWith(BACK_SLASH) ? BACK_SLASH : "");
			builder.append(realName);
			builder.append(sFullName.contains(BACK_SLASH) ? sFullName.substring(useName.length()) : "");
			
			check = builder.toString();
			
			if (isResolved(check)) {
				return true;
			}
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

		if (PDTModelUtils.findTypes(context.getProject(), searchString).length > 0) {
			found.put(searchString, true);
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
