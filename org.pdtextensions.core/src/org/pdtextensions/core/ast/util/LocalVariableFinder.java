package org.pdtextensions.core.ast.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.Variable;

/**
 * A ASTNode finder, which finds every local variable in the specified range in a method body.
 * 
 * A variable is considered to be local iff
 *  1. variable is dollared
 *  2. variable is not a static field (e.g. self::$var)
 *  3. variable is not a field (e.g. $this->var)
 *  4. the variable is not a global var (e.g. superglobals)
 *  5. the variable is not $this.
 * 
 * If no range is specified, then the range is assumed to be the whole php source code.
 * 
 * @author Alex
 *
 */
public final class LocalVariableFinder extends AbstractRangeFinder {
	
	private List<Variable> fVariables = new ArrayList<Variable>();
	
	private ArrayList<FormalParameter> fParameters = new ArrayList<FormalParameter>();
	

	public boolean visit(FormalParameter parameter)
	{
		if(isCovered(parameter)) {
			fParameters.add(parameter);
		}
		
		return true;
	}

	public boolean visit(Variable variable) {
		Expression name = variable.getName();

		if (name.getType() == ASTNode.IDENTIFIER
				&& variable.isDollared()
				// skip self::$var
				&& variable.getParent().getType() != ASTNode.STATIC_FIELD_ACCESS
				// skip $this->var
				&& variable.getParent().getType() != ASTNode.FIELD_ACCESS 
				&& isCovered(variable) && !((Identifier) name).getName().equals("this")) {
			
			IVariableBinding binding = variable.resolveVariableBinding();
			
			if(binding == null || binding.isLocal() && !binding.isGlobal()) {
				fVariables.add(variable);
			}
		}
		
		return true;
	}
	
	public List<Variable> getFoundVariables() {
		return fVariables;
	}
	
	public ArrayList<FormalParameter> getParameters() {
		return fParameters;
	}
}
