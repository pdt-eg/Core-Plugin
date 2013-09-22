package org.pdtextensions.semanticanalysis.validation.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.semanticanalysis.validation.AbstractValidator;
import org.pdtextensions.semanticanalysis.validation.Problem;

/**
 * Checks a PHP sourcemodule for unresolved type references.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class UsageValidator extends AbstractValidator {
	final private static String BACK_SLASH = "\\"; //$NON-NLS-1$
	
	final private static String MESSAGE_CANNOT_RESOLVE_TYPE = "The type %s cannot be resolved";
	
	// final private static String MESSAGE_CANNOT_RESOLVE_CALL =
	// "The call %s cannot be resolved.";
	final private static String MESSAGE_CANNOT_RESOLVE_USE = "The type %s cannot be resolved or namespace is empty";
	final private static String MESSAGE_DUPLATE_USE = "%s is a duplicate";

	final public static String SUB_DUPLICATE = "duplicate"; //$NON-NLS-1$
	final public static String SUB_UNRESOVABLE = "use"; //$NON-NLS-1$

	// protected Map<UseStatement, UseParts> statements;
	Map<UsePart, Boolean> parts;

	public UsageValidator() {
		super();
		parts = new HashMap<UsePart, Boolean>();
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
					
					context.registerProblem(SUB_DUPLICATE,
							Problem.CAT_IMPORT, String.format(
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
					context.registerProblem(SUB_UNRESOVABLE, Problem.CAT_IMPORT, String.format(MESSAGE_CANNOT_RESOLVE_USE,
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
				context.registerProblem(SUB_UNRESOVABLE, Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, String.format(MESSAGE_CANNOT_RESOLVE_TYPE, s
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
			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getClassName();
			if (!isResolved(fqr)) {
				// not revoled, provide an "inject use statement" quickfix

				context.registerProblem(
						SUB_UNRESOVABLE, 
						Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, 
						String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
						fqr.sourceStart(), 
						fqr.sourceEnd()
					);
			}
		}

		return true;
	}

	@Override
	public boolean visit(StaticConstantAccess s) throws Exception {

		if (s.getDispatcher() instanceof FullyQualifiedReference) {

			FullyQualifiedReference fqr = (FullyQualifiedReference) s
					.getDispatcher();

			if (!"self".equals(fqr.getName()) //$NON-NLS-1$
					&& !"static".equals(fqr.getName()) && !isResolved(fqr)) { //$NON-NLS-1$
				context.registerProblem(
						SUB_UNRESOVABLE, 
						Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, 
						String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
						fqr.sourceStart(), 
						fqr.sourceEnd()
					);
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

			if (!isResolved(fqr)) {
				context.registerProblem(
						SUB_UNRESOVABLE, 
						Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, 
						String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
						fqr.sourceStart(), 
						fqr.sourceEnd()
					);
			}

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

			if (!isResolved(fqr)) {
				context.registerProblem(
						SUB_UNRESOVABLE, 
						Problem.CAT_POTENTIAL_PROGRAMMING_PROBLEM, 
						String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
						fqr.sourceStart(), 
						fqr.sourceEnd()
					);
			}
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

			String useName = entry.getKey().getNamespace().getName();
			String realName = entry.getKey().getNamespace()
					.getFullyQualifiedName();
			boolean useResolved = entry.getValue();

			if (entry.getKey().getAlias() != null) {
				useName = entry.getKey().getAlias().getName();
			}
			if (useResolved && sFullName.contains(BACK_SLASH)) { // namespaced usage
				continue;
			} else if (useResolved && !sFullName.equals(useName)) {
				continue;
			} else if (!useResolved && !sFullName.startsWith(useName + BACK_SLASH)) {
				continue;
			}

			check = (realName.startsWith(BACK_SLASH) ? realName : BACK_SLASH + realName)
					+ (sFullName.contains(BACK_SLASH) ? sFullName.substring(useName
							.length()) : "");

			if (isResolved(check)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Found item by absolute address
	 * 
	 * @todo Find bitmask for traits
	 * @param fullyQualifiedReference
	 * @return
	 */
	private boolean isResolved(String fullyQualifiedReference) {
		if (!fullyQualifiedReference.startsWith(BACK_SLASH)) {

			return false;
		}
		String searchString = fullyQualifiedReference.substring(1);
		IDLTKSearchScope searchScope = SearchEngine.createSearchScope(context
				.getProject());
		IType[] types = PhpModelAccess.getDefault().findTypes(searchString,
				MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());

		for (IType type : types) {
			if (searchString.equals(type.getFullyQualifiedName(BACK_SLASH))) {
				return true;
			}
		}

		types = PhpModelAccess.getDefault().findTraits(searchString,
				MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());

		for (IType type : types) {
			if (searchString.equals(type.getFullyQualifiedName(BACK_SLASH))) {
				return true;
			}
		}

		return false;
	}
}
