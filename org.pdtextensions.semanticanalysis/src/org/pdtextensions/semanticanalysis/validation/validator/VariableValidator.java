package org.pdtextensions.semanticanalysis.validation.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayVariableReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.Assignment;
import org.eclipse.php.internal.core.compiler.ast.nodes.CatchClause;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.Comment;
import org.eclipse.php.internal.core.compiler.ast.nodes.ConditionalExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.ForEachStatement;
import org.eclipse.php.internal.core.compiler.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.compiler.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.compiler.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocBlock;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocTag;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocTagKinds;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPMethodDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPModuleDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPVariableKind;
import org.eclipse.php.internal.core.compiler.ast.nodes.ReferenceExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.ReflectionArrayVariableReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.ReflectionVariableReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.compiler.ast.nodes.StaticStatement;
import org.eclipse.php.internal.core.compiler.ast.nodes.TraitDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.VarComment;
import org.eclipse.php.internal.core.compiler.ast.parser.ASTUtils;
import org.eclipse.php.internal.core.language.PHPVariables;
import org.pdtextensions.internal.semanticanalysis.validation.PEXProblemIdentifier;
import org.pdtextensions.semanticanalysis.validation.AbstractValidator;
import org.pdtextensions.semanticanalysis.validation.IValidatorContext;

/**
 * Check variable
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class VariableValidator extends AbstractValidator {

	final private static String MESSAGE_UNUSED_VARIABLE = "Variable %s is never used";

	final private static String MESSAGE_UNINITIALIZED_VARIABLE = "Variable %s might be not initialized";

	final private static String MESSAGE_UNDEFINED_VARIABLE = "Variable %s is undefined";

	final public static String ID = "org.pdtextensions.semanticanalysis.validator.variableValidator"; //$NON-NLS-1$

	final private static String THIS_VAR = "$this"; //$NON-NLS-1$
	final private static String DOLLAR = "$"; //$NON-NLS-1$
	final private static String AMP = "&"; //$NON-NLS-1$

	private Stack<Scope> scopes = new Stack<VariableValidator.Scope>();
	private Stack<Operation> operations = new Stack<VariableValidator.Operation>();
	private Scope current;
	private int depth = 0;
	private int inClassDecl = -1;

	private List<VarComment> varCommentList;

	private List<PHPDocBlock> docBlocks;

	private enum Operation {
		ASSIGN, USE;
	}

	private class Scope {
		
		public Map<String, Variable> variables = new HashMap<String, VariableValidator.Variable>();
		public int start;
		public int end;

		public Scope(int start, int end) {
			this.start = start;
			this.end = end;
		}
		
		void copy(Scope scope) {
			for (Entry<String, Variable> entry : scope.variables.entrySet()) {
				variables.put(entry.getKey(), new ImportedVariable(entry.getValue()));
			}
		}
		
		boolean contains(String name, int offset) {
			if (variables.containsKey(name)) {
				return true;
			}
			
			for (VarComment varComment : varCommentList) {
				if (varComment.sourceStart() > offset || end < varComment.sourceStart()) {
					continue;
				}
				if (varComment.getVariableReference().getName()
						.equals(name)) {
					this.variables.put(name, new DocVariable(varComment));
					return true;
				}
			}

			
			for (PHPDocBlock block : docBlocks) {
				if (block.sourceStart() > offset
						|| end < block
								.sourceStart()) {
					continue;
				}

				for (PHPDocTag tag : block.getTags(PHPDocTagKinds.VAR)) {
					if (tag.isValidVarTag()
							&& tag.getVariableReference() != null
							&& tag.getVariableReference().getName()
									.equals(name)) {
						this.variables.put(name, new DocVariable(block));
						return true;
					}
				}
			}
			
			return false;
		}
	}

	private class Variable {
		public int start;
		public int end;
		private int initialized = -1;
		private int used = -1;
		public TreeMap<Integer, Integer> other = new TreeMap<Integer, Integer>();

		Variable() {
		}

		Variable(ASTNode node) {
			start = node.start();
			end = node.end();
		}
		
		Variable(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int initialized() {
			return initialized;
		}

		public void setInitialized(int init) {
			initialized = init;
		}

		public int used() {
			return used;
		}

		public void setUsed(int used) {
			this.used = used;
		}

		public void addAddress(ASTNode node) {
			other.put(node.start(), node.end());
		}
		
		public void addAddress(int start, int end) {
			other.put(start, end);
		}
	}
	
	private class DocVariable extends Variable {
		public DocVariable(Comment varComment) {
			super(varComment.sourceStart(), varComment.sourceEnd());
			setInitialized(start);
		}
		
	}

	private class ImportedVariable extends Variable {
		public Variable parent;

		ImportedVariable(Variable variable) {
			parent = variable;
			start = variable.start;
			end = variable.end;
		}

		ImportedVariable(ASTNode node) {
			parent = new Variable(node);
			start = parent.start;
			end = parent.end;
		}

		@Override
		public int used() {
			return parent.used();
		}

		@Override
		public void setUsed(int used) {
			parent.setUsed(used);
		}

		@Override
		public int initialized() {
			return parent.initialized();
		}

		@Override
		public void setInitialized(int init) {
			parent.setInitialized(init);
		}

		@Override
		public void addAddress(ASTNode node) {
			parent.addAddress(node);
		}

	}

	public VariableValidator() {
		super();
	}

	@Override
	public void validate(IValidatorContext context) throws Exception {
		pushScope(0, context.getSourceModule().getSourceRange().getLength());
		PHPModuleDeclaration phpModule = (PHPModuleDeclaration) context.getModuleDeclaration();
		List<VarComment> varComments = phpModule.getVarComments();
		varCommentList = new ArrayList<VarComment>(phpModule
				.getVarComments().size());
		varCommentList.addAll(varComments);
		Collections.sort(varCommentList, new Comparator<VarComment>() {

			public int compare(VarComment o1, VarComment o2) {
				return o2.sourceStart() - o1.sourceStart();
			}
		});
		
		docBlocks = new ArrayList<PHPDocBlock>(
				phpModule.getPhpDocBlocks().size());
		docBlocks.addAll(phpModule.getPhpDocBlocks());
		Collections.sort(docBlocks, new Comparator<PHPDocBlock>() {

			@Override
			public int compare(PHPDocBlock o1, PHPDocBlock o2) {
				return o1.sourceStart() - o1.sourceStart();
			}
		});
		
		super.validate(context);
		popScope();
	}

	@Override
	public boolean visit(PHPMethodDeclaration node) throws Exception {
		pushScope(node.sourceStart(), node.sourceEnd());
		if (node.isAbstract() || node.getBody() == null) {
			popScope();
			return false;
		}
		for (Object o : node.getArguments()) {
			if (o instanceof FormalParameter) {
				VariableReference parameterName = ((FormalParameter) o).getParameterName();
				Variable v = new ImportedVariable(parameterName);
				v.setInitialized(((FormalParameter) o).start());
				current.variables.put(parameterName.getName(), v);
			}
		}
		if (inClassDecl + 1 == depth && !node.isStatic()) {
			Variable v = new ImportedVariable(node);
			v.setInitialized(node.start());
			current.variables.put(THIS_VAR, v);
		}
		if (node.getBody() != null) {
			node.getBody().traverse(this);
		}
		popScope();
		return false;
	}

	@Override
	public boolean visit(LambdaFunctionDeclaration decl) throws Exception {
		Scope prev = current;
		pushScope(decl.sourceStart(), decl.sourceEnd());
		for (Object o : decl.getArguments()) {
			if (o instanceof FormalParameter) {
				VariableReference parameterName = ((FormalParameter) o).getParameterName();
				Variable v = new ImportedVariable(parameterName);
				v.setInitialized(((FormalParameter) o).start());
				current.variables.put(parameterName.getName(), v);
			}
		}
		if (decl.getLexicalVars() != null) {
			for (Expression var : decl.getLexicalVars()) {
				if (var instanceof ReferenceExpression) {
					var = ((ReferenceExpression) var).getVariable();
				}
				if (var instanceof VariableReference) {
					final String name = ((VariableReference) var).getName();
					if (prev.contains(name, var.sourceStart())) {
						current.variables.put(name, new ImportedVariable(prev.variables.get(name)));
					} else {
						context.registerProblem(PEXProblemIdentifier.UNDEFINED_VARIABLE, String.format(MESSAGE_UNDEFINED_VARIABLE, name), var.start(),
								var.end());
					}
				}
			}
		}
		if (inClassDecl < depth && prev.contains(THIS_VAR, 0)) { //$NON-NLS-1$
			current.variables.put(THIS_VAR, prev.variables.get(THIS_VAR)); //$NON-NLS-1$
		}
		decl.getBody().traverse(this);

		popScope();

		return false;
	}

	@Override
	public boolean visit(PHPCallExpression ex) throws Exception {
		operations.push(Operation.USE);

		if (ex.getReceiver() == null && ex.getCallName() != null && ex.getCallName().getName().equals("compact") && ex.getArgs() != null) {
			for (ASTNode node : ex.getArgs().getChilds()) {
				if (node instanceof Scalar && ((Scalar) node).getScalarType() == Scalar.TYPE_STRING) {
					String name = DOLLAR + ASTUtils.stripQuotes(((Scalar) node).getValue());
					if (current.contains(name, node.sourceStart())) {
						current.variables.get(name).setUsed(node.start());
					}
				}
			}
		}

		return super.visit(ex);
	}
	

	@Override
	public boolean endvisit(PHPCallExpression ex) throws Exception {
		operations.pop();

		return super.endvisit(ex);
	}

	private void endScope(Scope scope) {
		for (Entry<String, Variable> entry : scope.variables.entrySet()) {
			Variable value = entry.getValue();
			if (value.used() < 0 && !(value instanceof ImportedVariable) && !(value instanceof DocVariable)) {
				String mess = String.format(MESSAGE_UNUSED_VARIABLE, entry.getKey());
				context.registerProblem(PEXProblemIdentifier.UNUSED_VARIABLE, mess, value.start, value.end);
				for (Entry<Integer, Integer> a : value.other.entrySet()) {
					context.registerProblem(PEXProblemIdentifier.UNUSED_VARIABLE, mess, a.getKey(), a.getValue());
				}
			}
		}
	}

	public boolean visit(NamespaceDeclaration s) throws Exception {
		pushScope(s.sourceStart(), s.sourceEnd());
		return super.visit(s);
	}

	public boolean endvisit(NamespaceDeclaration s) throws Exception {
		popScope();
		return super.visit(s);
	}

	public boolean visit(ForEachStatement s) throws Exception {
		if (s.getExpression() != null) {
			s.getExpression().traverse(this);
		}
		operations.push(Operation.ASSIGN);
		if (s.getKey() != null) {
			s.getKey().traverse(this);
		}
		if (s.getValue() != null) {
			s.getValue().traverse(this);
		}
		operations.pop();

		if (s.getStatement() != null) {
			s.getStatement().traverse(this);
		}

		return false;
	}
	
	private String getName(String name) {
		if (name.startsWith(AMP)) {
			name = name.substring(1);
		}
		
		return name;
	}

	@Override
	public boolean visit(VariableReference s) throws Exception {
		String name = getName(s.getName());
		if (!name.startsWith(DOLLAR) || isSuperGlobal(name)) {
			return false;
		}
		if (s.getVariableKind() == PHPVariableKind.GLOBAL && isGlobal(name)) {
			return false;
		} else if (s.getVariableKind() != PHPVariableKind.LOCAL && s.getVariableKind() != PHPVariableKind.GLOBAL && s.getVariableKind() != PHPVariableKind.UNKNOWN) {
			return false;
		}
		
		check(name, s.start(), s.end());
		
		return super.visit(s);
	}
	
	@Override
	public boolean visit(StaticStatement s) throws Exception {
		operations.push(Operation.ASSIGN);
		for (Expression expr : s.getExpressions()) {
			expr.traverse(this);
		}
		operations.pop();
		return false;
	}
	
	protected void check(String name, int start, int end) {
		current.contains(name, start);
		Variable var = current.variables.get(name);

		if (isInit()) {
			if (var == null) {
				var = new Variable(start, end);
				current.variables.put(name, var);
			} else {
				var.addAddress(start, end);
			}
			if (var.initialized() < 0) {
				var.setInitialized(start);
			}
		}
		if (var == null) {
			context.registerProblem(PEXProblemIdentifier.UNDEFINED_VARIABLE, String.format(MESSAGE_UNDEFINED_VARIABLE, name), start, end);
		} else if (!isInit()) {
			var.setUsed(start);
		}
	}

	@Override
	public boolean visit(ArrayVariableReference s) throws Exception {
		String name = getName(s.getName());
		if (!name.startsWith(DOLLAR) || isSuperGlobal(name) || (s.getVariableKind() == PHPVariableKind.GLOBAL && isGlobal(name))) {
			if (s.getIndex() != null) {
				operations.push(Operation.USE);
				s.getIndex().traverse(this);
				operations.pop();
			}
			return false;
		}
		Variable var = current.variables.get(name);
		if (var == null) {
			context.registerProblem(PEXProblemIdentifier.UNDEFINED_VARIABLE, String.format(MESSAGE_UNDEFINED_VARIABLE, name), s.start(), s.start()
					+ s.getName().length());
		} else if (!isInit()) {
			var.setUsed(s.start());
		}
		if (s.getIndex() != null) {
			operations.push(Operation.USE);
			s.getIndex().traverse(this);
			operations.pop();
		}

		return false;
	}

	public boolean visit(ReflectionArrayVariableReference s) throws Exception {
		if (s.getExpression() instanceof Scalar) {
			Scalar name = (Scalar) s.getExpression();
			if (name.getScalarType() == Scalar.TYPE_STRING) {
				String dolarName = DOLLAR + name.getValue();
				check(dolarName, name.start(), name.end());
			}
		}
		operations.push(Operation.USE); 

		return super.visit(s);
	}

	public boolean endvisit(ReflectionArrayVariableReference s) throws Exception {
		operations.pop();

		return super.endvisit(s);
	}

	public boolean visit(ReflectionVariableReference s) throws Exception {
		if (s.getExpression() instanceof Scalar) {
			Scalar name = (Scalar) s.getExpression();
			if (name.getScalarType() == Scalar.TYPE_STRING) {
				String dolarName = DOLLAR + name.getValue();
				check(dolarName, name.start(), name.end());
			}
		}
		operations.push(Operation.USE);

		return super.visit(s);
	}

	public boolean endvisit(ReflectionVariableReference s) throws Exception {
		operations.pop();
		return super.endvisit(s);
	}

	@Override
	public boolean visit(FieldAccess s) throws Exception {
		if (s.getDispatcher() != null) {
			operations.push(Operation.USE);
			s.getDispatcher().traverse(this);
			operations.pop();
		}
		if (s.getField() != null) {
			operations.push(Operation.USE);
			s.getField().traverse(this);
			operations.pop();
		}

		return false;
	}

	@Override
	public boolean visit(StaticFieldAccess s) throws Exception {
		if (s.getDispatcher() != null) {
			operations.push(Operation.USE);
			s.getDispatcher().traverse(this);
			operations.pop();
		}
		if (s.getField() != null) {
			if (s.getField() instanceof ArrayVariableReference) {
				ArrayVariableReference ref = (ArrayVariableReference) s.getField();
				if (ref.getIndex() != null) {
					operations.push(Operation.USE);
					ref.getIndex().traverse(this);
					operations.pop();
				}
			} else if (!(s.getField() instanceof VariableReference)) {
				s.getField().traverse(this);
			}
		}
		return false;
	}

	public boolean visit(CatchClause s) throws Exception {
		Scope prev = current;
		pushScope(s.sourceStart(), s.sourceEnd());
		current.copy(prev);
		try {
			if (s.getVariable() != null) {
				Variable v = new Variable(s.getVariable());
				v.setInitialized(s.start());
				v.setUsed(s.start());
				current.variables.put(s.getVariable().getName(), v);
			}
			if (s.getStatement() != null) {
				s.getStatement().traverse(this);
			}
		} finally {
			popScope();
		}

		return false;
	}

	public boolean visit(Assignment s) throws Exception {
		operations.push(Operation.USE);
		s.getValue().traverse(this);
		operations.pop();
		operations.push(Operation.ASSIGN);
		s.getVariable().traverse(this);
		operations.pop();
		return false;
	};

	private boolean isInit() {
		return !operations.isEmpty() && operations.lastElement() == Operation.ASSIGN;
	}

	@Override
	/**
	 * TODO Allow mark Variable as global
	 */
	public boolean visit(GlobalStatement s) throws Exception {
		for (Expression el : s.getVariables()) {
			if (el instanceof VariableReference) {
				VariableReference ref = (VariableReference) el;
				if (scopes.size() > 1) {
					Scope parentScope = scopes.get(scopes.size() - 2);
					if (parentScope.contains(ref.getName(), ref.sourceStart())) {
						current.variables.put(ref.getName(), new ImportedVariable(parentScope.variables.get(ref.getName())));
						continue;
					}
				}
				Variable var = current.variables.get(ref.getName());
				if (var == null) {
					var = new Variable(ref);
					current.variables.put(ref.getName(), var);
				}
				if (var.initialized < 0) {
					var.initialized = ref.start();
				}
			} else {
				operations.push(Operation.ASSIGN);
				el.traverse(this);
				operations.pop();
			}
		}
		return false;
	}

	@Override
	public boolean visit(ClassDeclaration s) throws Exception {
		if (s.isInterface()) {
			return false;
		}
		inClassDecl = depth;

		return super.visit(s);
	}

	@Override
	public boolean endvisit(ClassDeclaration s) throws Exception {
		inClassDecl = -1;

		return super.endvisit(s);
	}

	@Override
	public boolean visit(TraitDeclaration s) throws Exception {
		inClassDecl = depth;

		return super.visit(s);
	}

	@Override
	public boolean endvisit(TraitDeclaration s) throws Exception {
		inClassDecl = -1;

		return super.endvisit(s);
	}

	@Override
	public boolean visit(InfixExpression s) throws Exception {
		operations.push(Operation.USE);
		return super.visit(s);
	}

	@Override
	public boolean endvisit(InfixExpression s) throws Exception {
		operations.pop();
		return super.endvisit(s);
	}

	@Override
	public boolean visit(ConditionalExpression s) throws Exception {
		operations.push(Operation.USE);

		return super.visit(s);
	}

	@Override
	public boolean endvisit(ConditionalExpression s) throws Exception {
		operations.pop();

		return super.endvisit(s);
	}

	private Scope pushScope(int start, int end) {
		current = new Scope(start, end);
		scopes.push(current);
		depth++;
		return scopes.lastElement();
	}

	private Scope popScope() {
		Scope tmp = scopes.pop();
		endScope(tmp);
		if (!scopes.isEmpty()) {
			current = scopes.lastElement();
		} else {
			current = null;
		}
		depth--;
		return tmp;
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean isSuperGlobal(String name) {
		if ("$GLOBALS".equals(name)) { //$NON-NLS-1$
			return true;
		}
		if (!name.startsWith("$_")) { //$NON-NLS-1$
			return false;
		}

		return isGlobal(name);
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean isGlobal(String name) {
		String[] globals = PHPVariables.getVariables(context.getPHPVersion());
		for (String global : globals) {
			if (global.equals(name)) {
				return true;
			}
			context.equals(null);

		}
		return false;
	}
}
