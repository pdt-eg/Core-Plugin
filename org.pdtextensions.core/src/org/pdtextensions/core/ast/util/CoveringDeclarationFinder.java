package org.pdtextensions.core.ast.util;

import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;

@SuppressWarnings("restriction")
/**
 * TODO: create another abstract super class, which stops visiting if we found 
 * the desired ASTNode
 * 
 * @author Alex
 *
 */
public class CoveringDeclarationFinder extends AbstractRangeFinder {

	private ClassDeclaration fCoveringClassDeclaration;
	
	private MethodDeclaration fCoveringMethodDeclaration;
	
	private NamespaceDeclaration fCoveringNamespaceDeclaration;
	
	private FunctionDeclaration fCoveringFunctionDeclaration;
	
	public boolean visit(ClassDeclaration declaration)
	{
		if(covers(declaration)) {
			fCoveringClassDeclaration = declaration;
			return true;
		}
		
		// php does not support classes in classes
		return false;
	}
	
	public boolean visit(MethodDeclaration declaration)
	{
		if(covers(declaration)) {
			fCoveringMethodDeclaration = declaration;
			return true;
		}
		
		// php does not support methods in methods
		return false;
	}
	
	public boolean visit(NamespaceDeclaration declaration)
	{
		if(covers(declaration)) {
			fCoveringNamespaceDeclaration = declaration;
			return true;
		}
		
		// php does not support namespaces in namepsaces
		return false;
	}
	
	public boolean visit(FunctionDeclaration declaration)
	{
		if(covers(declaration)) {
			fCoveringFunctionDeclaration = declaration;
			return true;
		}
		
		// php does not support function declarations in functions
		return false;
	}
	
	
	public ClassDeclaration getCoveringClassDeclaration()
	{
		return fCoveringClassDeclaration;
	}
	
	public MethodDeclaration getCoveringMethodDeclaration()
	{
		return fCoveringMethodDeclaration;
	}

	public NamespaceDeclaration getCoveringNamespaceDeclaration() {
		return fCoveringNamespaceDeclaration;
	}

	public FunctionDeclaration getCoveringFunctionDeclaration() {
		return fCoveringFunctionDeclaration;
	}
	
}
