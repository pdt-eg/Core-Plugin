package org.pdtextensions.core.ui.actions.refactoring;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.AST;
import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.ASTParser;
import org.eclipse.php.internal.core.ast.nodes.Block;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.Identifier;
import org.eclipse.php.internal.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.internal.core.ast.nodes.MethodInvocation;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Reference;
import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.Variable;
import org.eclipse.php.internal.core.ast.nodes.Assignment;
import org.eclipse.php.internal.core.ast.nodes.VariableBase;
import org.eclipse.php.internal.core.ast.rewrite.ASTRewrite;
import org.eclipse.php.internal.core.ast.rewrite.ListRewrite;
import org.eclipse.php.internal.core.project.ProjectOptions;
import org.eclipse.php.internal.core.search.Messages;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.pdtextensions.core.ast.util.BlockContainsFinder;
import org.pdtextensions.core.ast.util.BlockContainsFinder.Match;
import org.pdtextensions.core.ast.util.CoveringDeclarationFinder;
import org.pdtextensions.core.ast.util.LocalVariableFinder;
import org.pdtextensions.core.ast.util.RangeAssignmentFinder;
import org.pdtextensions.core.ast.util.RangeNodeFinder;
import org.pdtextensions.core.ast.util.ReturnStatementFinder;
import org.pdtextensions.core.ast.util.SourceRangeUtil;
import org.pdtextensions.core.ui.exception.RefactoringStatusException;
import org.pdtextensions.core.ui.refactoring.RefactoringMessages;
import org.pdtextensions.internal.corext.refactoring.Checks;
import org.pdtextensions.internal.corext.refactoring.ParameterInfo;

@SuppressWarnings("restriction")
/**
 * TODO: This class ignores the scope of variables and compares them just by name. Better approach would be
 * 		 to use the scope too, to determine whether the variables are pointing to the same value.  
 *         
 * TODO: Finding duplicates should ignore the variable name. (currently it searches for the exact same names)
 * 
 * TODO: Maybe add "final" and "static" modifier. If "static" is checked, then $this, must explicitly be concerned... 
 * 
 * TODO: If a parameter has a type, then add a type hint for the extracted method? 
 * 
 * @author Alex
 *
 */
public class ExtractMethodRefactoring extends Refactoring {

	private final static String THIS_VARIABLE_NAME = "this";
	private final static String METHOD_ARGUMENT_CLOSING_CHAR = ")";
	
	private ISourceModule fSourceModule;
	
	private int fSelectionStart;
	private int fSelectionLength;
	
	private boolean fReturnMultipleVariables;
	private boolean fGenerateDoc;
	private String fMethodName;
	private int fModifierAccessFlag;
	private boolean fReplaceDuplicates;
	
	private String fSelectedSource;
	
	private List<ParameterInfo> fExtractedMethodParameters = new ArrayList<ParameterInfo>();
	private List<ParameterInfo> fExtractedMethodReturnValues = new ArrayList<ParameterInfo>();
	private boolean fSelectedCodeContainsReturnStatement;

	private Program fProgram;
	private List<Variable> fMethodVariables;
	private List<Assignment> fMethodAssignments;
	
	private ArrayList<ParameterInfo> fReturnAndArgumentParameters = new ArrayList<ParameterInfo>();
	private ArrayList<ParameterInfo> fMustExplicitReturnParameters = new ArrayList<ParameterInfo>();
	private ParameterInfo fReturnStatement;
	private ArrayList<FormalParameter> fMethodParameters;
	
	private ISourceRange fSelectedSourceRange;
	private ISourceRange fPreSelectedSourceRange;
	private ISourceRange fPostSelectedSourceRange;
	private ISourceRange fSelectedMethodSourceRange;
	
	private CoveringDeclarationFinder fCoveringDeclarationFinder;
	
	private List<Match> fDuplicates;

	private RangeNodeFinder fSelectedNodesFinder;
		
	public ExtractMethodRefactoring(ISourceModule module, int selectionStart, int selectionLength)
	{
		fSourceModule = module;
		fGenerateDoc = true;
		fReturnMultipleVariables = false;
		fMethodName = "extracted"; //$NON-NLS-1$
		fSelectionStart = selectionStart;
		fSelectionLength = selectionLength;
		fSelectedSourceRange = new SourceRange(selectionStart, selectionLength);
		fReplaceDuplicates = false;
	}

	private void parsePHPCode() throws RefactoringStatusException {
		try {
			// parse the php code and create a program
			ASTParser parser = ASTParser.newParser(fSourceModule);
			fProgram = parser.createAST(null);
		} catch(Exception e) {
			throw new RefactoringStatusException(RefactoringMessages.ExtractMethodInputPage_errorCouldNotParseSourceCode);
		}
		
		// Get covering namespace/class/method/function declaration
		fCoveringDeclarationFinder = new CoveringDeclarationFinder();
		fCoveringDeclarationFinder.setRange(getSelectedRange());
		fProgram.accept(fCoveringDeclarationFinder);
		
		try {
			// retrieve method, which covers the selected code
			fSelectedMethodSourceRange = SourceRangeUtil.createFrom(fCoveringDeclarationFinder.getCoveringMethodDeclaration());
			// get the access modifiers from the covering method (e.g. public/protected/private) and ignore final/static etc.. modifiers
			fModifierAccessFlag = fCoveringDeclarationFinder.getCoveringMethodDeclaration().getModifier() & (Modifiers.AccPublic | Modifiers.AccProtected | Modifiers.AccPrivate | Modifiers.AccStatic);
		} catch(NullPointerException e) {
			throw new RefactoringStatusException(RefactoringMessages.ExtractMethodInputPage_errorCouldNotRetrieveCoveringMethodDeclaration);
		}
		
		// compute source ranges before and after the selected code
		fPreSelectedSourceRange = new SourceRange(fSelectedMethodSourceRange.getOffset(), fSelectionStart - fSelectedMethodSourceRange.getOffset());
		fPostSelectedSourceRange = new SourceRange(fSelectionStart + fSelectionLength, fSelectedMethodSourceRange.getOffset() + fSelectedMethodSourceRange.getLength() - fSelectionStart + fSelectionLength);
		
		// find all variables used in the selected method
		LocalVariableFinder finder = new LocalVariableFinder();
		finder.setRange(fSelectedMethodSourceRange);
		fProgram.accept(finder);
		
		// those are the used variables, including method parameters
		fMethodVariables = finder.getFoundVariables();
		// those are only the method parameters, as FormalParameter
		fMethodParameters = finder.getParameters();
		
		// find all assignments in the selected method 
		RangeAssignmentFinder assignmentFinder = new RangeAssignmentFinder();
		assignmentFinder.setRange(fSelectedMethodSourceRange);
		fProgram.accept(assignmentFinder);
		
		// those are all assignments in the selected method
		fMethodAssignments = assignmentFinder.getFoundAssignments();
		
		fSelectedNodesFinder = new RangeNodeFinder(fSelectedSourceRange);
		fProgram.accept(fSelectedNodesFinder);
		if(fSelectedNodesFinder.getNodes().size() == 0) {
			throw new RefactoringStatusException(RefactoringMessages.ExtractMethodInputPage_errorCouldNotParseSelectedCode);
		}
		
		ReturnStatementFinder returnFinder = new ReturnStatementFinder();
		returnFinder.setRange(fSelectedSourceRange);
		fProgram.accept(returnFinder);
		
		fSelectedCodeContainsReturnStatement = returnFinder.hasFoundReturnStatement();
	}
		
	private void computePassByReferenceArguments()
	{
		boolean matched = false;
		
		for(ParameterInfo returnParameter : fExtractedMethodReturnValues) {
			
			matched = false;
			for(ParameterInfo argumentParameter : fExtractedMethodParameters) {
				
				if(returnParameter.getParameterName().equals(argumentParameter.getParameterName())) {
					fReturnAndArgumentParameters.add(returnParameter);
					matched = true;
					break;
				}
			}
			
			if(!matched) {
				fMustExplicitReturnParameters.add(returnParameter);
			}
		}
		
		if(fMustExplicitReturnParameters.size() >= 1) {
			fReturnStatement = fMustExplicitReturnParameters.get(0);
		} else if(fReturnAndArgumentParameters.size() >= 1) {
			fReturnStatement = fReturnAndArgumentParameters.get(0);
		} 
	}
	
	@Override
	public String getName() {		
		return RefactoringMessages.ExtractMethod_name;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		
		fSelectedSource = fSourceModule.getSource().substring(fSelectionStart,
				fSelectionStart + fSelectionLength);
		try {
			parsePHPCode();
			
			// TODO: throw exceptions in those methods on wrong behaviour.
			computeRequiredArgumentsForExtractedMethod();
			computeMethodReturnValues();
			computePassByReferenceArguments();
			computeReplacements();
			
		} catch(RefactoringStatusException exception){
			status.addFatalError(exception.getStatusMessage());
		}
		
		if(fMustExplicitReturnParameters.size() > 1) {
			status.addFatalError(RefactoringMessages.ExtractMethodInputPage_errorMethodCannotReturnMultipleVariables);
		}

		return status;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		
		if(!Checks.checkMethodName(fMethodName).isOK()) {
			status.addFatalError(Messages.format(RefactoringMessages.ExtractMethodInputPage_errorNoValidMethodName, fMethodName));
		}
		
		for(ParameterInfo parameter : fExtractedMethodParameters)
		{
			if(!Checks.checkTypeName(parameter.getParameterName()).isOK() ) {
				status.addFatalError(Messages.format(RefactoringMessages.ExtractMethodInputPage_errorNoValidParameterName, parameter.getParameterName()));
			}
		}
		
		if(fSelectedCodeContainsReturnStatement) {
			status.addFatalError(RefactoringMessages.ExtractMethodInputPage_errorContainsReturnStatement);
		}
		
		if(fReturnMultipleVariables == false && fExtractedMethodReturnValues.size() > 1) {
			status.addFatalError(RefactoringMessages.ExtractMethodInputPage_errorMultipleReturnValuesNotAllowed);
		}
		
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		ITextFileBufferManager bufferManager = FileBuffers
				.getTextFileBufferManager();
		IPath path = fSourceModule.getPath();
		bufferManager.connect(path, LocationKind.IFILE, null);
		ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path,
				LocationKind.IFILE);
		
		IDocument document = textFileBuffer.getDocument();
		
		DocumentChange anotherChange = new DocumentChange(RefactoringMessages.ExtractMethodPreviewPage_TextChangeName, document);
		
		MultiTextEdit rootEdit = new MultiTextEdit();
		
		anotherChange.setEdit(rootEdit);
		
		TextEditGroup newMethodEdit = new TextEditGroup(Messages.format(RefactoringMessages.ExtractMethodPreviewPage_TextChangeNewMethod, fMethodName));
		TextEditGroup inlineReplacementEdit = new TextEditGroup(Messages.format(RefactoringMessages.ExtractMethodPreviewPage_TextChangeSubstituteStatements, fMethodName));
		TextEditGroup additionalInlineReplacementEdit = new TextEditGroup(Messages.format(RefactoringMessages.ExtractMethodPreviewPage_TextChangeSubsituteDuplicateStatements, fMethodName));
		anotherChange.addTextEditGroup(newMethodEdit);
		anotherChange.addTextEditGroup(inlineReplacementEdit);
		anotherChange.addTextEditGroup(additionalInlineReplacementEdit);
		
		AST ast = fProgram.getAST();
		MethodDeclaration method = ast.newMethodDeclaration();
		Block extractedMethodBody = ast.newBlock();
				
		FunctionDeclaration functionDec = ast.newFunctionDeclaration(ast.newIdentifier(fMethodName), computeArguments(ast), extractedMethodBody, false);
		method.setModifier(fModifierAccessFlag);
		method.setFunction(functionDec);
		
		ASTRewrite rewriter = ASTRewrite.create(ast);
		
		ListRewrite classListRewrite = rewriter.getListRewrite( fCoveringDeclarationFinder.getCoveringClassDeclaration().getBody(), Block.STATEMENTS_PROPERTY);
		VariableBase dispatcher = ast.newVariable(THIS_VARIABLE_NAME);
		FunctionInvocation calledExtractedMethod = ast.newFunctionInvocation(ast.newFunctionName(ast.newIdentifier(fMethodName)), computeParameters(ast));
		MethodInvocation inlineMethodCall = ast.newMethodInvocation(dispatcher, calledExtractedMethod);

		List<List<ASTNode>> Occurences = new ArrayList<List<ASTNode>>();
		
		if(fReplaceDuplicates) {
			for(Match replace : fDuplicates) {
				Occurences.add(Arrays.asList(replace.getNodes()));
			}
		} else {
			Occurences.add(fSelectedNodesFinder.getNodes());
		}
		
		boolean createdMethodBody = false;
		
		TextEditGroup inlineReplacementEditGroup = inlineReplacementEdit;
		
		for(List<ASTNode> selectedNodeOccurence : Occurences) {
		
			// this is also an indicator, whether this loop was already gone through
			if(createdMethodBody) {
				inlineReplacementEditGroup = additionalInlineReplacementEdit;
			}
			
			ASTNode parent = selectedNodeOccurence.get(0).getParent();
			
			inlineMethodCall = ASTNode.copySubtree(ast, inlineMethodCall);
			
			ListRewrite lrw;
						
			if(parent instanceof Block) {
				
				if(!createdMethodBody) {
					extractedMethodBody.statements().addAll(ASTNode.copySubtrees(ast, fSelectedNodesFinder.getNodes()));
					addReturnStatement(ast, extractedMethodBody, fReturnStatement);
					createdMethodBody = true;
				}
				
				lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY);
				
				ExpressionStatement inlineReplacement;
				if (fReturnStatement != null) {
					inlineReplacement = ast.newExpressionStatement(ast.newAssignment(
							ast.newVariable(fReturnStatement.getParameterName()),
							Assignment.OP_EQUAL, inlineMethodCall));
				} else {
					inlineReplacement = ast.newExpressionStatement(inlineMethodCall);
				}
				
				lrw.replace(selectedNodeOccurence.get(0),inlineReplacement, inlineReplacementEditGroup);
	
				for (int i = 1; i < selectedNodeOccurence.size(); ++i) {
					lrw.remove(selectedNodeOccurence.get(i), inlineReplacementEditGroup);
				}
				
			} else {
				if(!createdMethodBody) {
					addReturnStatement(ast, extractedMethodBody, ASTNode.copySubtree(ast, selectedNodeOccurence.get(0)));
					createdMethodBody = true;
				}
				rewriter.replace( selectedNodeOccurence.get(0), inlineMethodCall, inlineReplacementEditGroup);
			}
		} 
		

		classListRewrite.insertAfter(method, fCoveringDeclarationFinder.getCoveringMethodDeclaration(), newMethodEdit);
		
		TextEdit fullDocumentEdit = rewriter.rewriteAST(document, null);

		anotherChange.addEdit(fullDocumentEdit);
		
		return anotherChange;
	}

	private void addReturnStatement(AST ast, Block body, ParameterInfo parameter) {
		if(parameter != null) {
			body.statements().add(ast.newReturnStatement(ast.newVariable(parameter.getParameterName())));
		}
	}
	
	private void addReturnStatement(AST ast, Block body, ASTNode expression) {
		if(expression instanceof Expression) {
			body.statements().add(ast.newReturnStatement((Expression) expression));
		} else if(expression instanceof ExpressionStatement) {
			body.statements().add((ExpressionStatement) expression);
		}
	}

	private void computeReplacements() {
		
		// retrieve all method declarations
		List<Statement> classStatements = ((Block) fCoveringDeclarationFinder.getCoveringMethodDeclaration().getParent()).statements();
		
		ASTNode[] toSearchNodes = fSelectedNodesFinder.getNodes().toArray(new ASTNode[]{});
		fDuplicates = new ArrayList<Match>();
		
		// loop through every class statement, this might also include field declarations (etc..) so skip everything except MethodDeclarations.
		// => Check every method in the covering class for similar nodes.
		for(Statement statement : classStatements) {
			if(!(statement instanceof MethodDeclaration) ){
				continue;
			}

			// find all similar nodes
			BlockContainsFinder replacementFinder = new BlockContainsFinder(((MethodDeclaration) statement).getFunction().getBody(), toSearchNodes);
			replacementFinder.perform();
			
			// add the matches.
			fDuplicates.addAll(replacementFinder.getMatches());
		}
		
		// at least, the selected nodes have to get found...
		Assert.isLegal(fDuplicates.size() > 0);
	}

	private ArrayList<FormalParameter> computeArguments(AST ast)
	{
		ArrayList<FormalParameter> parameters = new ArrayList<FormalParameter>();
		
		for(ParameterInfo parameter : fExtractedMethodParameters)
		{
			FormalParameter formalParameter = new FormalParameter(ast);
			
			Expression variable = ast.newVariable(parameter.getParameterName());
			
			if(passByReference(parameter.getParameterName())) {
				variable = ast.newReference(variable);
			}
			
			formalParameter.setParameterName(variable);
			
			if(parameter.getParameterDefaultValue() != null && !parameter.getParameterDefaultValue().isEmpty()) {
				formalParameter.setDefaultValue(ast.newScalar(parameter.getParameterDefaultValue()));
			}
			
			if(parameter.getParameterType() != null && !parameter.getParameterType().isEmpty()) {
				formalParameter.setParameterType(ast.newIdentifier(parameter.getParameterType()));
			}
			
			parameters.add(formalParameter);
		}
			
		return parameters;
	}
	
	private ArrayList<Expression> computeParameters(AST ast)
	{
		ArrayList<Expression> parameters = new ArrayList<Expression>();
		
		for(ParameterInfo parameter : fExtractedMethodParameters)
		{
			Variable variable = ast.newVariable(parameter.getParameterName());
			
			parameters.add(variable);
		}
			
		return parameters;
	}
	
	/**
	 * The extracted method requires an argument iff
	 * 	1. A variable is used in the selected code, and
	 *  2. this variable was used in the code fragment, before the selected code, and
	 *  3. the variable is local (local scope or a parameter for the covering method)
	 * 
	 */
	private void computeRequiredArgumentsForExtractedMethod() {

		ISourceRange selectedRange = getSelectedRange();
		ISourceRange preSelectedRange = getPreSelectedRange();
		
		// Since we're iterating over fMethodVariables (this is gathered by LocalVariableFinder
		// those variables are already local (so 3. is fulfilled)
		// covers 3.
		for(Variable possibleParameter : fMethodVariables) 
		{
			// this covers 1. and for performance reasons, we skipping if the possibleParameter is already a method parameter..
			if(!SourceRangeUtil.covers(selectedRange, possibleParameter) || isMethodParameter(possibleParameter)) {
				continue;
			}

			// covers 2.
			if(isVariableUsedInRange(possibleParameter, preSelectedRange)) {
				addMethodParameter(possibleParameter);
			}
		}
	}

	private void addMethodParameter(Variable variable) {
		// only add a new method parameter, if it wasn't already one...
		if (!isMethodParameter(variable)) {
			fExtractedMethodParameters.add(new ParameterInfo(
					((Identifier) variable.getName()).getName()));
		}
	}

	private boolean isMethodParameter(Variable variable) {
		
		String variableName = ((Identifier) variable.getName()).getName();
		
		for (ParameterInfo parameter : fExtractedMethodParameters) {
			if (parameter.getParameterName().equals(variableName)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * A variable is considered to get returned iff<br>
	 * 	1. the variable is used in the code fragment, after the selected code<br>
	 * 	2. the variable is used in the selected code<br>
	 *  3. the variable is local (local scope or argument for the covering method) <br>
	 *  4. the variable is not an argument which references to an object and it will not get assigned in the selected code<br><br>
	 *  OR
	 *  5. the variable is an argument for the covering method and is passed by reference and is used in the selected code
	 * 
	 * Why 4.?<br>
	 * Because in PHP every argument which is an object, is passed by reference, there is no need
	 * to return this argument back (references are similar to pointers in c++). But if the argument gets a new value assigned the reference will not
	 * stick to the variable. Example:
	 * 
	 * <pre>
	 * $object = new Object(); (1)
	 * $object->doSomething(); (2)
	 * $object = new AnotherObject(); (3)
	 * $object->doSomething(); (4)
	 * </pre>
	 * We can extract line (2), without needing to return $object. But we cannot return lines 2-3 without returning $object, since this would result in 
	 * <pre>
	 *  // original method:
	 * $object = new Object(); (1)
	 * $this->extractedMethod();
	 * $object->doSomething(); (4)
	 * 
	 *  // extracted method:
	 * $object->doSomething(); (2)
	 * $object = new AnotherObject(); (3)
	 * </pre>
	 * which is not the same code! So we have to return $object even though $object is an object.
	 * 
	 */
	private void computeMethodReturnValues() {

		ISourceRange preSelectedRange = getPreSelectedRange();
		ISourceRange postSelectionRange = getPostSelectedRange();
		ISourceRange selectedRange = getSelectedRange();
		
		// again, 3. is fulfilled, since fMethodVariables only contains local variables
		for(Variable var : fMethodVariables)
		{
			// covers 5.
			if (SourceRangeUtil.covers(selectedRange, var)) {
				for (FormalParameter methodParameter : fMethodParameters) {
					Expression parameterName = methodParameter.getParameterName();
					if (parameterName instanceof Reference) {
						if (areTheSameVariables((Variable) ((Reference) parameterName).getExpression(),var)) {
							addMethodReturnValue(var);
							break;
						}
					}
				}
			}
			
			// covers 1. and performance
			if(!SourceRangeUtil.covers(postSelectionRange, var) || isMethodReturnValue(var)) {
				continue;
			}
			// covers 2.
			if(isVariableUsedInRange(var, selectedRange))
			{
				// covers 4.
				if(isVariableUsedInRange(var, preSelectedRange) && isUsedAsObject(var)) {
					continue;
				}
								
				addMethodReturnValue(var);
			}
		}
	}

	private void addMethodReturnValue(Variable var) {
		
		if(!isMethodReturnValue(var)) {
			fExtractedMethodReturnValues.add(new ParameterInfo(((Identifier)var.getName()).getName()));
		}
	}
	
	private boolean isMethodReturnValue(Variable var) {
		
		String variableName = ((Identifier) var.getName()).getName();

		for (ParameterInfo parameter : fExtractedMethodReturnValues) {

			if (parameter.getParameterName().equals(variableName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method checks whether the variable var is an object.
	 * 
	 * This does not imply that it is used as an object the whole time. The following code 
	 * will return true, since we can pass $obj as an parameter and it's reference will be passed
	 * automatically by php..
	 * <pre>
	 *  $obj = "";
	 *  $obj = new Object();
	 *  // here the to be extracted code beginns
	 *  $obj->doSmt();
	 *  // we never assign any value to $obj, we only access it.
	 *  $obj->doSmthMore();
	 *  // here the to be extracted code ends
	 *  </pre>
	 *  
	 * But if we change $obj in the to be extracted code (by creating any new object or assigning any primitiv type) we have to return the state
	 * of $obj at the end of the extracted method, since it's reference was changed.
	 *   
	 */
	private boolean isUsedAsObject(Variable var) {
		
		ISourceRange preSelectedRange = getPreSelectedRange();
		ISourceRange selectedRange = getSelectedRange();
		
		Variable lastObjectAssignment = null;
		
		// first we collect all assignments to var before the selected code.
		// and we take the last one of it, and hope that the last assignment is an object assignment
		for(Assignment assignment : fMethodAssignments)
		{
			// only consider the variables which are used before the selected code!
			if(!SourceRangeUtil.covers(preSelectedRange, assignment)) {
				continue;
			}
			
			try {
				if(!areTheSameVariables(var, (Variable) assignment.getLeftHandSide())) {
					continue;
				}
			} catch(ClassCastException exp) {
				continue;
			}
			
			
			Expression value = assignment.getRightHandSide();
			
			if(value instanceof ClassInstanceCreation) {
				lastObjectAssignment = (Variable) assignment.getLeftHandSide();
			} else {
				lastObjectAssignment = null;
			}
			
		}
		
		// now we know, that the last assignment before the selected code was an object assignment
		if(lastObjectAssignment == null) {
			return false;
		}
		
		// if we assign to this var anything, the reference is destroyed and thus, we have to return the variable in the extracted code
		for(Assignment assignment : fMethodAssignments)
		{
			try {
				if(!areTheSameVariables(var, (Variable) assignment.getLeftHandSide())) {
					continue;
				}
			} catch(ClassCastException exp) {
				continue;
			}
			
			// only consider the variables which are used before the selected code!
			if(!SourceRangeUtil.covers(selectedRange, assignment) ) {
				continue;
			}
			
			return false;
		}
		

		return true;
	}

	private ISourceRange getPostSelectedRange() {
		return fPostSelectedSourceRange;
	}

	private ISourceRange getPreSelectedRange() {
		return fPreSelectedSourceRange;
	}

	private ISourceRange getSelectedRange() {
		return fSelectedSourceRange;
	}
		
	private boolean isVariableUsedInRange(Variable variable, ISourceRange range) {
		
		for(Variable var : fMethodVariables)
		{
			if(!SourceRangeUtil.covers(range, var)) {
				continue;
			}
			
			if(areTheSameVariables(var, variable)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks whether the two variables firstVar and secondVar are the same.
	 * 
	 * Two variables are considered to be the same, if their names are equal.
	 * 
	 * @param firstVar
	 * @param secondVar
	 * @return false if at least one of the given variables is null or their names differ.
	 */
	private boolean areTheSameVariables(Variable firstVar, Variable secondVar)
	{
		if(firstVar == null || secondVar == null) {
			return false;
		}
		
		return ((Identifier) firstVar.getName()).getName().equals( ((Identifier) secondVar.getName()).getName());
	}
		
	public Integer getAccessOfModifiers() {
		return fModifierAccessFlag;
	}
	
	public void setAccessOfModifiers(int access)
	{
		fModifierAccessFlag = access;
	}

	public void setGenerateDocu(boolean value)
	{
		fGenerateDoc = value;
	}
	
	public boolean getGenerateDocu()
	{
		return fGenerateDoc;
	}

	public int getNumberOfDuplicates() {
		return Math.max(fDuplicates.size() - 1, 0);
	}

	public boolean getReplaceDuplicates() {
		return fReplaceDuplicates;
	}
	
	public void setReplaceDuplicates(boolean selection) {
		fReplaceDuplicates = selection;		
	}

	public List<String> getParameterInfos() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	public String getMethodName() {
		return fMethodName;
	}
	
	public boolean getMethodReturnsMultipleVariables()
	{
		return fReturnMultipleVariables;
	}
	
	public void setMethodReturnsMultipleVariables(boolean returnMultiple)
	{
		fReturnMultipleVariables = returnMultiple;
	}
	
	public int getMethodReturnVariablesCount()
	{
		return fExtractedMethodReturnValues.size();
	}

	public void setMethodName(String text) {
		text = text.trim();
		
		if(text.length() == 0) {
			return;
		}
		
		fMethodName = text;
	}

	public String getMethodSignature() {
		
		
		try {
			
			StringReader stringReader = new StringReader(new String());
			ASTParser previewParser = ASTParser.newParser(stringReader, ProjectOptions.getPhpVersion(fSourceModule), false);
			Program previewProgram = previewParser.createAST(null);
			
			previewProgram.recordModifications();
			AST previewAST = previewProgram.getAST();
			
			FunctionDeclaration function = previewAST.newFunctionDeclaration(previewAST.newIdentifier(fMethodName), computeArguments(previewAST), previewAST.newBlock(), false);
			MethodDeclaration method = previewAST.newMethodDeclaration(fModifierAccessFlag, function);
			previewProgram.statements().add(method);
			
			Document myDoc = new Document();
			previewProgram.rewrite(myDoc, null).apply(myDoc);
			
			return myDoc.get().substring(0, myDoc.get().indexOf(METHOD_ARGUMENT_CLOSING_CHAR) + 1);
			
		} catch (Exception e) {
			return RefactoringMessages.ExtractMethodPreviewPage_NoSignaturePreviewAvailable;
		}
	}
	
	private boolean passByReference(String parameterName) {
		
		if(!canPassArgumentsByReference() || (fReturnMultipleVariables && fExtractedMethodReturnValues.size() == 1)) {
			return false;
		}
		
		if(fMustExplicitReturnParameters.size() == 1 && fMustExplicitReturnParameters.get(0).isEqual(parameterName)) {
			fReturnStatement = fMustExplicitReturnParameters.get(0);
			return false;
		}
		
		for(ParameterInfo parameter : fReturnAndArgumentParameters)
		{
			if(parameter.isEqual(parameterName)) {
				
				if((fReturnStatement == null && fMustExplicitReturnParameters.size() < 1) || (fReturnStatement != null && fReturnStatement.isEqual(parameterName)) ) {
					fReturnStatement = parameter;
					return false;
				} else {
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean canPassArgumentsByReference()
	{
		if(!fReturnMultipleVariables || fMustExplicitReturnParameters.size() > 1) {
			return false;
		}
		
		return true;
	}
}
