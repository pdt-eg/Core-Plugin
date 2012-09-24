package org.pdtextensions.core.ui.ast;

import org.eclipse.php.internal.core.ast.nodes.ASTError;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ArrayAccess;
import org.eclipse.php.internal.core.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.BackTickExpression;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.BreakStatement;
import org.eclipse.php.internal.core.ast.nodes.CastExpression;
import org.eclipse.php.internal.core.ast.nodes.CatchClause;
import org.eclipse.php.internal.core.ast.nodes.ChainingInstanceCall;
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.ast.nodes.ClassName;
import org.eclipse.php.internal.core.ast.nodes.CloneExpression;
import org.eclipse.php.internal.core.ast.nodes.Comment;
import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;
import org.eclipse.php.internal.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ContinueStatement;
import org.eclipse.php.internal.core.ast.nodes.DeclareStatement;
import org.eclipse.php.internal.core.ast.nodes.DereferenceNode;
import org.eclipse.php.internal.core.ast.nodes.DoStatement;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FullyQualifiedTraitMethodReference;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.FunctionName;
import org.eclipse.php.internal.core.ast.nodes.GlobalStatement;
import org.eclipse.php.internal.core.ast.nodes.GotoLabel;
import org.eclipse.php.internal.core.ast.nodes.GotoStatement;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.IfStatement;
import org.eclipse.php.internal.core.ast.nodes.IgnoreError;
import org.eclipse.php.internal.core.ast.nodes.InLineHtml;
import org.eclipse.php.internal.core.ast.nodes.Include;
import org.eclipse.php.internal.core.ast.nodes.InfixExpression;
import org.eclipse.php.internal.core.ast.nodes.InstanceOfExpression;
import org.eclipse.php.internal.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ListVariable;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.ast.nodes.NamespaceName;
import org.eclipse.php.internal.core.ast.nodes.PHPArrayDereferenceList;
import org.eclipse.php.internal.core.ast.nodes.ParenthesisExpression;
import org.eclipse.php.internal.core.ast.nodes.PostfixExpression;
import org.eclipse.php.internal.core.ast.nodes.PrefixExpression;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Quote;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.ReflectionVariable;
import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.internal.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.internal.core.ast.nodes.StaticFieldAccess;
import org.eclipse.php.internal.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.StaticStatement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;
import org.eclipse.php.internal.core.ast.nodes.SwitchStatement;
import org.eclipse.php.internal.core.ast.nodes.ThrowStatement;
import org.eclipse.php.internal.core.ast.nodes.TraitAlias;
import org.eclipse.php.internal.core.ast.nodes.TraitAliasStatement;
import org.eclipse.php.internal.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.internal.core.ast.nodes.TraitPrecedence;
import org.eclipse.php.internal.core.ast.nodes.TraitPrecedenceStatement;
import org.eclipse.php.internal.core.ast.nodes.TraitUseStatement;
import org.eclipse.php.internal.core.ast.nodes.TryStatement;
import org.eclipse.php.internal.core.ast.nodes.UnaryOperation;
import org.eclipse.php.internal.core.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.ast.nodes.UseStatementPart;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.nodes.WhileStatement;
import org.eclipse.php.internal.core.ast.visitor.Visitor;

@SuppressWarnings("restriction")
public class RunThroughVisitor implements Visitor {

	public boolean visit(ArrayAccess arrayAccess) {
		arrayAccess.childrenAccept(this);
		return false;
	}

	public boolean visit(ArrayCreation arrayCreation) {
		arrayCreation.childrenAccept(this);
		return false;
	}

	public boolean visit(ArrayElement arrayElement) {
		arrayElement.childrenAccept(this);
		return false;
	}

	public boolean visit(Assignment assignment) {
		assignment.childrenAccept(this);
		return false;
	}

	public boolean visit(ASTError astError) {
		astError.childrenAccept(this);
		return false;
	}

	public boolean visit(ASTNode node) {
		node.childrenAccept(this);
		return false;
	}

	public boolean visit(BackTickExpression backTickExpression) {
		backTickExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(Block block) {
		block.childrenAccept(this);
		return false;
	}

	public boolean visit(BreakStatement breakStatement) {
		breakStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(CastExpression castExpression) {
		castExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(CatchClause catchClause) {
		catchClause.childrenAccept(this);
		return false;
	}

	public boolean visit(ClassDeclaration classDeclaration) {
		classDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		classInstanceCreation.childrenAccept(this);
		return false;
	}

	public boolean visit(ClassName className) {
		className.childrenAccept(this);
		return false;
	}

	public boolean visit(CloneExpression cloneExpression) {
		cloneExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(Comment comment) {
		comment.childrenAccept(this);
		return false;
	}

	public boolean visit(ConditionalExpression conditionalExpression) {
		conditionalExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(ConstantDeclaration classConstantDeclaration) {
		classConstantDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(ContinueStatement continueStatement) {
		continueStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(DeclareStatement declareStatement) {
		declareStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(DoStatement doStatement) {
		doStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(EchoStatement echoStatement) {
		echoStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(EmptyStatement emptyStatement) {
		emptyStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(ExpressionStatement expressionStatement) {
		expressionStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(FieldAccess fieldAccess) {
		fieldAccess.childrenAccept(this);
		return false;
	}

	public boolean visit(FieldsDeclaration fieldsDeclaration) {
		fieldsDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(ForEachStatement forEachStatement) {
		forEachStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(FormalParameter formalParameter) {
		formalParameter.childrenAccept(this);
		return false;
	}

	public boolean visit(ForStatement forStatement) {
		forStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(FunctionDeclaration functionDeclaration) {
		functionDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(FunctionInvocation functionInvocation) {
		functionInvocation.childrenAccept(this);
		return false;
	}

	public boolean visit(FunctionName functionName) {
		functionName.childrenAccept(this);
		return false;
	}

	public boolean visit(GlobalStatement globalStatement) {
		globalStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(GotoLabel gotoLabel) {
		gotoLabel.childrenAccept(this);
		return false;
	}

	public boolean visit(GotoStatement gotoStatement) {
		gotoStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(Identifier identifier) {
		identifier.childrenAccept(this);
		return false;
	}

	public boolean visit(IfStatement ifStatement) {
		ifStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(IgnoreError ignoreError) {
		ignoreError.childrenAccept(this);
		return false;
	}

	public boolean visit(Include include) {
		include.childrenAccept(this);
		return false;
	}

	public boolean visit(InfixExpression infixExpression) {
		infixExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(InLineHtml inLineHtml) {
		inLineHtml.childrenAccept(this);
		return false;
	}

	public boolean visit(InstanceOfExpression instanceOfExpression) {
		instanceOfExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(InterfaceDeclaration interfaceDeclaration) {
		interfaceDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {
		lambdaFunctionDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(ListVariable listVariable) {
		listVariable.childrenAccept(this);
		return false;
	}

	public boolean visit(MethodDeclaration methodDeclaration) {
		methodDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(MethodInvocation methodInvocation) {
		methodInvocation.childrenAccept(this);
		return false;
	}

	public boolean visit(NamespaceDeclaration namespaceDeclaration) {
		namespaceDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(NamespaceName namespaceName) {
		namespaceName.childrenAccept(this);
		return false;
	}

	public boolean visit(ParenthesisExpression parenthesisExpression) {
		parenthesisExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(PostfixExpression postfixExpression) {
		postfixExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(PrefixExpression prefixExpression) {
		prefixExpression.childrenAccept(this);
		return false;
	}

	public boolean visit(Program program) {
		program.childrenAccept(this);
		return false;
	}

	public boolean visit(Quote quote) {
		quote.childrenAccept(this);
		return false;
	}

	public boolean visit(Reference reference) {
		reference.childrenAccept(this);
		return false;
	}

	public boolean visit(ReflectionVariable reflectionVariable) {
		reflectionVariable.childrenAccept(this);
		return false;
	}

	public boolean visit(ReturnStatement returnStatement) {
		returnStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(Scalar scalar) {
		scalar.childrenAccept(this);
		return false;
	}

	public boolean visit(SingleFieldDeclaration singleFieldDeclaration) {
		singleFieldDeclaration.childrenAccept(this);
		return false;
	}

	public boolean visit(StaticConstantAccess classConstantAccess) {
		classConstantAccess.childrenAccept(this);
		return false;
	}

	public boolean visit(StaticFieldAccess staticFieldAccess) {
		staticFieldAccess.childrenAccept(this);
		return false;
	}

	public boolean visit(StaticMethodInvocation staticMethodInvocation) {
		staticMethodInvocation.childrenAccept(this);
		return false;
	}

	public boolean visit(StaticStatement staticStatement) {
		staticStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(SwitchCase switchCase) {
		switchCase.childrenAccept(this);
		return false;
	}

	public boolean visit(SwitchStatement switchStatement) {
		switchStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(ThrowStatement throwStatement) {
		throwStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(TryStatement tryStatement) {
		tryStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(UnaryOperation unaryOperation) {
		unaryOperation.childrenAccept(this);
		return false;
	}

	public boolean visit(UseStatement useStatement) {
		useStatement.childrenAccept(this);
		return false;
	}

	public boolean visit(UseStatementPart useStatementPart) {
		useStatementPart.childrenAccept(this);
		return false;
	}

	public boolean visit(Variable variable) {
		variable.childrenAccept(this);
		return false;
	}

	public boolean visit(WhileStatement whileStatement) {
		whileStatement.childrenAccept(this);
		return false;
	}

	public void endVisit(ArrayAccess arrayAccess) {
	}

	public void endVisit(ArrayCreation arrayCreation) {
	}

	public void endVisit(ArrayElement arrayElement) {
	}

	public void endVisit(Assignment assignment) {
	}

	public void endVisit(ASTError astError) {
	}

	public void endVisit(ASTNode node) {
	}

	public void endVisit(BackTickExpression backTickExpression) {
	}

	public void endVisit(Block block) {
	}

	public void endVisit(BreakStatement breakStatement) {
	}

	public void endVisit(CastExpression castExpression) {
	}

	public void endVisit(CatchClause catchClause) {
	}

	public void endVisit(ClassDeclaration classDeclaration) {
	}

	public void endVisit(ClassInstanceCreation classInstanceCreation) {
	}

	public void endVisit(ClassName className) {
	}

	public void endVisit(CloneExpression cloneExpression) {
	}

	public void endVisit(Comment comment) {
	}

	public void endVisit(ConditionalExpression conditionalExpression) {
	}

	public void endVisit(ConstantDeclaration classConstantDeclaration) {
	}

	public void endVisit(ContinueStatement continueStatement) {
	}

	public void endVisit(DeclareStatement declareStatement) {
	}

	public void endVisit(DoStatement doStatement) {
	}

	public void endVisit(EchoStatement echoStatement) {
	}

	public void endVisit(EmptyStatement emptyStatement) {
	}

	public void endVisit(ExpressionStatement expressionStatement) {
	}

	public void endVisit(FieldAccess fieldAccess) {
	}

	public void endVisit(FieldsDeclaration fieldsDeclaration) {
	}

	public void endVisit(ForEachStatement forEachStatement) {
	}

	public void endVisit(FormalParameter formalParameter) {
	}

	public void endVisit(ForStatement forStatement) {
	}

	public void endVisit(FunctionDeclaration functionDeclaration) {
	}

	public void endVisit(FunctionInvocation functionInvocation) {
	}

	public void endVisit(FunctionName functionName) {
	}

	public void endVisit(GlobalStatement globalStatement) {
	}

	public void endVisit(GotoLabel gotoLabel) {
	}

	public void endVisit(GotoStatement gotoStatement) {
	}

	public void endVisit(Identifier identifier) {
	}

	public void endVisit(IfStatement ifStatement) {
	}

	public void endVisit(IgnoreError ignoreError) {
	}

	public void endVisit(Include include) {
	}

	public void endVisit(InfixExpression infixExpression) {
	}

	public void endVisit(InLineHtml inLineHtml) {
	}

	public void endVisit(InstanceOfExpression instanceOfExpression) {
	}

	public void endVisit(InterfaceDeclaration interfaceDeclaration) {
	}

	public void endVisit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {
	}

	public void endVisit(ListVariable listVariable) {
	}

	public void endVisit(MethodDeclaration methodDeclaration) {
	}

	public void endVisit(MethodInvocation methodInvocation) {
	}

	public void endVisit(NamespaceDeclaration namespaceDeclaration) {
	}

	public void endVisit(NamespaceName namespaceName) {
	}

	public void endVisit(ParenthesisExpression parenthesisExpression) {
	}

	public void endVisit(PostfixExpression postfixExpression) {
	}

	public void endVisit(PrefixExpression prefixExpression) {
	}

	public void endVisit(Program program) {
	}

	public void endVisit(Quote quote) {
	}

	public void endVisit(Reference reference) {
	}

	public void endVisit(ReflectionVariable reflectionVariable) {
	}

	public void endVisit(ReturnStatement returnStatement) {
	}

	public void endVisit(Scalar scalar) {
	}

	public void endVisit(SingleFieldDeclaration singleFieldDeclaration) {
	}

	public void endVisit(StaticConstantAccess staticConstantAccess) {
	}

	public void endVisit(StaticFieldAccess staticFieldAccess) {
	}

	public void endVisit(StaticMethodInvocation staticMethodInvocation) {
	}

	public void endVisit(StaticStatement staticStatement) {
	}

	public void endVisit(SwitchCase switchCase) {
	}

	public void endVisit(SwitchStatement switchStatement) {
	}

	public void endVisit(ThrowStatement throwStatement) {
	}

	public void endVisit(TryStatement tryStatement) {
	}

	public void endVisit(UnaryOperation unaryOperation) {
	}

	public void endVisit(UseStatement useStatement) {
	}

	public void endVisit(UseStatementPart useStatementPart) {
	}

	public void endVisit(Variable variable) {
	}

	public void endVisit(WhileStatement whileStatement) {
	}

	public void postVisit(ASTNode node) {
	}

	public void preVisit(ASTNode node) {
	}

	public boolean visit(ChainingInstanceCall node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(ChainingInstanceCall node) {
	}

	public boolean visit(DereferenceNode node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(DereferenceNode node) {

	}

	public boolean visit(FullyQualifiedTraitMethodReference node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(FullyQualifiedTraitMethodReference node) {
	}

	public boolean visit(PHPArrayDereferenceList node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(PHPArrayDereferenceList node) {
	}

	public boolean visit(TraitAlias node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitAlias node) {
	}

	public boolean visit(TraitAliasStatement node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitAliasStatement node) {

	}

	public boolean visit(TraitDeclaration node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitDeclaration node) {
	}

	public boolean visit(TraitPrecedence node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitPrecedence node) {
	}

	public boolean visit(TraitPrecedenceStatement node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitPrecedenceStatement node) {
	}

	public boolean visit(TraitUseStatement node) {
		node.childrenAccept(this);
		return false;
	}

	public void endVisit(TraitUseStatement node) {
	}
}
