package org.pdtextensions.core.validation.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.pdtextensions.core.util.PDTModelUtils;
import org.pdtextensions.core.validation.IPDTProblem;

/**
 * Checks a PHP sourcemodule for unresolved type references.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class UsageValidator extends PHPASTVisitor {
	final private static String MESSAGE_CANNOT_RESOLVE_TYPE = "The type %s cannot be resolved";
	//final private static String MESSAGE_CANNOT_RESOLVE_CALL = "The call %s cannot be resolved.";
	final private static String MESSAGE_CANNOT_RESOLVE_USE  = "The type %s cannot be resolved or namespace is empty";
	final private static String MESSAGE_DUPLATE_USE 		= "%s is a duplicate";
	
	//protected Map<UseStatement, UseParts> statements;
	protected Map<UsePart, Boolean> parts;
	private ISourceModule source;
	
	List<IProblem> problems;

	public UsageValidator(ISourceModule source) {
		super();
		this.source = source;
		problems = new ArrayList<IProblem>();
		parts = new HashMap<UsePart, Boolean>();
	}

	/**
	 * Adds the usestatement to the internal list of statements
	 * and checks if it can be resolved in the project.
	 * 
	 * @param s
	 */
	public boolean visit(UseStatement s) {
		for (UsePart part : s.getParts()) {
			if (part.getNamespace() == null) {
				continue;
			}
			String sFullName = part.getNamespace().getFullyQualifiedName();
			if (!sFullName.startsWith("\\")) {
				sFullName = "\\" + sFullName;
			}
			for (UsePart existsPart : parts.keySet()) {
				if (existsPart.getNamespace().getFullyQualifiedName().equals(part.getNamespace().getFullyQualifiedName())) {
					reportProblem(
							ProblemSeverity.ERROR, 
							String.format(MESSAGE_DUPLATE_USE, part.getNamespace().getFullyQualifiedName()), 
							IPDTProblem.Duplicate, part.getNamespace().sourceStart(), 
							part.getNamespace().sourceEnd()
				    );
					continue;
				}
			}
			
			boolean isRes = isResolved(sFullName);
			parts.put(part, isRes);
			
			// add problem if namespace is empty
			if (!isRes) {
				IDLTKSearchScope searchScope = SearchEngine.createSearchScope(source.getScriptProject());
				IType[] types = PhpModelAccess.getDefault().findTypes(sFullName.substring(1) + "\\", MatchRule.PREFIX, 0, 0, searchScope, new NullProgressMonitor());
				if (types.length == 0) {
					reportProblem(
						String.format(MESSAGE_CANNOT_RESOLVE_USE, part.getNamespace().getFullyQualifiedName()),
						IPDTProblem.Unresolvable, 
						part.getNamespace().sourceStart(), 
						part.getNamespace().sourceEnd()
					);
				}
			}
		}
		
		return true;
	}
	/*
	@Override
	public boolean visit(TraitUseStatement s) throws Exception {
		for (TypeReference part : s.getTraitList()) {
			if (part instanceof FullyQualifiedReference) {
				FullyQualifiedReference fqr = (FullyQualifiedReference) part;
				
			}
		}
		
		return super.visit(s);
	}*/

	/**
	 * Checks if a FormalParameter can be resolved.
	 * @param s
	 */
	public boolean visit(FormalParameter s) {

		if (s.getParameterType() == null) {
			return true;
		}
		
		if (s.getParameterType() instanceof TypeReference && !(s.getParameterType() instanceof FullyQualifiedReference)) {
			return true;
		}

		
		if (s.getParameterType() instanceof FullyQualifiedReference) {
			
			FullyQualifiedReference fqr = (FullyQualifiedReference) s.getParameterType();
			if (!isResolved(fqr)) {
				reportProblem(
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, s.getParameterType().getName()), 
					IPDTProblem.UsageRelated, 
					s.getParameterType().sourceStart(), 
					s.getParameterType().sourceEnd()
				);
			}
		}
		
		return true;

	}
	
	
	/**
	 * Check if ClassInstanceCreations can be resolved.
	 * @param s
	 */
	public boolean visit(ClassInstanceCreation s) {
		if (s.getClassName() instanceof FullyQualifiedReference) {
			FullyQualifiedReference fqr = (FullyQualifiedReference) s.getClassName();
			if (!isResolved(fqr)) {
				// not revoled, provide an "inject use statement" quickfix
				reportProblem(
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
				    IPDTProblem.UsageRelated, 
				    fqr.sourceStart(), fqr.sourceEnd()
				);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean visit(StaticConstantAccess s) throws Exception {
		
		if (s.getDispatcher() instanceof FullyQualifiedReference) {
			
			FullyQualifiedReference fqr = (FullyQualifiedReference) s.getDispatcher();
			
			if (!"self".equals(fqr.getName()) && !"static".equals(fqr.getName()) && !isResolved(fqr)) {
				reportProblem(
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
					IPDTProblem.UsageRelated, 
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
			
			FullyQualifiedReference fqr = (FullyQualifiedReference) s.getDispatcher();
			
			if ("self".equals(fqr.getName()) || "static".equals(fqr.getName())) {
				return true;
			}
			
			if (!isResolved(fqr)) {
				reportProblem(
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
					IPDTProblem.UsageRelated, 
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
			
			FullyQualifiedReference fqr = (FullyQualifiedReference) s.getReceiver();
			String fqn = fqr.getFullyQualifiedName();
			
			if ("parent".equals(fqn) || "self".equals(fqn) || "static".equals(fqn)) {
				return true;
			}
			
			if (!isResolved(fqr)) {
				reportProblem(
					String.format(MESSAGE_CANNOT_RESOLVE_TYPE, fqr.getFullyQualifiedName()), 
					IPDTProblem.UsageRelated, 
					fqr.sourceStart(), 
					fqr.sourceEnd()
				);
			}
		}
		
		return false;
	}
	
	/**
	 * Warn if resolve
	 * @param fqr
	 * @return
	 */
	private boolean isResolved(FullyQualifiedReference fqr) {
		// ignore builtin
		
		if (PDTModelUtils.isBuiltinType(fqr.getFullyQualifiedName())) {
			return true;
		}
		if (fqr.getFullyQualifiedName().startsWith("\\")) {
			return isResolved(fqr.getFullyQualifiedName());
		}
		
		String check;
		String sFullName = fqr.getFullyQualifiedName();
		IType namespace = PHPModelUtils.getCurrentNamespace(source, fqr.sourceStart());
		
		if (namespace != null) {
			check = "\\" + namespace.getFullyQualifiedName() + "\\" + sFullName;
		} else {
			check = "\\" + sFullName;
		}
		if (isResolved(check)) {
			return true;
		}
		
		
		for (Entry<UsePart, Boolean> entry : parts.entrySet()) {
			
			String useName = entry.getKey().getNamespace().getName();
			String realName = entry.getKey().getNamespace().getFullyQualifiedName();
			boolean useResolved = entry.getValue();
			
			if (entry.getKey().getAlias() != null) {
				useName = entry.getKey().getAlias().getName();
			}
			if (useResolved && sFullName.contains("\\") ) { //namespaced usage
				continue;
			} else if(useResolved && !sFullName.equals(useName)) {
				continue;
			} else if (!useResolved && !sFullName.startsWith(useName + "\\")) {
				continue;
			}
			
			check = (realName.startsWith("\\") ? realName : "\\" + realName) + (sFullName.contains("\\") ? sFullName.substring(useName.length()) :  "");
			
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
		if (!fullyQualifiedReference.startsWith("\\")) {
			
			return false;
		}
		String searchString = fullyQualifiedReference.substring(1);
		IDLTKSearchScope searchScope = SearchEngine.createSearchScope(source.getScriptProject());
		IType[] types = PhpModelAccess.getDefault().findTypes(searchString, MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());
		
		for (IType type : types) {
			if (searchString.equals(type.getFullyQualifiedName("\\"))) {
				return true;
			}
		}
		
		types = PhpModelAccess.getDefault().findTraits(searchString, MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());
		
		for (IType type : types) {
			if (searchString.equals(type.getFullyQualifiedName("\\"))) {
				return true;
			}
		}
		
		//or it is a trait
		
		return false;
	}
	
	protected void reportProblem(String message, int type, int start, int end) {
		reportProblem(ProblemSeverity.WARNING, message, type, start, end);
	}
	
	protected void reportProblem(ProblemSeverity severity, String message, int type, int start, int end) {
		problems.add(new DefaultProblem(message, DefaultProblemIdentifier.decode(type), null, severity, start, end, 0));
	}
	
	public List<IProblem> getProblems() {
		return problems;
	}
}
