package org.pdtextensions.core.ast.util;

import org.eclipse.php.core.ast.nodes.ASTError;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ArrayAccess;
import org.eclipse.php.core.ast.nodes.ArrayCreation;
import org.eclipse.php.core.ast.nodes.ArrayElement;
import org.eclipse.php.core.ast.nodes.Assignment;
import org.eclipse.php.core.ast.nodes.BackTickExpression;
import org.eclipse.php.core.ast.nodes.Block;
import org.eclipse.php.core.ast.nodes.BreakStatement;
import org.eclipse.php.core.ast.nodes.CastExpression;
import org.eclipse.php.core.ast.nodes.CatchClause;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.core.ast.nodes.ClassName;
import org.eclipse.php.core.ast.nodes.CloneExpression;
import org.eclipse.php.core.ast.nodes.Comment;
import org.eclipse.php.core.ast.nodes.ConditionalExpression;
import org.eclipse.php.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.core.ast.nodes.ContinueStatement;
import org.eclipse.php.core.ast.nodes.DeclareStatement;
import org.eclipse.php.core.ast.nodes.DoStatement;
import org.eclipse.php.core.ast.nodes.EchoStatement;
import org.eclipse.php.core.ast.nodes.EmptyStatement;
import org.eclipse.php.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.core.ast.nodes.FinallyClause;
import org.eclipse.php.core.ast.nodes.ForEachStatement;
import org.eclipse.php.core.ast.nodes.ForStatement;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.FullyQualifiedTraitMethodReference;
import org.eclipse.php.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.core.ast.nodes.FunctionName;
import org.eclipse.php.core.ast.nodes.GlobalStatement;
import org.eclipse.php.core.ast.nodes.GotoLabel;
import org.eclipse.php.core.ast.nodes.GotoStatement;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.IfStatement;
import org.eclipse.php.core.ast.nodes.IgnoreError;
import org.eclipse.php.core.ast.nodes.InLineHtml;
import org.eclipse.php.core.ast.nodes.Include;
import org.eclipse.php.core.ast.nodes.InfixExpression;
import org.eclipse.php.core.ast.nodes.InstanceOfExpression;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.core.ast.nodes.ListVariable;
import org.eclipse.php.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.core.ast.nodes.MethodInvocation;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.NamespaceName;
import org.eclipse.php.core.ast.nodes.ParenthesisExpression;
import org.eclipse.php.core.ast.nodes.PostfixExpression;
import org.eclipse.php.core.ast.nodes.PrefixExpression;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.nodes.Quote;
import org.eclipse.php.core.ast.nodes.Reference;
import org.eclipse.php.core.ast.nodes.ReflectionVariable;
import org.eclipse.php.core.ast.nodes.ReturnStatement;
import org.eclipse.php.core.ast.nodes.Scalar;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.ast.nodes.StaticFieldAccess;
import org.eclipse.php.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.core.ast.nodes.StaticStatement;
import org.eclipse.php.core.ast.nodes.SwitchCase;
import org.eclipse.php.core.ast.nodes.SwitchStatement;
import org.eclipse.php.core.ast.nodes.ThrowStatement;
import org.eclipse.php.core.ast.nodes.TraitAlias;
import org.eclipse.php.core.ast.nodes.TraitAliasStatement;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TraitPrecedence;
import org.eclipse.php.core.ast.nodes.TraitPrecedenceStatement;
import org.eclipse.php.core.ast.nodes.TraitUseStatement;
import org.eclipse.php.core.ast.nodes.TryStatement;
import org.eclipse.php.core.ast.nodes.UnaryOperation;
import org.eclipse.php.core.ast.nodes.UseStatement;
import org.eclipse.php.core.ast.nodes.UseStatementPart;
import org.eclipse.php.core.ast.nodes.Variable;
import org.eclipse.php.core.ast.nodes.WhileStatement;
import org.eclipse.php.core.ast.nodes.YieldExpression;
import org.eclipse.php.core.ast.visitor.Visitor;

@SuppressWarnings("restriction")
abstract public class GenericVisitor implements Visitor {

	abstract protected boolean visitNode(ASTNode node);

	@Override
	public void preVisit(ASTNode node) {
	}

	@Override
	public void postVisit(ASTNode node) {
	}

	@Override
	public boolean visit(ArrayAccess arrayAccess) {
		return visitNode(arrayAccess);
	}

	@Override
	public void endVisit(ArrayAccess arrayAccess) {
	}

	@Override
	public boolean visit(ArrayCreation arrayCreation) {
		return visitNode(arrayCreation);
	}

	@Override
	public void endVisit(ArrayCreation arrayCreation) {
	}

	@Override
	public boolean visit(ArrayElement arrayElement) {

		return visitNode(arrayElement);
	}

	@Override
	public void endVisit(ArrayElement arrayElement) {

	}

	@Override
	public boolean visit(Assignment assignment) {

		return visitNode(assignment);
	}

	@Override
	public void endVisit(Assignment assignment) {

	}

	@Override
	public boolean visit(ASTError astError) {

		return visitNode(astError);
	}

	@Override
	public void endVisit(ASTError astError) {

	}

	@Override
	public boolean visit(BackTickExpression backTickExpression) {

		return visitNode(backTickExpression);
	}

	@Override
	public void endVisit(BackTickExpression backTickExpression) {

	}

	@Override
	public boolean visit(Block block) {

		return visitNode(block);
	}

	@Override
	public void endVisit(Block block) {

	}

	@Override
	public boolean visit(BreakStatement breakStatement) {

		return visitNode(breakStatement);
	}

	@Override
	public void endVisit(BreakStatement breakStatement) {

	}

	@Override
	public boolean visit(CastExpression castExpression) {

		return visitNode(castExpression);
	}

	@Override
	public void endVisit(CastExpression castExpression) {

	}

	@Override
	public boolean visit(CatchClause catchClause) {

		return visitNode(catchClause);
	}

	@Override
	public void endVisit(CatchClause catchClause) {

	}

	@Override
	public boolean visit(ConstantDeclaration classConstantDeclaration) {

		return visitNode(classConstantDeclaration);
	}

	@Override
	public void endVisit(ConstantDeclaration classConstantDeclaration) {

	}

	@Override
	public boolean visit(ClassDeclaration classDeclaration) {

		return visitNode(classDeclaration);
	}

	@Override
	public void endVisit(ClassDeclaration classDeclaration) {

	}

	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {

		return visitNode(classInstanceCreation);
	}

	@Override
	public void endVisit(ClassInstanceCreation classInstanceCreation) {

	}

	@Override
	public boolean visit(ClassName className) {

		return visitNode(className);
	}

	@Override
	public void endVisit(ClassName className) {

	}

	@Override
	public boolean visit(CloneExpression cloneExpression) {

		return visitNode(cloneExpression);
	}

	@Override
	public void endVisit(CloneExpression cloneExpression) {

	}

	@Override
	public boolean visit(Comment comment) {

		return visitNode(comment);
	}

	@Override
	public void endVisit(Comment comment) {

	}

	@Override
	public boolean visit(ConditionalExpression conditionalExpression) {

		return visitNode(conditionalExpression);
	}

	@Override
	public void endVisit(ConditionalExpression conditionalExpression) {

	}

	@Override
	public boolean visit(ContinueStatement continueStatement) {

		return visitNode(continueStatement);
	}

	@Override
	public void endVisit(ContinueStatement continueStatement) {

	}

	@Override
	public boolean visit(DeclareStatement declareStatement) {

		return visitNode(declareStatement);
	}

	@Override
	public void endVisit(DeclareStatement declareStatement) {

	}

	@Override
	public boolean visit(DoStatement doStatement) {

		return visitNode(doStatement);
	}

	@Override
	public void endVisit(DoStatement doStatement) {

	}

	@Override
	public boolean visit(EchoStatement echoStatement) {

		return visitNode(echoStatement);
	}

	@Override
	public void endVisit(EchoStatement echoStatement) {

	}

	@Override
	public boolean visit(EmptyStatement emptyStatement) {

		return visitNode(emptyStatement);
	}

	@Override
	public void endVisit(EmptyStatement emptyStatement) {

	}

	@Override
	public boolean visit(ExpressionStatement expressionStatement) {

		return visitNode(expressionStatement);
	}

	@Override
	public void endVisit(ExpressionStatement expressionStatement) {

	}

	@Override
	public boolean visit(FieldAccess fieldAccess) {

		return visitNode(fieldAccess);
	}

	@Override
	public void endVisit(FieldAccess fieldAccess) {

	}

	@Override
	public boolean visit(FieldsDeclaration fieldsDeclaration) {

		return visitNode(fieldsDeclaration);
	}

	@Override
	public void endVisit(FieldsDeclaration fieldsDeclaration) {

	}

	@Override
	public boolean visit(ForEachStatement forEachStatement) {

		return visitNode(forEachStatement);
	}

	@Override
	public void endVisit(ForEachStatement forEachStatement) {

	}

	@Override
	public boolean visit(FormalParameter formalParameter) {

		return visitNode(formalParameter);
	}

	@Override
	public void endVisit(FormalParameter formalParameter) {

	}

	@Override
	public boolean visit(ForStatement forStatement) {

		return visitNode(forStatement);
	}

	@Override
	public void endVisit(ForStatement forStatement) {

	}

	@Override
	public boolean visit(FunctionDeclaration functionDeclaration) {

		return visitNode(functionDeclaration);
	}

	@Override
	public void endVisit(FunctionDeclaration functionDeclaration) {

	}

	@Override
	public boolean visit(FunctionInvocation functionInvocation) {

		return visitNode(functionInvocation);
	}

	@Override
	public void endVisit(FunctionInvocation functionInvocation) {

	}

	@Override
	public boolean visit(FunctionName functionName) {

		return visitNode(functionName);
	}

	@Override
	public void endVisit(FunctionName functionName) {

	}

	@Override
	public boolean visit(GlobalStatement globalStatement) {

		return visitNode(globalStatement);
	}

	@Override
	public void endVisit(GlobalStatement globalStatement) {

	}

	@Override
	public boolean visit(GotoLabel gotoLabel) {

		return visitNode(gotoLabel);
	}

	@Override
	public void endVisit(GotoLabel gotoLabel) {

	}

	@Override
	public boolean visit(GotoStatement gotoStatement) {

		return visitNode(gotoStatement);
	}

	@Override
	public void endVisit(GotoStatement gotoStatement) {

	}

	@Override
	public boolean visit(Identifier identifier) {

		return visitNode(identifier);
	}

	@Override
	public void endVisit(Identifier identifier) {

	}

	@Override
	public boolean visit(IfStatement ifStatement) {

		return visitNode(ifStatement);
	}

	@Override
	public void endVisit(IfStatement ifStatement) {

	}

	@Override
	public boolean visit(IgnoreError ignoreError) {

		return visitNode(ignoreError);
	}

	@Override
	public void endVisit(IgnoreError ignoreError) {

	}

	@Override
	public boolean visit(Include include) {

		return visitNode(include);
	}

	@Override
	public void endVisit(Include include) {

	}

	@Override
	public boolean visit(InfixExpression infixExpression) {

		return visitNode(infixExpression);
	}

	@Override
	public void endVisit(InfixExpression infixExpression) {

	}

	@Override
	public boolean visit(InLineHtml inLineHtml) {

		return visitNode(inLineHtml);
	}

	@Override
	public void endVisit(InLineHtml inLineHtml) {

	}

	@Override
	public boolean visit(InstanceOfExpression instanceOfExpression) {

		return visitNode(instanceOfExpression);
	}

	@Override
	public void endVisit(InstanceOfExpression instanceOfExpression) {

	}

	@Override
	public boolean visit(InterfaceDeclaration interfaceDeclaration) {

		return visitNode(interfaceDeclaration);
	}

	@Override
	public void endVisit(InterfaceDeclaration interfaceDeclaration) {

	}

	@Override
	public boolean visit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {

		return visitNode(lambdaFunctionDeclaration);
	}

	@Override
	public void endVisit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {

	}

	@Override
	public boolean visit(ListVariable listVariable) {

		return visitNode(listVariable);
	}

	@Override
	public void endVisit(ListVariable listVariable) {

	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {

		return visitNode(methodDeclaration);
	}

	@Override
	public void endVisit(MethodDeclaration methodDeclaration) {

	}

	@Override
	public boolean visit(MethodInvocation methodInvocation) {

		return visitNode(methodInvocation);
	}

	@Override
	public void endVisit(MethodInvocation methodInvocation) {

	}

	@Override
	public boolean visit(NamespaceName namespaceName) {

		return visitNode(namespaceName);
	}

	@Override
	public void endVisit(NamespaceName namespaceName) {

	}

	@Override
	public boolean visit(NamespaceDeclaration namespaceDeclaration) {

		return visitNode(namespaceDeclaration);
	}

	@Override
	public void endVisit(NamespaceDeclaration namespaceDeclaration) {

	}

	@Override
	public boolean visit(ParenthesisExpression parenthesisExpression) {

		return visitNode(parenthesisExpression);
	}

	@Override
	public void endVisit(ParenthesisExpression parenthesisExpression) {

	}

	@Override
	public boolean visit(PostfixExpression postfixExpression) {

		return visitNode(postfixExpression);
	}

	@Override
	public void endVisit(PostfixExpression postfixExpression) {

	}

	@Override
	public boolean visit(PrefixExpression prefixExpression) {

		return visitNode(prefixExpression);
	}

	@Override
	public void endVisit(PrefixExpression prefixExpression) {

	}

	@Override
	public boolean visit(Program program) {

		return visitNode(program);
	}

	@Override
	public void endVisit(Program program) {

	}

	@Override
	public boolean visit(Quote quote) {

		return visitNode(quote);
	}

	@Override
	public void endVisit(Quote quote) {

	}

	@Override
	public boolean visit(Reference reference) {

		return visitNode(reference);
	}

	@Override
	public void endVisit(Reference reference) {

	}

	@Override
	public boolean visit(ReflectionVariable reflectionVariable) {

		return visitNode(reflectionVariable);
	}

	@Override
	public void endVisit(ReflectionVariable reflectionVariable) {

	}

	@Override
	public boolean visit(ReturnStatement returnStatement) {

		return visitNode(returnStatement);
	}

	@Override
	public void endVisit(ReturnStatement returnStatement) {

	}

	@Override
	public boolean visit(Scalar scalar) {

		return visitNode(scalar);
	}

	@Override
	public void endVisit(Scalar scalar) {

	}

	@Override
	public boolean visit(SingleFieldDeclaration singleFieldDeclaration) {

		return visitNode(singleFieldDeclaration);
	}

	@Override
	public void endVisit(SingleFieldDeclaration singleFieldDeclaration) {

	}

	@Override
	public boolean visit(StaticConstantAccess classConstantAccess) {

		return visitNode(classConstantAccess);
	}

	@Override
	public void endVisit(StaticConstantAccess staticConstantAccess) {

	}

	@Override
	public boolean visit(StaticFieldAccess staticFieldAccess) {

		return visitNode(staticFieldAccess);
	}

	@Override
	public void endVisit(StaticFieldAccess staticFieldAccess) {

	}

	@Override
	public boolean visit(StaticMethodInvocation staticMethodInvocation) {

		return visitNode(staticMethodInvocation);
	}

	@Override
	public void endVisit(StaticMethodInvocation staticMethodInvocation) {

	}

	@Override
	public boolean visit(StaticStatement staticStatement) {

		return visitNode(staticStatement);
	}

	@Override
	public void endVisit(StaticStatement staticStatement) {

	}

	@Override
	public boolean visit(SwitchCase switchCase) {

		return visitNode(switchCase);
	}

	@Override
	public void endVisit(SwitchCase switchCase) {

	}

	@Override
	public boolean visit(SwitchStatement switchStatement) {

		return visitNode(switchStatement);
	}

	@Override
	public void endVisit(SwitchStatement switchStatement) {

	}

	@Override
	public boolean visit(ThrowStatement throwStatement) {

		return visitNode(throwStatement);
	}

	@Override
	public void endVisit(ThrowStatement throwStatement) {

	}

	@Override
	public boolean visit(TryStatement tryStatement) {

		return visitNode(tryStatement);
	}

	@Override
	public void endVisit(TryStatement tryStatement) {

	}

	@Override
	public boolean visit(UnaryOperation unaryOperation) {

		return visitNode(unaryOperation);
	}

	@Override
	public void endVisit(UnaryOperation unaryOperation) {

	}

	@Override
	public boolean visit(Variable variable) {

		return visitNode(variable);
	}

	@Override
	public void endVisit(Variable variable) {

	}

	@Override
	public boolean visit(UseStatement useStatement) {

		return visitNode(useStatement);
	}

	@Override
	public void endVisit(UseStatement useStatement) {

	}

	@Override
	public boolean visit(UseStatementPart useStatementPart) {

		return visitNode(useStatementPart);
	}

	@Override
	public void endVisit(UseStatementPart useStatementPart) {

	}

	@Override
	public boolean visit(WhileStatement whileStatement) {

		return visitNode(whileStatement);
	}

	@Override
	public void endVisit(WhileStatement whileStatement) {

	}

	@Override
	public boolean visit(ASTNode node) {

		return visitNode(node);
	}

	@Override
	public void endVisit(ASTNode node) {

	}

	@Override
	public boolean visit(FullyQualifiedTraitMethodReference node) {

		return visitNode(node);
	}

	@Override
	public void endVisit(FullyQualifiedTraitMethodReference node) {

	}

	@Override
	public boolean visit(TraitAlias node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitAlias node) {
	}

	@Override
	public boolean visit(TraitAliasStatement node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitAliasStatement node) {
	}

	@Override
	public boolean visit(TraitDeclaration node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitDeclaration node) {
	}

	@Override
	public boolean visit(TraitPrecedence node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitPrecedence node) {
	}

	@Override
	public boolean visit(TraitPrecedenceStatement node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitPrecedenceStatement node) {
	}

	@Override
	public boolean visit(TraitUseStatement node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(TraitUseStatement node) {
	}

	@Override
	public boolean visit(YieldExpression YieldExpression) {
		return visitNode(YieldExpression);
	}

	@Override
	public void endVisit(YieldExpression YieldExpression) {
	}

	@Override
	public boolean visit(FinallyClause YieldExpression) {
		return visitNode(YieldExpression);
	}

	@Override
	public void endVisit(FinallyClause YieldExpression) {
	}

}
