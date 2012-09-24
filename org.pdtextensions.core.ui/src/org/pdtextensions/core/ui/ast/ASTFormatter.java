package org.pdtextensions.core.ui.ast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
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
import org.eclipse.php.internal.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.ast.nodes.ClassName;
import org.eclipse.php.internal.core.ast.nodes.CloneExpression;
import org.eclipse.php.internal.core.ast.nodes.Comment;
import org.eclipse.php.internal.core.ast.nodes.ConditionalExpression;
import org.eclipse.php.internal.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ContinueStatement;
import org.eclipse.php.internal.core.ast.nodes.DeclareStatement;
import org.eclipse.php.internal.core.ast.nodes.DoStatement;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.EmptyStatement;
import org.eclipse.php.internal.core.ast.nodes.Expression;
import org.eclipse.php.internal.core.ast.nodes.ExpressionStatement;
import org.eclipse.php.internal.core.ast.nodes.FieldAccess;
import org.eclipse.php.internal.core.ast.nodes.FieldsDeclaration;
import org.eclipse.php.internal.core.ast.nodes.ForEachStatement;
import org.eclipse.php.internal.core.ast.nodes.ForStatement;
import org.eclipse.php.internal.core.ast.nodes.FormalParameter;
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
import org.eclipse.php.internal.core.ast.nodes.Statement;
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
import org.eclipse.php.internal.core.ast.nodes.VariableBase;
import org.eclipse.php.internal.core.ast.nodes.WhileStatement;
import org.eclipse.php.internal.core.documentModel.parser.PHPRegionContext;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.pdtextensions.core.ui.OutputBuffer;
import org.pdtextensions.core.ui.PEXUIPlugin;
import org.pdtextensions.core.ui.Token;
import org.pdtextensions.core.ui.TokenHolder;
import org.pdtextensions.core.ui.TokenTypes;
import org.pdtextensions.core.ui.Utils;
import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;
import org.pdtextensions.core.ui.formatter.CodeFormatterOptions;
import org.pdtextensions.core.ui.formatter.align.Alignment;

@SuppressWarnings("restriction")
public class ASTFormatter extends RunThroughVisitor {

	private Program program;
	private TokenHolder holder;
	private OutputBuffer output;
	private CodeFormatterOptions options;

	private int editorTabWidth;

	// global working data
	private int globalOffset = 0;
	private int openTagPosition = 0;
	private int commentPosition = 0;

	// internal flags
	private boolean astErrorDetected = false;
	private boolean incompleteStatement = false;
	private boolean spaceInsertableBeforeSemicolon = true;
	private boolean inHereDoc = false;
	private boolean inArguments = false;
	// since v1.2.1
	private boolean insert_blank_lines_before_class_stopper = false;
	private boolean insert_blank_lines_before_method_stopper = false;

	private class ClassDeclarationData {
		public boolean isFirstProperty = true;
	}

	private class ArrayCreationData {
		public boolean useMaxKeyLength = false;
		public int maxKeyLength = 0;
	}

	private class BlockData {
		public String options_brace_position = CodeFormatterConstants.END_OF_LINE;
		public boolean options_insert_space_before_opening_brace = true;
		public boolean options_indent_body = true;
	}

	private class MethodInvocationData {
		public int count = 0;
		public int depth = 0;
		public int wrap = 0;
		public boolean wrapIndent = false;
		public int markedPosition = 0;
	}

	private Stack<ClassDeclarationData> classDeclarationStack;
	private Stack<ArrayCreationData> arrayCreationStack;
	private Stack<BlockData> blockDataStack;
	private Stack<MethodInvocationData> methodInvocationStack;

	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public ASTFormatter(Program program, TokenHolder holder,
			CodeFormatterOptions options) {
		this.program = program;
		this.holder = holder;
		if (options == null) {
			IProject project = this.holder.getProject();
			Map optionsMap = PEXUIPlugin.getDefault().getOptions(project);
			this.options = new CodeFormatterOptions(optionsMap);
		} else {
			this.options = options;
		}
		this.options.line_separator = TextUtilities
				.getDefaultLineDelimiter(this.holder.getDocument());
		output = new OutputBuffer(this.options);
		classDeclarationStack = new Stack<ASTFormatter.ClassDeclarationData>();
		arrayCreationStack = new Stack<ASTFormatter.ArrayCreationData>();
		blockDataStack = new Stack<ASTFormatter.BlockData>();
		methodInvocationStack = new Stack<ASTFormatter.MethodInvocationData>();
		editorTabWidth = EditorsUI
				.getPreferenceStore()
				.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	/**
	 * 
	 */
	public String format() {
		program.accept(this);
		if (astErrorDetected) {
			return null;
		}
		return output.getOutput();
	}

	private void walk(int start, int end) {
		if (start >= end) {
			return;
		}
		boolean inCommentBlock = false;
		int countCrLfBorder = 1;
		StringBuffer comment = new StringBuffer();
		int alignOpenTag = -1;

		Token[] tokens = holder.getTokens(start, end);
		boolean isFirst = true;
		for (Token token : tokens) {
			if (alignOpenTag >= 0) {
				output.setLineLeader(alignOpenTag);
				alignOpenTag = -1;
			}
			String text = holder.getText(token);
			int insertNewLine = 0;
			if (isFirst) {
				int diff;
				if ((diff = start - token.getStart()) > 0) {
					text = text.substring(diff);
				}
				if ((diff = token.getEnd() - end) > 0) {
					text = text.substring(0, text.length() - diff);
				}
				isFirst = false;
			} else if (token.getEnd() > end && end > token.getStart()) {
				text = text.substring(0, end - token.getStart());
			}

			if (inCommentBlock) {
				if (token.isPhpToken()) {
					switch (token.getTokenType()) {
					case PHP_COMMENT:
					case PHPDOC_COMMENT:
						comment.append(text);
						break;
					case PHP_COMMENT_END:
					case PHPDOC_COMMENT_END:
						comment.append(text);
						inCommentBlock = false;
						countCrLfBorder = 0;
						String str = Utils.convertCrLf(comment.toString());
						String[] strings = str.split("\\n");
						String leader = "";
						int pos = output.getPosition();
						switch (options.tab_char) {
						case CodeFormatterOptions.TAB:
							if (options.tab_size > 0) {
								leader = output.tabs(pos / options.tab_size)
										+ output.spaces(pos % options.tab_size);
							} else {
								leader = output.spaces(pos);
							}
							break;
						case CodeFormatterOptions.SPACE:
							leader = output.spaces(pos);
							break;
						}
						for (int i = 0; i < strings.length; i++) {
							String s = null;
							String w = Utils.convertTabsToSpaces(strings[i],
									editorTabWidth, true);
							if (w.length() > commentPosition) {
								String z = w.substring(0, commentPosition);
								if (z.trim().equals("")) {
									s = w.substring(commentPosition);
									z = Utils.ltrim(s);
									if (z.startsWith("*")) {
										s = " " + z;
									}
								}
							}
							if (s == null) {
								s = Utils.ltrim(strings[i]);
								if (s.startsWith("*")) {
									s = " " + s;
								}
							}
							if (i > 0 && pos > 0) {
								output.appendRaw(leader + s, false);
							} else {
								output.append(s);
							}
							if (i < strings.length - 1) {
								output.newLine(true);
							}
						}
						insertSpaceAfterComment(token.getEnd());
						comment.setLength(0);
						break;
					default:
						if (token.isPHPDoc()) {
							comment.append(text);
						} else {
							PEXUIPlugin.log(IStatus.WARNING,
									"Unexpected token detected");
						}
					}
				} else {
					output.append(text);
				}
				continue;
			}

			if (token.isPhpToken()) {
				switch (token.getTokenType()) {
				case PHPDOC_COMMENT_START:
					// begin v1.2.1
					if (options.blank_lines_before_first_class_body_declaration > 0
							|| options.blank_lines_before_method > 0) {
						ASTNode node = ASTFinder.findNode(program,
								token.getStart());
						if (node != null) {
							int offset = node.getEnd();
							Token nextToken = holder.getToken(offset);
							while (nextToken != null
									&& nextToken.getTokenType() == TokenTypes.WHITESPACE) {
								nextToken = holder.getToken(nextToken.getEnd());
							}
							if (nextToken != null) {
								node = ASTFinder.findNode(program,
										nextToken.getStart());
								if (node != null) {
									switch (node.getType()) {
									case ASTNode.CLASS_DECLARATION:
										if (options.blank_lines_before_first_class_body_declaration > 0) { // 代用
											output.blankLines(options.blank_lines_before_first_class_body_declaration);
											insert_blank_lines_before_class_stopper = true;
										}
										break;
									case ASTNode.FUNCTION_DECLARATION:
										if (options.blank_lines_before_method > 0) {
											output.blankLines(options.blank_lines_before_method);
											insert_blank_lines_before_method_stopper = true;
										}
										break;
									}
								}
							}
						}
					}
					// fall through the next case block
					// end v1.2.1
				case PHP_COMMENT_START:
					commentPosition = 0;
					String line = holder.getLineLeader(token.getStart());
					String conv = Utils.convertTabsToSpaces(line,
							editorTabWidth, false);
					commentPosition = conv.length();
					insertSpaceBeforeComment(token.getStart() - 1);
					inCommentBlock = true;
					comment.setLength(0);
					comment.append(text);
					continue;
				}
				if (Utils.hasCrLf(text)) {
					int count = Utils.countCrLf(text);
					if (count > countCrLfBorder) {
						insertNewLine = count - countCrLfBorder;
					}
				}
				countCrLfBorder = 1; // reset

				switch (token.getTokenType()) {
				case WHITESPACE:
					text = text.trim();
					break;
				case PHP_ENCAPSED_AND_WHITESPACE:
					break;
				case PHP_HEREDOC_TAG:
					Pattern pattern = Pattern.compile(
							"<<<(\\s)*(.*?)([\\n\\r])*", Pattern.DOTALL);
					Matcher matcher = pattern.matcher(text);
					if (matcher.matches()) {
						String spc = (matcher.group(1) == null) ? "" : " ";
						String hereDocTag = "<<<" + spc + matcher.group(2);
						output.append(hereDocTag);
						if (matcher.group(3) != null) {
							output.newLine(); // CAUTION
						}
						text = "";
					}
					break;
				case PHP_LINE_COMMENT:
					String s = holder.getText(token).trim();
					if (s.equals("//") || s.equals("#")) {
						if (holder.getLineLeader(token.getStart()).trim()
								.equals("")) {
							output.newLine(); // CAUTION
						} else {
							insertSpaceBeforeComment(token.getStart() - 1);
						}
					}
					if (text.endsWith("\r\n")) {
						text = text.substring(0, text.length() - 2);
						insertNewLine = 1;
						countCrLfBorder = 0;
					} else if (text.endsWith("\n")) {
						text = text.substring(0, text.length() - 1);
						insertNewLine = 1;
						countCrLfBorder = 0;
					} else if (text.endsWith("\r")) {
						text = text.substring(0, text.length() - 1);
						insertNewLine = 1;
						countCrLfBorder = 0;
						Token next = holder.getToken(token.getEnd());
						if (next != null
								&& next.getTokenType().equals(
										TokenTypes.WHITESPACE)) {
							if (holder.getText(next).startsWith("\n")) {
								countCrLfBorder = 1;
							}
						}
					}
					break;
				case PHP_CATCH:
				case PHP_CONSTANT_ENCAPSED_STRING:
				case PHP_DO:
				case PHP_ECHO:
				case PHP_ELSEIF:
				case PHP_EXTENDS:
				case PHP_FOR:
				case PHP_FOREACH:
				case PHP_FUNCTION:
				case PHP_IF:
				case PHP_IMPLEMENTS:
				case PHP_OBJECT_OPERATOR:
				case PHP_OPERATOR:
				case PHP_PRINT:
				case PHP_RETURN:
				case PHP_SELF:
				case PHP_SWITCH:
				case PHP_THROW:
				case PHP_TOKEN:
				case PHP_TRY:
				case PHP_WHILE:
					text = text.trim();
					break;
				case PHP_ABSTRACT:
				case PHP_CASE:
				case PHP_CLASS:
				case PHP_CLONE:
				case PHP_CONST:
				case PHP_FINAL:
				case PHP_GLOBAL:
				case PHP_GOTO:
				case PHP_INTERFACE:
				case PHP_NAMESPACE:
				case PHP_NEW:
				case PHP_PUBLIC:
				case PHP_PROTECTED:
				case PHP_PRIVATE:
				case PHP_STATIC:
				case PHP_USE:
				case PHP_TRAIT:
				case PHP_INSTEADOF:
				case PHP_VAR:
					text = text.trim() + " ";
					break;
				case PHP_AS:
				case PHP_INSTANCEOF:
					text = " " + text.trim() + " ";
					break;
				case PHP_SEMICOLON:
					text = text.trim();
					if (text.equals(";") && spaceInsertableBeforeSemicolon
							&& options.insert_space_before_semicolon) {
						text = " ;";
					}
					break;
				default:
					if (token.isPHPDoc()) {
						break;
					}
					text = text.trim();
				}

				output.append(text);
				if (insertNewLine > 0) {
					while (insertNewLine-- > 0) {
						output.newLine(true);
					}
				}
			} else {
				if (token.getType().equals(PHPRegionContext.PHP_OPEN)) {
					int offset = token.getStart();
					String gap = holder.getLineLeader(offset).replaceAll("\\t",
							output.spaces(options.tab_size));
					if (options.align_php_region_with_open_tag) {
						alignOpenTag = gap.length();
					}
					openTagPosition = gap.length();
					Token nextToken = holder.getToken(token.getEnd());
					if (nextToken != null) {
						if (nextToken.getTokenType().equals(
								TokenTypes.WHITESPACE)) {
							if (Utils.countCrLf(holder.getText(nextToken)) == 0) {
								text = text + " ";
							} else {
								nextToken = holder.getToken(nextToken.getEnd());
								if (nextToken == null) {
									text = text + "\n";
								}
								countCrLfBorder = 0;
							}
						}
					}
				} else if (token.getType().equals(PHPRegionContext.PHP_CLOSE)) {
					incompleteStatement = true;
					boolean inLine = false;
					try {
						Token[] tokenInLine = holder.getTokens(
								holder.getLineOffset(token.getStart()),
								token.getEnd());
						for (Token tkn : tokenInLine) {
							if (tkn.getType().equals(PHPRegionContext.PHP_OPEN)) {
								inLine = true;
								break;
							}
						}
					} catch (BadLocationException e) {
						PEXUIPlugin.log(e);
					}
					if (inLine) {
						output.join(text);
					} else {
						output.setLineLeader(openTagPosition);
						output.appendToLeader(text);
					}
					text = "";
					alignOpenTag = 0;
				
				}
				output.appendRaw(text, false);
				
			}
		}
	}

	private int visit(List<Statement> statements, int offset, int end) {
		for (Statement statement : statements) {
			walk(offset, statement.getStart());
			statement.accept(this);
			offset = Math.max(statement.getEnd(), globalOffset);
			switch (statement.getType()) {
			case ASTNode.BREAK_STATEMENT:
			case ASTNode.CONTINUE_STATEMENT:
			case ASTNode.DECLARE_STATEMENT:
			case ASTNode.DO_STATEMENT:
			case ASTNode.ECHO_STATEMENT:
			case ASTNode.EMPTY_STATEMENT:
			case ASTNode.EXPRESSION_STATEMENT:
			case ASTNode.FOR_EACH_STATEMENT:
			case ASTNode.FOR_STATEMENT:
			case ASTNode.GLOBAL_STATEMENT:
			case ASTNode.IF_STATEMENT:
			case ASTNode.RETURN_STATEMENT:
			case ASTNode.STATIC_STATEMENT:
			case ASTNode.SWITCH_STATEMENT:
			case ASTNode.THROW_STATEMENT:
			case ASTNode.TRY_STATEMENT:
			case ASTNode.USE_STATEMENT:
			case ASTNode.WHILE_STATEMENT:
			case ASTNode.GOTO_STATEMENT:
			case ASTNode.CLASS_DECLARATION:
			case ASTNode.CONSTANT_DECLARATION:
			case ASTNode.FIELD_DECLARATION:
			case ASTNode.FUNCTION_DECLARATION:
			case ASTNode.INTERFACE_DECLARATION:
			case ASTNode.METHOD_DECLARATION:
			case ASTNode.SINGLE_FIELD_DECLARATION:
			case ASTNode.LAMBDA_FUNCTION_DECLARATION:
			case ASTNode.BLOCK:
			case ASTNode.COMMENT:
			case ASTNode.NAMESPACE:
			case ASTNode.SWITCH_CASE:
			case ASTNode.TRAIT_USE_STATEMENT:
				break;
			case ASTNode.GOTO_LABEL:
			case ASTNode.IN_LINE_HTML:
				incompleteStatement = true;
				break;
			case ASTNode.AST_ERROR:
				astErrorDetected = true;
				break;
			default:
				PEXUIPlugin.log(IStatus.ERROR, "Unexpected ASTNode:\n"
						+ statement.toString());
			}
			if (!incompleteStatement) {
				offset = trailer(offset);
				output.newLine();
			} else {
				incompleteStatement = false;
			}
		}
		return offset;
	}

	private Token findToken(int start, int end, TokenTypes type, String text) {
		Token[] tokens = holder.getTokens(start, end);
		for (Token token : tokens) {
			if (token.getTokenType().equals(type)) {
				if (text == null) {
					return token;
				}
				String tokenText = holder.getText(token);
				int diff;
				if ((diff = start - token.getStart()) > 0) {
					tokenText = tokenText.substring(diff);
				}
				if (tokenText.length() < text.length()) {
					continue;
				}
				tokenText = tokenText.substring(0, text.length());
				if (tokenText.equalsIgnoreCase(text)) {
					return token;
				}
			}
		}
		return null;
	}

	private Token findToken(int start, int end, TokenTypes type) {
		return findToken(start, end, type, null);
	}

	private Token findLastToken(int start, int end, TokenTypes type) {
		Token[] tokens = holder.getTokens(start, end);
		for (int i = tokens.length - 1; i >= 0; i--) {
			if (tokens[i].getTokenType().equals(type)) {
				return tokens[i];
			}
		}
		return null;
	}

	private int trailer(int offset) {
		globalOffset = offset;
		Token token = holder.getToken(offset);
		if (token == null) {
			return offset;
		}
		String text = holder.getText(token);
		int diff = offset - token.getStart();
		if (diff > 0) {
			text = text.substring(diff);
		}
		if (text.matches("[ \\t]*")) {
			offset = token.getEnd();
			token = holder.getNextToken(token.getTokenIndex());
		}
		boolean found = false;
		Token last = null;
		Token stopper = null;
		boolean stop = false;

		while (token != null && !stop) {
			switch (token.getTokenType()) {
			case PHPDOC_COMMENT_START:
				found = true;
				while (token != null
						&& token.getTokenType() != TokenTypes.PHPDOC_COMMENT_END) {
					last = token;
					token = holder.getNextToken(token.getTokenIndex());
				}
				continue;
			case PHP_COMMENT_START:
				found = true;
				while (token != null
						&& token.getTokenType() != TokenTypes.PHP_COMMENT_END) {
					last = token;
					token = holder.getNextToken(token.getTokenIndex());
				}
				continue;
			case PHP_LINE_COMMENT:
				found = true;
			case PHPDOC_COMMENT_END:
			case PHP_COMMENT_END:
			case WHITESPACE:
			case PHPDOC_TODO:
				if (Utils.hasCrLf(text)) {
					stopper = token;
					stop = true;
					break;
				}
				break;
			default:
				stopper = last;
				stop = true;
			}
			if (!stop) {
				last = token;
				token = holder.getNextToken(token.getTokenIndex());
			}
		}

		if (found) {
			if (stopper == null) {
				stopper = last;
			}
			int ends = stopper.getEnd();
			walk(offset, ends);
			offset = ends;
			if (stopper.getTokenType().equals(TokenTypes.PHP_LINE_COMMENT)) {
				if (holder.getText(stopper).endsWith("\r")) {
					token = holder.getToken(offset);
					if (token != null
							&& token.getTokenType().equals(
									TokenTypes.WHITESPACE)) {
						if (holder.getText(token).startsWith("\n")) {
							offset++;
						}
					}
				}
			}
		}

		globalOffset = offset;
		return offset;
	}

	private void insertSpaceBeforeComment(int offset) {
		if (output.isNewLine()) {
			return;
		}
		if (offset >= 0) {
			try {
				char ch = holder.getDocument().getChar(offset);
				if (ch == ' ' || ch == '\t') {
					output.space();
				}
			} catch (BadLocationException e) {
				PEXUIPlugin.log(e);
			}
		}
	}

	private void insertSpaceAfterComment(int offset) {
		if (output.isNewLine()) {
			return;
		}
		IDocument document = holder.getDocument();
		if (offset < document.getLength()) {
			try {
				char ch = document.getChar(offset);
				if (ch == ' ' || ch == '\t') {
					output.space();
				}
			} catch (BadLocationException e) {
				PEXUIPlugin.log(e);
			}
		}
	}

	private int getWrapStyle(int bits) {
		int wrapStyle = bits
				& (Alignment.M_COMPACT_SPLIT
						| Alignment.M_COMPACT_FIRST_BREAK_SPLIT
						| Alignment.M_ONE_PER_LINE_SPLIT
						| Alignment.M_NEXT_SHIFTED_SPLIT | Alignment.M_NEXT_PER_LINE_SPLIT);
		return wrapStyle;
	}

	private int getIndentStyle(int bits) {
		int indentStyle = bits
				& (Alignment.M_INDENT_BY_ONE | Alignment.M_INDENT_ON_COLUMN);
		return indentStyle;
	}

	private boolean isForceSplit(int bits) {
		return (bits & Alignment.M_FORCE) != 0;
	}

	private int boolDigit(boolean bool) {
		return bool ? 1 : 0;
	}

	@Override
	public boolean visit(TraitUseStatement node) {

		/*
		List<TraitStatement> tsList = node.getTsList();
		
		// TODO: implement TraitPrecedenceStatement 
		// currently trait use statements can only be formatted without precedences
		if (node.getTsList().size() > 0) {
			node.childrenAccept(this);
			return false;
		}
		*/
		
		walk(node.getStart(), node.getEnd());
		return false;
	}
	
	/*
	@Override
	public boolean visit(DereferenceNode node) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(ChainingInstanceCall node) {
		// TODO Auto-generated method stub
		System.err.println("chaining instance call");
		return false;
	}
	
	@Override
	public boolean visit(PHPArrayDereferenceList node) {
		// TODO Auto-generated method stub
		System.err.println("php array dereferenece list");
		return false;
	}
	*/
	
	@Override
	public boolean visit(TraitAlias node) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(TraitAliasStatement node) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(TraitPrecedence node) {
		// TODO Auto-generated method stub
		node.childrenAccept(this);
		return false;
	}
	
	@Override
	public boolean visit(TraitPrecedenceStatement node) {
		node.childrenAccept(this);
		return false;
	}
	
	/*
	@Override
	public boolean visit(FullyQualifiedTraitMethodReference node) {
		// TODO Auto-generated method stub
		
		NamespaceName className = node.getClassName();
		Identifier name = node.getFunctionName();

		System.err.println(node.getStart());
		walk(node.getStart(), className.getStart());
		
		className.accept(this);
		
		int offset = className.getEnd();
		
		walk(offset, name.getStart());
		name.accept(this);
		offset = name.getEnd();
		
		walk(offset, node.getEnd());
		
//		System.err.println(className);
//		System.err.println("fqtmr");
		
		return false;
	}
	*/
	protected void visitClassOrTrait(ClassDeclaration classDeclaration) {
		
		Identifier name = classDeclaration.getName();
		Expression superClass = classDeclaration.getSuperClass();
		List<Identifier> interfaces = classDeclaration.interfaces();
		Block body = classDeclaration.getBody();

		classDeclarationStack.push(new ClassDeclarationData());

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_type_declaration;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_type_declaration;
		blockData.options_indent_body = options.indent_body_declarations_compare_to_type_header;
		blockDataStack.push(blockData);

		if (insert_blank_lines_before_class_stopper) {
			insert_blank_lines_before_class_stopper = false;
		} else {
			if (options.blank_lines_before_first_class_body_declaration > 0) { // 代用
				output.blankLines(options.blank_lines_before_first_class_body_declaration);
			}
		}
		walk(classDeclaration.getStart(), name.getStart()); // "class"
		name.accept(this);
		int offset = name.getEnd();

		if (superClass != null) {
			int wrapStyle = getWrapStyle(options.alignment_for_superclass_in_type_declaration);
			int indentStyle = getIndentStyle(options.alignment_for_superclass_in_type_declaration);
			boolean forceSplit = isForceSplit(options.alignment_for_superclass_in_type_declaration);
			boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
			int wrap = 0;
			boolean spaceRequired = true;
			boolean wrapped = false;

			if (split && !forceSplit) {
				int width = output.getPosition() + " extends ".length()
						+ superClass.getLength();
				split = width > options.page_width;
			}

			int end = superClass.getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_EXTENDS);
			if (token != null) {
				walk(offset, token.getStart());
				offset = token.getStart();
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						int width = output.getPosition() + "extends ".length();
						if (forceSplit || width > options.page_width) {
							output.newLine();
							spaceRequired = false;
							wrapped = true;
						}
						break;
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						// do not wrap here
						break;
					case Alignment.M_NEXT_SHIFTED_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
						output.newLine();
						spaceRequired = false;
						wrapped = true;
						break;
					}
				}
				output.spaceIf(spaceRequired);
				if (split && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
					output.markPosition();
				}
				walk(offset, token.getEnd()); // "extends"
				offset = token.getEnd();
				spaceRequired = true;
			} else {
				PEXUIPlugin.warning("extends", classDeclaration, offset,
						end);
			}
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition() + 1
							+ superClass.getLength();
					if (width > options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					if (wrapped) {
						wrap++;
						output.wrap(+1);
					}
					// non-break
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				}
			}
			output.spaceIf(spaceRequired);
			walk(offset, superClass.getStart());
			superClass.accept(this);
			offset = superClass.getEnd();
			if (split) {
				if (wrap > 0) {
					output.wrap(-wrap);
				}
				output.markPosition(0);
			}
			spaceRequired = true;
		}

		if (!interfaces.isEmpty()) {
			int wrapStyle = getWrapStyle(options.alignment_for_superinterfaces_in_type_declaration);
			int indentStyle = getIndentStyle(options.alignment_for_superinterfaces_in_type_declaration);
			boolean forceSplit = isForceSplit(options.alignment_for_superinterfaces_in_type_declaration);
			boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
			int wrap = 0;
			int wrapDelta = 1
					+ boolDigit(options.insert_space_before_comma_in_superinterfaces)
					+ boolDigit(options.insert_space_after_comma_in_superinterfaces);
			boolean spaceRequired = true;
			boolean wrapIndent = false;
			boolean wrapped = false;

			if (split && !forceSplit) {
				int width = output.getPosition() + "implements ".length();
				for (Identifier identifier : interfaces) {
					width += identifier.getLength();
				}
				if (interfaces.size() > 1) {
					width += (interfaces.size() - 1) * wrapDelta;
				}
				split = width > options.page_width;
			}

			int end = interfaces.get(0).getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_IMPLEMENTS);
			if (token != null) {
				walk(offset, token.getStart());
				offset = token.getStart();
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						int width = output.getPosition()
								+ "implements ".length();
						if (forceSplit || width > options.page_width) {
							output.newLine();
							spaceRequired = false;
							wrapped = true;
						}
						break;
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						// do not wrap here
						break;
					case Alignment.M_NEXT_SHIFTED_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
						output.newLine();
						spaceRequired = false;
						wrapped = true;
						break;
					}
				}
				output.spaceIf(spaceRequired);
				if (split && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
					output.markPosition();
				}
				walk(offset, token.getEnd()); // "implements"
				offset = token.getEnd();
				spaceRequired = true;
			} else {
				PEXUIPlugin.warning("implements", classDeclaration, offset,
						end);
			}

			for (Identifier identifier : interfaces) {
				end = identifier.getStart();
				token = findToken(offset, end, TokenTypes.PHP_TOKEN, ",");
				if (token != null) {
					walk(offset, token.getStart()); // ""
					output.spaceIf(options.insert_space_before_comma_in_superinterfaces);
					output.append(",");
					spaceRequired = options.insert_space_after_comma_in_superinterfaces;
					if (split) {
						switch (wrapStyle) {
						case Alignment.M_NEXT_SHIFTED_SPLIT:
							if (!wrapIndent) {
								wrap++;
								output.wrap(+1);
								wrapIndent = true;
							}
							break;
						}
					}
					offset = token.getEnd();
				} else {
					// first [second to "implements"] element
					if (split) {
						switch (wrapStyle) {
						case Alignment.M_NEXT_SHIFTED_SPLIT:
							if (wrapped) {
								wrap++;
								output.wrap(+1);
								wrapIndent = true;
							}
							break;
						}
					}
				}
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						int width = output.getPosition()
								+ identifier.getLength() + wrapDelta;
						if (width > options.page_width) {
							output.newLine();
							spaceRequired = false;
						}
						break;
					case Alignment.M_NEXT_SHIFTED_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						output.newLine();
						spaceRequired = false;
						break;
					}
				}
				output.spaceIf(spaceRequired);
				walk(offset, identifier.getStart());
				identifier.accept(this);
				offset = identifier.getEnd();
			}
			if (split) {
				if (wrap > 0) {
					output.wrap(-wrap);
				}
				output.markPosition(0);
			}
		}

		walk(offset, body.getStart()); // ""
		body.accept(this);
		walk(body.getEnd(), classDeclaration.getEnd()); // ""

		classDeclarationStack.pop();
		blockDataStack.pop();
		
		
	}
	
	@Override
	public boolean visit(TraitDeclaration classDeclaration) {
		visitClassOrTrait(classDeclaration);
		return false;
	}
	
	@Override
	public boolean visit(ArrayAccess arrayAccess) {
		VariableBase name = arrayAccess.getName();
		Expression index = arrayAccess.getIndex();

		walk(arrayAccess.getStart(), name.getStart()); // ""
		name.accept(this);

		int offset = name.getEnd();
		int end = arrayAccess.getEnd();
		String[] arrayChar;
		Token token;
		if (arrayAccess.getArrayType() == ArrayAccess.VARIABLE_ARRAY) {
			arrayChar = new String[] { "[", "]" };
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, "[");
		} else {
			arrayChar = new String[] { "{", "}" };
			token = findToken(offset, end, TokenTypes.PHP_CURLY_OPEN);
		}
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_opening_bracket_in_array_reference);
			output.append(arrayChar[0]);
			output.spaceIf(options.insert_space_after_opening_bracket_in_array_reference);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(arrayChar[0], arrayAccess, offset, end);
		}

		if (index != null) {
			walk(offset, index.getStart()); // ""
			index.accept(this);
			offset = index.getEnd();
		}

		end = arrayAccess.getEnd();
		if (arrayAccess.getArrayType() == ArrayAccess.VARIABLE_ARRAY) {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, "]");
		} else {
			token = findToken(offset, end, TokenTypes.PHP_CURLY_CLOSE);
		}
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_bracket_in_array_reference);
			output.append(arrayChar[1]);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(arrayChar[1], arrayAccess, offset, end);
		}
		walk(offset, end); // ""
		return false;
	}

	@Override
	public boolean visit(ArrayCreation arrayCreation) {
		List<ArrayElement> elements = arrayCreation.elements();

		int wrapStyle = getWrapStyle(options.alignment_for_expressions_in_array_initializer);
		int indentStyle = getIndentStyle(options.alignment_for_expressions_in_array_initializer);
		boolean forceSplit = isForceSplit(options.alignment_for_expressions_in_array_initializer);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_comma_in_array_initializer)
				+ boolDigit(options.insert_space_after_comma_in_array_initializer);
		boolean spaceRequired = false;
		boolean wrapIndent = false;
		int markedPosition = 0;

		if (inArguments) {
			split = split & options.wrap_array_in_arguments;
		}

		ArrayCreationData arrayCreationData = new ArrayCreationData();
		if (split) {
			int keyLength = 0;
			switch (wrapStyle) {
			case Alignment.M_ONE_PER_LINE_SPLIT:
			case Alignment.M_NEXT_PER_LINE_SPLIT:
			case Alignment.M_NEXT_SHIFTED_SPLIT:
				for (ArrayElement element : elements) {
					Expression key = element.getKey();
					if (key != null) {
						if (keyLength < key.getLength()) {
							keyLength = key.getLength();
						}
					}
				}
				arrayCreationData.maxKeyLength = keyLength;
				break;
			}
		}
		arrayCreationStack.push(arrayCreationData);

		int startLine = output.getLineNumber();
		int indent = 0;
		int offset = arrayCreation.getStart();
		int end = !elements.isEmpty() ? elements.get(0).getStart()
				: arrayCreation.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "array"
			output.spaceIf(options.insert_space_before_opening_brace_in_array_initializer); // 代用
			output.append("(");
			if (options.insert_new_line_after_opening_brace_in_array_initializer // 代用
					&& !elements.isEmpty()
					&& (inArguments ? options.insert_new_line_after_opening_brace_in_array_initializer_in_arguments
							: true)) {
				output.indent(+1);
				indent = 1;
				output.newLine();
			} else if (!elements.isEmpty()) {
				output.spaceIf(options.insert_space_after_opening_brace_in_array_initializer); // 代用
			}
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", arrayCreation, offset, end);
		}

		if (elements.isEmpty()
				&& options.insert_space_between_empty_braces_in_array_initializer) {
			output.space();
		}

		if (split && !forceSplit) {
			int width = output.getPosition();
			for (ArrayElement element : elements) {
				width += element.getLength();
			}
			if (elements.size() > 1) {
				width += (elements.size() - 1) * wrapDelta;
			}
			split = (width + 1) > options.page_width;
		}

		boolean isFirst = true;
		for (ArrayElement element : elements) {
			token = findToken(offset, element.getStart(), TokenTypes.PHP_TOKEN,
					",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_array_initializer);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_array_initializer;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				spaceRequired = false;
				if (split) {
					wrap = indentStyle == Alignment.M_INDENT_BY_ONE ? 1
							: options.continuation_indentation_for_array_initializer;
					if (wrap >= indent) {
						output.indent(-indent);
						indent = 0;
					}
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
						}
						break;
					}
				}
			}
			if (split) {
				int width = output.getPosition() + element.getLength()
						+ wrapDelta;
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					if (width >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					arrayCreationData.useMaxKeyLength = true;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					arrayCreationData.useMaxKeyLength = true;
					break;
				}
			}
			output.spaceIf(spaceRequired);
			walk(offset, element.getStart()); // ""
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
			}
			element.accept(this);
			offset = element.getEnd();
			isFirst = false;
		}
		if (indent > 0) {
			output.indent(-indent);
		}
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			output.markPosition(markedPosition);
		}

		end = arrayCreation.getEnd();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			if (options.insert_new_line_before_closing_brace_in_array_initializer // 代用
					&& startLine != output.getLineNumber()) {
				output.newLine();
			} else if (!elements.isEmpty()) {
				output.spaceIf(options.insert_space_before_closing_brace_in_array_initializer); // 代用
			}
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", arrayCreation, offset, end);
		}
		walk(offset, end); // ";"

		arrayCreationStack.pop();
		return false;
	}

	@Override
	public boolean visit(ArrayElement arrayElement) {
		Expression key = arrayElement.getKey();
		Expression value = arrayElement.getValue();

		int offset = arrayElement.getStart();
		if (key != null) {
			walk(offset, key.getStart()); // ""
			key.accept(this);
			offset = key.getEnd();

			int end = value.getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_OPERATOR, "=>");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_double_arrow_operator);
				if (options.insert_space_before_double_arrow_operator_with_filler) {
					if (!arrayCreationStack.isEmpty()
							&& arrayCreationStack.peek().useMaxKeyLength) {
						int diff = arrayCreationStack.peek().maxKeyLength
								- key.getLength();
						if (diff > 0) {
							output.append(output.spaces(diff));
						}
					}
				}
				output.append("=>");
				output.spaceIf(options.insert_space_after_double_arrow_operator);
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("=>", arrayElement, offset, end);
			}
		}

		walk(offset, value.getStart()); // ""
		value.accept(this);
		walk(value.getEnd(), arrayElement.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Assignment assignment) {
		VariableBase left = assignment.getLeftHandSide();
		Expression right = assignment.getRightHandSide();

		int wrapStyle = getWrapStyle(options.alignment_for_assignment);
		int indentStyle = getIndentStyle(options.alignment_for_assignment);
		boolean forceSplit = isForceSplit(options.alignment_for_assignment);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_assignment_operator)
				+ boolDigit(options.insert_space_after_assignment_operator);
		boolean spaceRequired = false;

		walk(assignment.getStart(), left.getStart()); // ""

		left.accept(this);

		int offset = left.getEnd();
		int end = right.getStart();
		String op = assignment.getOperationString();
		Token token;
		if (op.equals("=")) {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, op);
		} else {
			token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
		}
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_assignment_operator);
			output.append(op); // "=" "+=" ".=" ...
			spaceRequired = options.insert_space_after_assignment_operator;
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					if (forceSplit) {
						output.newLine();
						spaceRequired = false;
					} else if (output.getPosition() + right.getLength()
							+ wrapDelta >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, assignment, offset, end);
		}
		walk(offset, right.getStart()); // ""

		right.accept(this);

		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
		}
		walk(right.getEnd(), assignment.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(BackTickExpression backTickExpression) {
		List<Expression> expressions = backTickExpression.expressions();

		int offset = backTickExpression.getStart();
		int count = 0;
		for (Expression expression : expressions) {
			walk(offset, expression.getStart()); // "`" ?
			if (count++ > 0) {
				output.space();
			}
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, backTickExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Block block) {
		if (block.isCurly()) {
			curlyBlock(block);
		} else {
			switch (block.getParent().getType()) {
			case ASTNode.FOR_STATEMENT:
			case ASTNode.FOR_EACH_STATEMENT:
			case ASTNode.IF_STATEMENT:
			case ASTNode.SWITCH_STATEMENT:
			case ASTNode.WHILE_STATEMENT:
			case ASTNode.DECLARE_STATEMENT:
				colonBlock(block);
				break;
			case ASTNode.NAMESPACE: // NamespaceDeclaration
				output.newLine();
				
				if (options.insert_new_line_after_namespace_declaration) {
					output.newLine(true);
				}
				
				visit(block.statements(), block.getStart(), block.getEnd());
				break;
			default:
				PEXUIPlugin.log(IStatus.ERROR, "Unexpected ASTNode:\n"
						+ block.toString());
			}
		}
		return false;
	}

	private boolean curlyBlock(Block block) {
		List<Statement> statements = block.statements();
		BlockData blockData = null;
		if (!blockDataStack.isEmpty()) {
			blockData = blockDataStack.peek();
		}

		int parentType = block.getParent().getType();
		boolean indentBody = options.indent_statements_compare_to_block;
		int offset = block.getStart();
		int end = (!statements.isEmpty()) ? statements.get(0).getStart()
				: block.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_CURLY_OPEN);
		if (token != null) {
			walk(offset, token.getStart()); // ""
			if (blockData != null) {
				if (blockData.options_brace_position
						.equals(CodeFormatterConstants.NEXT_LINE)) {
					output.newLine();
				} else if (blockData.options_brace_position
						.equals(CodeFormatterConstants.NEXT_LINE_SHIFTED)) {
					output.newLine();
					output.indent(1);
				} else {
					output.spaceIf(blockData.options_insert_space_before_opening_brace);
				}
				indentBody = blockData.options_indent_body;
			} else {
				switch (parentType) {
				case ASTNode.PROGRAM:
				case ASTNode.BLOCK:
					break;
				default:
					if (options.brace_position_for_block
							.equals(CodeFormatterConstants.NEXT_LINE)) {
						output.newLine();
					} else {
						output.spaceIf(options.insert_space_before_opening_brace_in_block);
					}
					break;
				}
			}
			output.append("{");
			offset = token.getEnd();
			offset = trailer(token.getStart() + 1);
		} else {
			PEXUIPlugin.warning("{", block, offset, end);
		}

		output.indentIf(indentBody);

		if (!statements.isEmpty()) {
			output.newLine();
			walk(offset, end);
			offset = end;
			switch (parentType) {
			case ASTNode.FUNCTION_DECLARATION:
				if (options.blank_lines_at_beginning_of_method_body > 0) {
					output.blankLines(options.blank_lines_at_beginning_of_method_body);
				}
				break;
			}
			offset = visit(statements, offset, block.getEnd());
		} else {
			switch (parentType) {
			case ASTNode.CLASS_DECLARATION:
				if (options.insert_new_line_in_empty_type_declaration) {
					output.newLine();
				}
				break;
			case ASTNode.METHOD_DECLARATION:
			case ASTNode.FUNCTION_DECLARATION:
				if (options.insert_new_line_in_empty_method_body) {
					output.newLine();
				}
				break;
			default:
				if (options.insert_new_line_in_empty_block) {
					output.newLine();
				}
			}
		}

		token = findLastToken(offset, block.getEnd(),
				TokenTypes.PHP_CURLY_CLOSE);
		if (token != null) {
			walk(offset, token.getStart());
			offset = token.getEnd();
			output.unindentIf(indentBody);
			if (!statements.isEmpty()) {
				boolean inline = false;
				try {
					int lineOffset = holder.getLineOffset(token.getStart());
					Token[] tokens = holder.getTokens(lineOffset,
							token.getStart());
					int index = tokens.length;
					while (--index >= 0) {
						switch (tokens[index].getTokenType()) {
						case PHP_COMMENT_END:
							while (--index >= 0) {
								if (tokens[index].getTokenType().equals(
										TokenTypes.PHP_COMMENT_START)) {
									break;
								}
							}
							break;
						case PHPDOC_COMMENT_END:
							while (--index >= 0) {
								if (tokens[index].getTokenType().equals(
										TokenTypes.PHPDOC_COMMENT_START)) {
									break;
								}
							}
							break;
						case UNKNOWN_TOKEN:
							if (tokens[index].getType().equals(
									PHPRegionContext.PHP_OPEN)) {
								inline = true;
								index = 0;
							}
							break;
						case WHITESPACE:
							break;
						default:
							index = 0;
							break;
						}
					}
				} catch (BadLocationException e) {
				}
				if (!inline) {
					output.newLine();
				}
			}
			output.append("}");
		} else {
			PEXUIPlugin.warning("}", block, offset, end);
			output.unindentIf(indentBody);
			output.newLine();
		}

		if (blockData != null) {
			if (blockData.options_brace_position
					.equals(CodeFormatterConstants.NEXT_LINE_SHIFTED)) {
				output.indent(-1);
			}
		}

		walk(offset, block.getEnd());
		return false;
	}

	private boolean colonBlock(Block block) {
		List<Statement> statements = block.statements();

		int parentType = block.getParent().getType();
		boolean indent = false;
		int offset = block.getStart();
		int end = (!statements.isEmpty()) ? statements.get(0).getStart()
				: block.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, ":");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			offset = token.getEnd();
			output.spaceIf(options.insert_space_before_opening_brace_in_block);
			output.append(":");
		} else {
			PEXUIPlugin.warning(":", block, offset, end);
		}

		switch (parentType) {
		case ASTNode.SWITCH_STATEMENT:
			indent = options.indent_switchstatements_compare_to_switch;
			break;
		default:
			indent = options.indent_statements_compare_to_block;
		}
		output.indentIf(indent);

		if (!statements.isEmpty()) {
			walk(offset, end);
			offset = end;
			output.newLine();
			offset = visit(statements, offset, block.getEnd());
		} else {
			switch (parentType) {
			default:
				if (options.insert_new_line_in_empty_block) {
					output.newLine();
				}
			}
		}

		TokenTypes type = null;
		switch (parentType) {
		case ASTNode.IF_STATEMENT:
			// includes ';'
			// endif is out of Block
			walk(offset, block.getEnd());
			output.unindentIf(indent);
			return false;
		case ASTNode.FOR_STATEMENT:
			type = TokenTypes.PHP_ENDFOR;
			break;
		case ASTNode.FOR_EACH_STATEMENT:
			type = TokenTypes.PHP_ENDFOREACH;
			break;
		case ASTNode.SWITCH_STATEMENT:
			type = TokenTypes.PHP_ENDSWITCH;
			break;
		case ASTNode.WHILE_STATEMENT:
			type = TokenTypes.PHP_ENDWHILE;
			break;
		case ASTNode.DECLARE_STATEMENT:
			type = TokenTypes.PHP_ENDDECLARE;
			break;
		}
		incompleteStatement = true;
		end = block.getEnd();
		token = findToken(offset, end, type);
		if (token != null) {
			walk(offset, token.getStart()); // ""
			offset = token.getEnd();
			output.unindentIf(indent);
			output.append(holder.getText(token).trim());
		} else {
			PEXUIPlugin.warning(type.getValue(), block, offset, end);
			output.unindentIf(indent);
		}

		walk(offset, block.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(BreakStatement breakStatement) {
		Expression expression = breakStatement.getExpression();

		int offset = breakStatement.getStart();
		if (expression != null) {
			walk(offset, expression.getStart()); // "break"
			output.space();
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, breakStatement.getEnd()); // "break;" ";"
		return false;
	}

	@Override
	public boolean visit(CastExpression castExpression) {
		Expression expression = castExpression.getExpression();

		int offset = castExpression.getStart();
		int end = expression.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_CASTING);
		if (token != null) {
			String casting = holder.getText(token);
			Pattern pattern = Pattern.compile("(\\()(.*?)(\\))\\s*");
			Matcher matcher = pattern.matcher(casting);
			if (matcher.matches()) {
				output.append("(");
				output.spaceIf(options.insert_space_after_opening_paren_in_cast);
				output.append(matcher.group(2).trim());
				output.spaceIf(options.insert_space_before_closing_paren_in_cast);
				output.append(")");
				output.spaceIf(options.insert_space_after_closing_paren_in_cast);
				offset = token.getEnd();
			}
		} else {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.append("(");
				output.spaceIf(options.insert_space_after_opening_paren_in_cast);
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("(", castExpression, offset, end);
			}
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
			if (token != null) {
				walk(offset, token.getStart()); // "int" "string" ...
				output.spaceIf(options.insert_space_before_closing_paren_in_cast);
				output.append(")");
				output.spaceIf(options.insert_space_after_closing_paren_in_cast);
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning(")", castExpression, offset, end);
			}
		}

		walk(offset, expression.getStart()); // ""
		expression.accept(this);
		walk(expression.getEnd(), castExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(CatchClause catchClause) {
		Expression className = catchClause.getClassName();
		Variable variable = catchClause.getVariable();
		Block body = catchClause.getBody();

		if (options.insert_new_line_before_catch_in_try_statement) {
			output.newLine();
		} else {
			output.space();
		}

		int offset = catchClause.getStart();
		int end = className.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "catch"
			output.spaceIf(options.insert_space_before_opening_paren_in_catch);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_catch);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", catchClause, offset, end);
		}

		walk(offset, className.getStart()); // ""
		className.accept(this);
		output.space();
		walk(className.getEnd(), variable.getStart()); // ""
		variable.accept(this);
		offset = variable.getEnd();

		end = body.getStart();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_catch);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", catchClause, offset, end);
		}

		walk(offset, body.getStart()); // ""
		body.accept(this);
		walk(body.getEnd(), catchClause.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(ClassDeclaration classDeclaration) {
		visitClassOrTrait(classDeclaration);
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		ClassName className = classInstanceCreation.getClassName();
		List<Expression> ctorParams = classInstanceCreation.ctorParams();

		int wrapStyle = getWrapStyle(options.alignment_for_arguments_in_allocation_expression);
		int indentStyle = getIndentStyle(options.alignment_for_arguments_in_allocation_expression);
		boolean forceSplit = isForceSplit(options.alignment_for_arguments_in_allocation_expression);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_comma_in_allocation_expression)
				+ boolDigit(options.insert_space_after_comma_in_allocation_expression);
		boolean spaceRequired = false;
		boolean wrapIndent = false;
		int markedPosition = 0;

		walk(classInstanceCreation.getStart(), className.getStart()); // "new"
		className.accept(this);

		int offset = className.getEnd();
		int end;
		if (!ctorParams.isEmpty()) {
			end = ctorParams.get(0).getStart();
		} else {
			end = classInstanceCreation.getEnd();
		}
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_opening_paren_in_method_invocation);
			output.append("(");
			if (!ctorParams.isEmpty()) {
				output.spaceIf(options.insert_space_after_opening_paren_in_method_invocation);
			}
			offset = token.getEnd();
		} else {
			if (!ctorParams.isEmpty()) {
				PEXUIPlugin
						.warning("(", classInstanceCreation, offset, end);
			}
		}

		if (ctorParams.isEmpty()) {
			output.spaceIf(options.insert_space_between_empty_parens_in_method_invocation);
		}

		if (split && !forceSplit) {
			int width = output.getPosition();
			for (Expression parameter : ctorParams) {
				width += parameter.getLength();
			}
			if (ctorParams.size() > 1) {
				width += (ctorParams.size() - 1) * wrapDelta;
			}
			split = (width + 1) > options.page_width;
		}

		boolean isFirst = true;
		for (Expression ctorParam : ctorParams) {
			token = findToken(offset, ctorParam.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_allocation_expression);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_allocation_expression;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				spaceRequired = false;
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
						}
						break;
					}
				}
			}
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition() + ctorParam.getLength()
							+ wrapDelta;
					if (width >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			walk(offset, ctorParam.getStart()); // ""
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
			}
			ctorParam.accept(this);
			offset = ctorParam.getEnd();
			isFirst = false;
		}
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			output.markPosition(markedPosition);
		}

		walk(offset, classInstanceCreation.getEnd()); // ")"
		return false;
	}

	@Override
	public boolean visit(ClassName className) {
		Expression name = className.getName();

		walk(className.getStart(), name.getStart());
		name.accept(this);
		walk(name.getEnd(), className.getEnd());
		return false;
	}

	@Override
	public boolean visit(CloneExpression cloneExpression) {
		Expression expression = cloneExpression.getExpression();

		walk(cloneExpression.getStart(), expression.getStart());
		expression.accept(this);
		walk(expression.getEnd(), cloneExpression.getEnd());
		return false;
	}

	@Override
	public boolean visit(Comment comment) {
		walk(comment.getStart(), comment.getEnd());
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression conditionalExpression) {
		Expression condition = conditionalExpression.getCondition();
		Expression ifTrue = conditionalExpression.getIfTrue();
		Expression ifFalse = conditionalExpression.getIfFalse();

		int wrapStyle = getWrapStyle(options.alignment_for_conditional_expression);
		int indentStyle = getIndentStyle(options.alignment_for_conditional_expression);
		boolean forceSplit = isForceSplit(options.alignment_for_conditional_expression);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_question_in_conditional)
				+ boolDigit(options.insert_space_after_question_in_conditional);
		boolean spaceRequired = false;
		boolean wrapped = false;
		int markedPosition = 0;

		walk(conditionalExpression.getStart(), condition.getStart()); // ""
		condition.accept(this);

		if (split && !forceSplit) {
			int width = output.getPosition();
			int delta = 1
					+ boolDigit(options.insert_space_before_colon_in_conditional)
					+ boolDigit(options.insert_space_after_colon_in_conditional);
			if (ifTrue != null) {
				width += wrapDelta + ifTrue.getLength();
			}
			if (ifFalse != null) {
				width += delta + ifFalse.getLength();
			}
			split = width > options.page_width;
		}

		int offset = condition.getEnd();
		int end;
		if (ifTrue != null) {
			end = ifTrue.getStart();
		} else {
			end = ifFalse.getStart();
		}
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "?");
		if (token != null) {
			walk(offset, token.getStart());
			spaceRequired = options.insert_space_before_question_in_conditional;
			if (split) {
				wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
						: options.continuation_indentation;
				if (wrap > 0) {
					output.wrap(wrap);
				}
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition();
					if (ifTrue != null) {
						width += wrapDelta + ifTrue.getLength();
					}
					if (forceSplit || width > options.page_width) {
						output.newLine();
						spaceRequired = false;
						wrapped = true;
					}
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					// do not wrap here
					break;
				case Alignment.M_NEXT_SHIFTED_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
					output.newLine();
					spaceRequired = false;
					wrapped = true;
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
			}
			output.append("?");
			output.spaceIf(options.insert_space_after_question_in_conditional);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("?", conditionalExpression, offset, end);
		}

		if (ifTrue != null) {
			walk(offset, end); // ""
			ifTrue.accept(this);
			offset = ifTrue.getEnd();
		}

		wrapDelta = 1
				+ boolDigit(options.insert_space_before_colon_in_conditional)
				+ boolDigit(options.insert_space_after_colon_in_conditional);

		end = ifFalse.getStart();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ":");
		if (token != null) {
			walk(offset, token.getStart());
			spaceRequired = options.insert_space_before_colon_in_conditional;
			if (split) {
				int width = output.getPosition();
				if (ifFalse != null) {
					width += wrapDelta + ifFalse.getLength();
				}
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (forceSplit || width > options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					if (wrapped) {
						wrap++;
						output.wrap(+1);
					}
					// non-break
				case Alignment.M_ONE_PER_LINE_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				}
			}
			output.spaceIf(spaceRequired);
			output.append(":");
			output.spaceIf(options.insert_space_after_colon_in_conditional);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(":", conditionalExpression, offset, end);
		}
		walk(offset, ifFalse.getStart()); // ""
		ifFalse.accept(this);
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			output.markPosition(markedPosition);
		}

		walk(ifFalse.getEnd(), conditionalExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(ConstantDeclaration classConstantDeclaration) {
		List<Identifier> names = classConstantDeclaration.names();
		List<Expression> initializers = classConstantDeclaration.initializers();

		int offset = classConstantDeclaration.getStart();
		Iterator<Expression> it = initializers.iterator();
		for (Identifier name : names) {
			Expression initializer = it.next();
			walk(offset, name.getStart()); // "const" ","
			name.accept(this);
			offset = name.getEnd();
			int end = initializer.getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "=");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_assignment_operator);
				output.append("=");
				output.spaceIf(options.insert_space_after_assignment_operator);
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("=", classConstantDeclaration, offset,
						end);
			}
			walk(offset, initializer.getStart()); // "="
			initializer.accept(this);
			offset = initializer.getEnd();
		}
		walk(offset, classConstantDeclaration.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(ContinueStatement continueStatement) {
		Expression expression = continueStatement.getExpression();

		int offset = continueStatement.getStart();
		if (expression != null) {
			walk(offset, expression.getStart()); // "continue"
			output.space();
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, continueStatement.getEnd()); // "continue;" ";"
		return false;
	}

	@Override
	public boolean visit(DeclareStatement declareStatement) {
		List<Identifier> directiveNames = declareStatement.directiveNames();
		List<Expression> directiveValues = declareStatement.directiveValues();
		Statement body = declareStatement.getBody();

		int offset = declareStatement.getStart();
		Iterator<Expression> it = directiveValues.iterator();
		for (Identifier directiveName : directiveNames) {
			Expression directiveValue = it.next();
			walk(offset, directiveName.getStart());
			directiveName.accept(this);
			walk(directiveName.getEnd(), directiveValue.getStart());
			directiveValue.accept(this);
			offset = directiveValue.getEnd();
		}
		walk(offset, body.getStart());
		output.space();
		body.accept(this);
		walk(body.getEnd(), declareStatement.getEnd());
		return false;
	}

	@Override
	public boolean visit(DoStatement doStatement) {
		Statement body = doStatement.getBody();
		Expression condition = doStatement.getCondition();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockData.options_indent_body = options.indent_statements_compare_to_block;
		blockDataStack.push(blockData);

		walk(doStatement.getStart(), body.getStart()); // "do"
		int offset = body.getEnd();
		if (body instanceof Block) {
			body.accept(this);
		} else {
			// single statement
			output.indentIf(options.indent_statements_compare_to_block);
			output.newLine();
			body.accept(this);
			offset = trailer(offset);
			output.unindentIf(options.indent_statements_compare_to_block);
			output.newLine();
		}

		if (options.insert_new_line_before_while_in_do_statement) {
			output.newLine();
		} else {
			if (!output.isNewLine()) {
				output.space();
			}
		}
		int end = condition.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "while"
			output.spaceIf(options.insert_space_before_opening_paren_in_while);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_while);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", doStatement, offset, end);
		}

		walk(offset, condition.getStart()); // ""
		condition.accept(this);
		offset = condition.getEnd();

		end = doStatement.getEnd();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_while);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", doStatement, offset, end);
		}
		walk(offset, doStatement.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(EchoStatement echoStatement) {
		List<Expression> expressions = echoStatement.expressions();

		int offset = echoStatement.getStart();
		for (Expression expression : expressions) {
			Token token = findToken(offset, expression.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_method_invocation_arguments);
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_method_invocation_arguments);
				offset = token.getEnd();
			} else {
				// first expression
				int end = expression.getStart();
				walk(offset, end); // "echo"
				offset = end;
				if (expression instanceof ParenthesisExpression) {
					if (!options.insert_space_before_opening_paren_in_parenthesized_expression) {
						output.spaceIf(options.insert_space_before_parenthesized_expression_in_echo);
					}
				} else {
					output.space();
				}
			}
			walk(offset, expression.getStart()); // ""
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, echoStatement.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(EmptyStatement emptyStatement) {
		walk(emptyStatement.getStart(), emptyStatement.getEnd());
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement expressionStatement) {
		Expression expression = expressionStatement.getExpression();

		walk(expressionStatement.getStart(), expression.getStart());
		expression.accept(this);
		walk(expression.getEnd(), expressionStatement.getEnd());
		return false;
	}

	@Override
	public boolean visit(FieldAccess fieldAccess) {
		VariableBase dispatcher = fieldAccess.getDispatcher();
		Variable field = fieldAccess.getField();

		int wrapStyle = getWrapStyle(options.alignment_for_selector_in_method_invocation);
		int indentStyle = getIndentStyle(options.alignment_for_selector_in_method_invocation);
		boolean forceSplit = isForceSplit(options.alignment_for_selector_in_method_invocation);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrapDelta = 2
				+ boolDigit(options.insert_space_before_object_operator)
				+ boolDigit(options.insert_space_after_object_operator);
		boolean spaceRequired = false;
		int markedPosition = 0;
		ASTNode parent = fieldAccess.getParent();
		// CAUTION!
		// Quote内でのFieldAccessを行分割するとパースの結果が正しくなくなり、Verifyエラーを生じる
		if (parent instanceof Quote) {
			split = false;
			forceSplit = false;
		}
		MethodInvocationData invocationData = new MethodInvocationData(); // dummy
		if (split) {
			if (!(parent instanceof MethodInvocation || parent instanceof FieldAccess)
					|| methodInvocationStack.isEmpty()) {
				methodInvocationStack.push(new MethodInvocationData());
			}
			invocationData = methodInvocationStack.peek();
			invocationData.count++;
			if (invocationData.depth < invocationData.count) {
				invocationData.depth = invocationData.count;
			}
		}

		walk(fieldAccess.getStart(), dispatcher.getStart()); // ""
		dispatcher.accept(this);
		boolean isFirst = invocationData.depth == invocationData.count;

		int offset = dispatcher.getEnd();
		int end = field.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_OBJECT_OPERATOR);
		if (token != null) {
			walk(offset, token.getStart()); // ""
			spaceRequired = options.insert_space_before_object_operator;
			if (split) {
				if (isFirst) {
					invocationData.wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (invocationData.wrap > 0) {
						output.wrap(invocationData.wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
						}
						break;
					}
				} else {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!invocationData.wrapIndent) {
							invocationData.wrap++;
							output.wrap(+1);
							invocationData.wrapIndent = true;
						}
						break;
					}
				}
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition() + field.getLength()
							+ wrapDelta;
					if (forceSplit || width >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
				invocationData.markedPosition = output.getMarkedPosition();
			}
			output.append("->");
			output.spaceIf(options.insert_space_after_object_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("->", fieldAccess, offset, end);
		}

		walk(offset, field.getStart()); // ""
		field.accept(this);
		walk(field.getEnd(), fieldAccess.getEnd());
		if (split) {
			if (--invocationData.count == 0) {
				if (invocationData.wrap > 0) {
					output.wrap(-invocationData.wrap);
				}
				methodInvocationStack.pop();
				if (methodInvocationStack.isEmpty()) {
					output.markPosition(markedPosition);
				} else {
					output.markPosition(methodInvocationStack.peek().markedPosition);
				}
			}
		}
		return false;
	}

	@Override
	public boolean visit(FieldsDeclaration fieldsDeclaration) {
		List<SingleFieldDeclaration> fields = fieldsDeclaration.fields();
		if (!classDeclarationStack.isEmpty()) {
			if (classDeclarationStack.peek().isFirstProperty) {
				if (options.blank_lines_before_field > 0) {
					output.blankLines(options.blank_lines_before_field);
				}
				classDeclarationStack.peek().isFirstProperty = false;
			}
		}

		int offset = fieldsDeclaration.getStart();
		Token token;
		for (SingleFieldDeclaration field : fields) {
			token = findToken(offset, field.getStart(), TokenTypes.PHP_TOKEN,
					",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_multiple_field_declarations);
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_multiple_field_declarations);
				offset = token.getEnd();
			} else {
				// skip first field
			}
			walk(offset, field.getStart()); // "" "var" "public" ...
			field.accept(this);
			offset = field.getEnd();
		}
		walk(offset, fieldsDeclaration.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(ForStatement forStatement) {
		List<Expression> initializers = forStatement.initializers();
		List<Expression> conditions = forStatement.conditions();
		List<Expression> updaters = forStatement.updaters();
		Statement body = forStatement.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockData.options_indent_body = options.indent_statements_compare_to_block;
		blockDataStack.push(blockData);

		int offset = forStatement.getStart();
		int end = body.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "for"
			output.spaceIf(options.insert_space_before_opening_paren_in_for);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_for);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", forStatement, offset, end);
		}

		for (Expression initializer : initializers) {
			token = findToken(offset, initializer.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_for_inits);
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_for_inits);
				offset = token.getEnd();
			} else {
				// skip first initializer
			}
			walk(offset, initializer.getStart()); // ""
			initializer.accept(this);
			offset = initializer.getEnd();
		}
		token = findToken(offset, end, TokenTypes.PHP_SEMICOLON);
		if (token != null) {
			spaceInsertableBeforeSemicolon = false;
			walk(offset, token.getStart());
			if (!initializers.isEmpty()) {
				output.spaceIf(options.insert_space_before_semicolon_in_for);
			}
			output.append(";");
			if (!conditions.isEmpty()) {
				output.spaceIf(options.insert_space_after_semicolon_in_for);
			}
			offset = token.getEnd();
			spaceInsertableBeforeSemicolon = true;
		} else {
			PEXUIPlugin.warning(";", forStatement, offset, end);
		}

		for (Expression condition : conditions) {
			token = findToken(offset, condition.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_for_inits); // 代用
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_for_inits); // 代用
				offset = token.getEnd();
			} else {
				// skip first initializer
			}
			walk(offset, condition.getStart()); // ""
			condition.accept(this);
			offset = condition.getEnd();
		}
		token = findToken(offset, end, TokenTypes.PHP_SEMICOLON);
		if (token != null) {
			spaceInsertableBeforeSemicolon = false;
			walk(offset, token.getStart());
			if (!conditions.isEmpty()) {
				output.spaceIf(options.insert_space_before_semicolon_in_for);
			}
			output.append(";");
			if (!updaters.isEmpty()) {
				output.spaceIf(options.insert_space_after_semicolon_in_for);
			}
			offset = token.getEnd();
			spaceInsertableBeforeSemicolon = true;
		} else {
			PEXUIPlugin.warning(";", forStatement, offset, end);
		}

		for (Expression updater : updaters) {
			token = findToken(offset, updater.getStart(), TokenTypes.PHP_TOKEN,
					",");
			if (token != null) {
				output.spaceIf(options.insert_space_before_comma_in_for_increments);
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_for_increments);
				offset = token.getEnd();
			} else {
				// skip first updater
			}
			walk(offset, updater.getStart()); // ""
			updater.accept(this);
			offset = updater.getEnd();
		}
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_for);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", forStatement, offset, end);
		}

		walk(offset, body.getStart()); // ""
		offset = body.getEnd();
		if (body instanceof Block) {
			body.accept(this);
		} else {
			// single statement
			output.indentIf(options.indent_statements_compare_to_block);
			output.newLine();
			body.accept(this);
			offset = trailer(offset);
			output.unindentIf(options.indent_statements_compare_to_block);
			output.newLine();
		}
		walk(offset, forStatement.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(ForEachStatement forEachStatement) {
		Expression expression = forEachStatement.getExpression();
		Expression key = forEachStatement.getKey();
		Expression value = forEachStatement.getValue();
		Statement statement = forEachStatement.getStatement();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockData.options_indent_body = options.indent_statements_compare_to_block;
		blockDataStack.push(blockData);

		int offset = forEachStatement.getStart();
		int end = statement.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "foreach"
			output.spaceIf(options.insert_space_before_opening_paren_in_for);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_for);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", forEachStatement, offset, end);
		}

		walk(offset, expression.getStart()); // ""
		expression.accept(this);
		offset = expression.getEnd();

		if (key != null) {
			walk(offset, key.getStart()); // "as"
			key.accept(this);
			offset = key.getEnd();
			end = value.getStart();
			token = findToken(offset, end, TokenTypes.PHP_OPERATOR, "=>");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_double_arrow_operator);
				output.append("=>");
				output.spaceIf(options.insert_space_after_double_arrow_operator);
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("=>", forEachStatement, offset, end);
			}
		}

		walk(offset, value.getStart()); // "as" "=>"
		value.accept(this);
		offset = value.getEnd();

		end = statement.getStart();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_for);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", forEachStatement, offset, end);
		}

		walk(offset, statement.getStart());
		offset = statement.getEnd();
		if (statement instanceof Block) {
			statement.accept(this);
		} else {
			// single statement
			output.indentIf(options.indent_statements_compare_to_block);
			output.newLine();
			statement.accept(this);
			offset = trailer(offset);
			output.unindentIf(options.indent_statements_compare_to_block);
			output.newLine();
		}
		walk(offset, forEachStatement.getEnd());
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(FormalParameter formalParameter) {
		Expression parameterType = formalParameter.getParameterType();
		Expression parameterName = formalParameter.getParameterName();
		Expression defaultValue = formalParameter.getDefaultValue();

		int offset = formalParameter.getStart();
		if (parameterType != null) {
			walk(offset, parameterType.getStart()); // "" ","
			parameterType.accept(this);
			output.space();
			offset = parameterType.getEnd();
		}
		walk(offset, parameterName.getStart()); // ""
		parameterName.accept(this);
		offset = parameterName.getEnd();
		if (defaultValue != null) {
			int end = defaultValue.getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "=");
			if (token != null) {
				walk(offset, token.getStart());
				output.space().append("=").space();
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("=", formalParameter, offset, end);
			}
			walk(offset, defaultValue.getStart()); // "="
			defaultValue.accept(this);
			offset = defaultValue.getEnd();
		}
		walk(offset, formalParameter.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(FunctionDeclaration functionDeclaration) {
		Identifier functionName = functionDeclaration.getFunctionName();
		List<FormalParameter> formalParameters = functionDeclaration
				.formalParameters();
		Block body = functionDeclaration.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_method_declaration;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_method_declaration;
		blockData.options_indent_body = options.indent_statements_compare_to_body;
		blockDataStack.push(blockData);

		int wrapStyle = getWrapStyle(options.alignment_for_method_declaration);
		int indentStyle = getIndentStyle(options.alignment_for_method_declaration);
		boolean forceSplit = isForceSplit(options.alignment_for_method_declaration);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1;
		boolean spaceRequired = true;
		boolean wrapIndent = false;
		boolean wrapped = false;

		if (insert_blank_lines_before_method_stopper) {
			insert_blank_lines_before_method_stopper = false;
		} else {
			if (options.blank_lines_before_method > 0) {
				output.blankLines(options.blank_lines_before_method);
			}
		}

		int offset = functionDeclaration.getStart();
		int end = functionName.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_FUNCTION);
		if (token != null) {
			walk(offset, token.getEnd()); // "function"
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("function", functionDeclaration, offset,
					end);
		}

		if (split && !forceSplit) {
			int len = output.getPosition() + functionName.getLength() + 3;
			for (FormalParameter parameter : formalParameters) {
				len += parameter.getLength();
			}
			int delta = 1
					+ boolDigit(options.insert_space_before_comma_in_method_declaration_parameters)
					+ boolDigit(options.insert_space_after_comma_in_method_declaration_parameters);
			len += formalParameters.size() > 1 ? (formalParameters.size() - 1)
					* delta : 0;
			split = len > options.page_width;
		}

		if (split) {
			wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
					: options.continuation_indentation;
			if (wrap > 0) {
				output.wrap(wrap);
			}
			switch (wrapStyle) {
			case Alignment.M_COMPACT_SPLIT:
			case Alignment.M_NEXT_PER_LINE_SPLIT:
				int width = output.getPosition() + functionName.getLength() + 2;
				if (forceSplit || width > options.page_width) {
					output.newLine();
					spaceRequired = false;
					wrapped = true;
				}
				break;
			case Alignment.M_NEXT_SHIFTED_SPLIT:
			case Alignment.M_ONE_PER_LINE_SPLIT:
				output.newLine();
				spaceRequired = false;
				wrapped = true;
				break;
			}
		}
		output.spaceIf(spaceRequired);
		spaceRequired = false;

		if (functionDeclaration.isReference()) {
			end = functionName.getStart();
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, "&");
			if (token != null) {
				walk(offset, token.getEnd()); // "&"
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("&", functionDeclaration, offset, end);
			}
		}

		functionName.accept(this);
		if (!wrapped) {
			if (wrap > 0) {
				output.wrap(-wrap);
				wrap = 0;
			}
		}

		offset = functionName.getEnd();
		if (!formalParameters.isEmpty()) {
			end = formalParameters.get(0).getStart();
		} else {
			if (body != null) {
				end = body.getStart();
			} else {
				end = functionDeclaration.getEnd();
			}
		}
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_opening_paren_in_method_declaration);
			output.append("(");
			if (!formalParameters.isEmpty()) {
				output.spaceIf(options.insert_space_after_opening_paren_in_method_declaration);
			}
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", functionDeclaration, offset, end);
		}

		if (formalParameters.isEmpty()) {
			output.spaceIf(options.insert_space_between_empty_parens_in_method_declaration);
		}

		boolean fsplit = split;
		int fwrap = wrap;
		wrapStyle = getWrapStyle(options.alignment_for_parameters_in_method_declaration);
		indentStyle = getIndentStyle(options.alignment_for_parameters_in_method_declaration);
		forceSplit = isForceSplit(options.alignment_for_parameters_in_method_declaration);
		split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		wrap = 0;
		wrapDelta = 1
				+ boolDigit(options.insert_space_before_comma_in_method_declaration_parameters)
				+ boolDigit(options.insert_space_after_comma_in_method_declaration_parameters);
		spaceRequired = false;
		wrapIndent = false;

		if (split && !forceSplit) {
			int width = output.getPosition() + 1;
			for (FormalParameter parameter : formalParameters) {
				width += parameter.getLength();
			}
			if (formalParameters.size() > 1) {
				width += (formalParameters.size() - 1) * wrapDelta;
			}
			split = (width + 1) > options.page_width;
		}

		boolean isFirst = true;
		for (FormalParameter formalParameter : formalParameters) {
			token = findToken(offset, formalParameter.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_method_declaration_parameters);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_method_declaration_parameters;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
							spaceRequired = false;
						}
						break;
					}
				}
			}
			walk(offset, formalParameter.getStart()); // ""
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition()
							+ formalParameter.getLength() + wrapDelta;
					if (width > options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				output.markPosition();
			}
			formalParameter.accept(this);
			offset = formalParameter.getEnd();
			isFirst = false;
		}
		if (split || fsplit) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			if (fwrap > 0) {
				output.wrap(-fwrap);
			}
			output.markPosition(0);
		}

		if (body != null) {
			end = body.getStart();
		} else {
			end = functionDeclaration.getEnd();
		}
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			if (!formalParameters.isEmpty()) {
				output.spaceIf(options.insert_space_before_closing_paren_in_method_declaration);
			}
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", functionDeclaration, offset, end);
		}

		if (body != null) {
			walk(offset, body.getStart());
			body.accept(this);
			offset = body.getEnd();
		}

		walk(offset, functionDeclaration.getEnd()); // ";"

		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(FunctionInvocation functionInvocation) {
		FunctionName functionName = functionInvocation.getFunctionName();
		List<Expression> parameters = functionInvocation.parameters();

		int wrapStyle = getWrapStyle(options.alignment_for_arguments_in_method_invocation);
		int indentStyle = getIndentStyle(options.alignment_for_arguments_in_method_invocation);
		boolean forceSplit = isForceSplit(options.alignment_for_arguments_in_method_invocation);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_comma_in_method_invocation_arguments)
				+ boolDigit(options.insert_space_after_comma_in_method_invocation_arguments);
		boolean spaceRequired = false;
		boolean wrapIndent = false;
		int markedPosition = 0;

		Token name = holder.getToken(functionName.getStart());
		boolean isReserved = false;
		switch (name.getTokenType()) {
		case PHP_PRINT:
		case PHP_EXIT:
		case PHP_DIE:
			isReserved = true;
			break;
		}

		walk(functionInvocation.getStart(), functionName.getStart());
		functionName.accept(this);

		int offset = functionName.getEnd();
		int end;
		if (!parameters.isEmpty()) {
			end = parameters.get(0).getStart();
		} else {
			end = functionInvocation.getEnd();
		}
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_opening_paren_in_method_invocation);
			output.append("(");
			if (!parameters.isEmpty()) {
				output.spaceIf(options.insert_space_after_opening_paren_in_method_invocation);
			}
			offset = token.getEnd();
		} else {
			if (isReserved) {
				if (!parameters.isEmpty()) {
					if (parameters.get(0).getType() != ASTNode.PARENTHESIS_EXPRESSION) {
						output.space();
					}
				}
			} else {
				PEXUIPlugin.warning("(", functionInvocation, offset, end);
			}
		}

		if (parameters.isEmpty()) {
			output.spaceIf(options.insert_space_between_empty_parens_in_method_invocation);
		}

		if (split && !forceSplit) {
			int width = output.getPosition();
			for (Expression parameter : parameters) {
				width += parameter.getLength();
			}
			if (parameters.size() > 1) {
				width += (parameters.size() - 1) * wrapDelta;
			}
			split = (width + 1) > options.page_width;
		}

		inArguments = true;
		boolean isFirst = true;
		for (Expression parameter : parameters) {
			token = findToken(offset, parameter.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_method_invocation_arguments);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_method_invocation_arguments;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				spaceRequired = false;
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
						}
						break;
					}
				}
			}
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition() + parameter.getLength()
							+ wrapDelta;
					if (width >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			walk(offset, parameter.getStart()); // ""
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
			}
			parameter.accept(this);
			offset = parameter.getEnd();
			isFirst = false;
		}
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			output.markPosition(markedPosition);
		}
		inArguments = false;

		end = functionInvocation.getEnd();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart());
			if (!parameters.isEmpty()) {
				output.spaceIf(options.insert_space_before_closing_paren_in_method_invocation);
			}
			output.append(")");
			offset = token.getEnd();
		} else {
			if (!isReserved) {
				PEXUIPlugin.warning(")", functionInvocation, offset, end);
			}
		}
		walk(offset, functionInvocation.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(FunctionName functionName) {
		Expression name = functionName.getName();

		walk(functionName.getStart(), name.getStart());
		name.accept(this);
		walk(name.getEnd(), functionName.getEnd());
		return false;
	}

	@Override
	public boolean visit(GlobalStatement globalStatement) {
		List<Variable> variables = globalStatement.variables();

		int offset = globalStatement.getStart();
		for (Variable variable : variables) {
			Token token = findToken(offset, variable.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_multiple_field_declarations); // 代用
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_multiple_field_declarations); // 代用
				offset = token.getEnd();
			} else {
				// skip first variable
			}
			walk(offset, variable.getStart()); // ""
			variable.accept(this);
			offset = variable.getEnd();
		}
		walk(offset, globalStatement.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(GotoLabel gotoLabel) {
		Identifier name = gotoLabel.getName();

		walk(gotoLabel.getStart(), name.getStart()); // ""
		name.accept(this);

		int offset = name.getEnd();
		int end = gotoLabel.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, ":");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_colon_in_labeled_statement);
			output.append(":");
			if (!options.insert_new_line_after_label) {
				output.spaceIf(options.insert_space_after_colon_in_labeled_statement);
			}
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(":", gotoLabel, offset, end);
		}
		walk(offset, gotoLabel.getEnd()); // ""
		trailer(offset);
		if (options.insert_new_line_after_label) {
			output.newLine();
		}
		return false;
	}

	@Override
	public boolean visit(GotoStatement gotoStatement) {
		Identifier label = gotoStatement.getLabel();

		walk(gotoStatement.getStart(), label.getStart()); // "goto"
		label.accept(this);
		walk(label.getEnd(), gotoStatement.getEnd());
		return false;
	}

	@Override
	public boolean visit(Identifier identifier) {
		output.append(identifier.getName());
		return false;
	}

	@Override
	public boolean visit(IfStatement ifStatement) {
		Expression condition = ifStatement.getCondition();
		Statement trueStatement = ifStatement.getTrueStatement();
		Statement falseStatement = ifStatement.getFalseStatement();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockData.options_indent_body = options.indent_statements_compare_to_block;
		blockDataStack.push(blockData);

		boolean indent = false;
		int offset = ifStatement.getStart();
		int end = condition.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token == null) {
			PEXUIPlugin.warning("(", ifStatement, offset, end);
		} else {
			walk(offset, token.getStart()); // "if"
			output.spaceIf(options.insert_space_before_opening_paren_in_if);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_if);
			offset = token.getEnd();
		}

		walk(offset, condition.getStart()); // ""
		condition.accept(this);
		offset = condition.getEnd();

		end = trueStatement.getStart();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token == null) {
			PEXUIPlugin.warning(")", ifStatement, offset, end);
		} else {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_if);
			output.append(")");
			offset = token.getEnd();
		}

		walk(offset, trueStatement.getStart()); // ""
		if (trueStatement instanceof Block) {
		} else {
			if (options.keep_then_statement_on_same_line) {
				output.space();
			} else if (falseStatement == null
					&& options.keep_simple_if_on_one_line) {
				output.space();
			} else {
				output.indent(+1);
				indent = true;
				output.newLine();
			}
		}
		trueStatement.accept(this);
		offset = Math.max(trueStatement.getEnd(), globalOffset);
		if (indent) {
			output.indent(-1);
			indent = false;
		}
		if (!(trueStatement instanceof Block)) {
			output.newLine();
		}

		if (falseStatement != null) {
			if (falseStatement instanceof IfStatement) {
				// elseif, else if
				Token firstToken = holder.getToken(falseStatement.getStart());
				if (firstToken != null) {
					if (firstToken.getTokenType().equals(TokenTypes.PHP_ELSEIF)) {
						// elseif
						walk(offset, falseStatement.getStart()); // ""
						if (options.insert_new_line_before_else_in_if_statement) {
							output.newLine();
						} else {
							boolean spacing = false;
							if (trueStatement instanceof Block) {
								if (((Block) trueStatement).isCurly()) {
									// if(...){...}elseif(...){...}
									spacing = options.insert_space_after_closing_brace_in_block;
								}
							}
							output.spaceIf(spacing);
						}
						falseStatement.accept(this);
						offset = Math
								.max(falseStatement.getEnd(), globalOffset);
						walk(offset, ifStatement.getEnd()); // ""
						return false;
					}
				}
			}
			// else, else if
			end = falseStatement.getStart();
			token = findToken(offset, end, TokenTypes.PHP_ELSE, "else");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				if (options.insert_new_line_before_else_in_if_statement) {
					output.newLine();
				} else {
					boolean spacing = false;
					if (trueStatement instanceof Block) {
						if (((Block) trueStatement).isCurly()) {
							// if(...){...}else{...}
							spacing = options.insert_space_after_closing_brace_in_block;
						}
					}
					output.spaceIf(spacing);
				}
				output.append("else");
				if (falseStatement instanceof IfStatement) {
					// "else if"
					if (options.compact_else_if) {
						output.space();
					} else {
						output.indent(+1);
						indent = true;
						output.newLine();
					}
				} else if (falseStatement instanceof Block) {
					// "else {" "else:"
				} else {
					// e.g. "else return;"
					if (options.keep_else_statement_on_same_line) {
						output.space();
					} else {
						output.indent(+1);
						indent = true;
						output.newLine();
					}
				}
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("else", ifStatement, offset, end);
			}
			walk(offset, falseStatement.getStart()); // ""
			falseStatement.accept(this);
			offset = Math.max(falseStatement.getEnd(), globalOffset);
			if (indent) {
				output.indent(-1);
			}
		}

		walk(offset, ifStatement.getEnd()); // "" "endif;"
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(IgnoreError ignoreError) {
		Expression expression = ignoreError.getExpression();

		walk(ignoreError.getStart(), expression.getStart());
		expression.accept(this);
		walk(expression.getEnd(), ignoreError.getEnd());
		return false;
	}

	@Override
	public boolean visit(Include include) {
		Expression expression = include.getExpression();

		walk(include.getStart(), expression.getStart()); // "include"
		if (!(expression instanceof ParenthesisExpression)) {
			output.space();
		}
		expression.accept(this);
		walk(expression.getEnd(), include.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(InfixExpression infixExpression) {
		if (infixExpression.getOperator() == InfixExpression.OP_CONCAT) {
			return visitConcat(infixExpression);
		}

		Expression left = infixExpression.getLeft();
		Expression right = infixExpression.getRight();

		int wrapStyle = getWrapStyle(options.alignment_for_binary_expression);
		int indentStyle = getIndentStyle(options.alignment_for_binary_expression);
		boolean forceSplit = isForceSplit(options.alignment_for_binary_expression);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 0
				+ boolDigit(options.insert_space_before_binary_operator)
				+ boolDigit(options.insert_space_after_binary_operator);
		boolean spaceRequired = false;

		walk(infixExpression.getStart(), left.getStart());
		left.accept(this);

		int offset = left.getEnd();
		int end = right.getStart();
		String op = InfixExpression.getOperator(infixExpression.getOperator());
		Token token;
		if (op.length() == 1) {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, op);
		} else {
			token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
			if (token == null
					&& infixExpression.getOperator() == InfixExpression.OP_IS_NOT_EQUAL) {
				op = "<>";
				token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
			}
		}
		if (token != null) {
			boolean options_insert_space_before_binary_operator = options.insert_space_before_binary_operator;
			boolean options_insert_space_after_binary_operator = options.insert_space_after_binary_operator;
			IDocument document = holder.getDocument();
			char preceding = Utils.getPrecedingChar(document,
					token.getStart() - 1);
			char following = Utils.getFollowingChar(document, token.getEnd());
			switch (infixExpression.getOperator()) {
			case InfixExpression.OP_PLUS:
				// following character must not '+' sign
				if (following == '+') {
					options_insert_space_after_binary_operator = true;
				}
				break;
			case InfixExpression.OP_MINUS:
				// following character must not '-' sign
				if (following == '-') {
					options_insert_space_after_binary_operator = true;
				}
				break;
			case InfixExpression.OP_STRING_AND:
			case InfixExpression.OP_STRING_OR:
			case InfixExpression.OP_STRING_XOR:
				// preceding/following character must not alphabetic
				if (Character.isLetter(preceding)) {
					options_insert_space_before_binary_operator = true;
				}
				if (Character.isLetter(following)) {
					options_insert_space_after_binary_operator = true;
				}
				break;
			case InfixExpression.OP_CONCAT:
				// following character must not numeric
				if (Character.isDigit(following)) {
					options_insert_space_after_binary_operator = true;
				}
				break;
			}
			walk(offset, token.getStart());

			spaceRequired = options_insert_space_before_binary_operator;
			if (split && options.wrap_before_binary_operator) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					if (forceSplit
							|| output.getPosition() + right.getLength()
									+ wrapDelta + op.length() >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			output.append(holder.getText(token).trim()); // operator
			spaceRequired = options_insert_space_after_binary_operator;
			if (split && !options.wrap_before_binary_operator) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					if (forceSplit
							|| output.getPosition() + right.getLength()
									+ wrapDelta + op.length() >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, infixExpression, offset, end);
		}

		walk(offset, right.getStart()); // ""
		right.accept(this);
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
		}
		walk(right.getEnd(), infixExpression.getEnd());
		return false;
	}

	private boolean visitConcat(InfixExpression infixExpression) {
		Expression left = infixExpression.getLeft();
		Expression right = infixExpression.getRight();

		int wrapStyle = getWrapStyle(options.alignment_for_concat_expression);
		int indentStyle = getIndentStyle(options.alignment_for_concat_expression);
		boolean forceSplit = isForceSplit(options.alignment_for_concat_expression);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 0
				+ boolDigit(options.insert_space_before_concat_operator)
				+ boolDigit(options.insert_space_after_concat_operator);
		boolean spaceRequired = false;

		walk(infixExpression.getStart(), left.getStart());
		left.accept(this);

		int offset = left.getEnd();
		int end = right.getStart();
		String op = InfixExpression.getOperator(infixExpression.getOperator());
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, op);
		if (token != null) {
			boolean options_insert_space_before_concat_operator = options.insert_space_before_concat_operator;
			boolean options_insert_space_after_concat_operator = options.insert_space_after_concat_operator;
			IDocument document = holder.getDocument();
			char following = Utils.getFollowingChar(document, token.getEnd());
			// following character must not numeric
			if (Character.isDigit(following)) {
				options_insert_space_after_concat_operator = true;
			}
			walk(offset, token.getStart());

			spaceRequired = options_insert_space_before_concat_operator;
			if (split && options.wrap_before_concat_operator) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					if (forceSplit
							|| output.getPosition() + right.getLength()
									+ wrapDelta + op.length() >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			output.append(holder.getText(token).trim()); // operator
			spaceRequired = options_insert_space_after_concat_operator;
			if (split && !options.wrap_before_concat_operator) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					if (forceSplit
							|| output.getPosition() + right.getLength()
									+ wrapDelta + op.length() >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, infixExpression, offset, end);
		}

		walk(offset, right.getStart()); // ""
		right.accept(this);
		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
		}
		walk(right.getEnd(), infixExpression.getEnd());
		return false;
	}

	@Override
	public boolean visit(InLineHtml inLineHtml) {
		try {
			String html = holder.getDocument().get(inLineHtml.getStart(),
					inLineHtml.getLength());
			output.appendRaw(html, false);
			globalOffset = Math.max(inLineHtml.getEnd(), globalOffset);
		} catch (BadLocationException e) {
			PEXUIPlugin.log(e);
		}
		return false;
	}

	@Override
	public boolean visit(InstanceOfExpression instanceOfExpression) {
		Expression expression = instanceOfExpression.getExpression();
		ClassName className = instanceOfExpression.getClassName();

		walk(instanceOfExpression.getStart(), expression.getStart());
		expression.accept(this);
		walk(expression.getEnd(), className.getStart()); // "instanceof"
		className.accept(this);
		walk(className.getEnd(), instanceOfExpression.getEnd());
		return false;
	}

	@Override
	public boolean visit(InterfaceDeclaration interfaceDeclaration) {
		Identifier name = interfaceDeclaration.getName();
		List<Identifier> interfaces = interfaceDeclaration.interfaces();
		Block body = interfaceDeclaration.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_type_declaration;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_type_declaration;
		blockData.options_indent_body = options.indent_body_declarations_compare_to_type_header;
		blockDataStack.push(blockData);

		walk(interfaceDeclaration.getStart(), name.getStart()); // "interface"
		name.accept(this);
		int offset = name.getEnd();

		if (!interfaces.isEmpty()) {
			int wrapStyle = getWrapStyle(options.alignment_for_superclass_in_type_declaration);
			int indentStyle = getIndentStyle(options.alignment_for_superclass_in_type_declaration);
			boolean forceSplit = isForceSplit(options.alignment_for_superclass_in_type_declaration);
			boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
			int wrap = 0;
			int wrapDelta = 1
					+ boolDigit(options.insert_space_before_comma_in_superinterfaces)
					+ boolDigit(options.insert_space_after_comma_in_superinterfaces);
			boolean spaceRequired = true;
			boolean wrapIndent = false;
			boolean wrapped = false;

			if (split && !forceSplit) {
				int width = output.getPosition() + " extends ".length();
				for (Identifier identifier : interfaces) {
					width += identifier.getLength();
				}
				if (interfaces.size() > 1) {
					width += (interfaces.size() - 1) * wrapDelta;
				}
				split = width > options.page_width;
			}

			int end = interfaces.get(0).getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_EXTENDS);
			if (token != null) {
				walk(offset, token.getStart());
				offset = token.getStart();
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						int width = output.getPosition() + "extends ".length();
						if (forceSplit || width > options.page_width) {
							output.newLine();
							spaceRequired = false;
							wrapped = true;
						}
						break;
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						// do not wrap here
						break;
					case Alignment.M_NEXT_SHIFTED_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
						output.newLine();
						spaceRequired = false;
						wrapped = true;
						break;
					}
				}
				output.spaceIf(spaceRequired);
				if (split && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
					output.markPosition();
				}
				walk(offset, token.getEnd()); // "extends"
				offset = token.getEnd();
				spaceRequired = true;
			} else {
				PEXUIPlugin.warning("extends", interfaceDeclaration,
						offset, end);
			}

			for (Identifier identifier : interfaces) {
				end = identifier.getStart();
				token = findToken(offset, end, TokenTypes.PHP_TOKEN, ",");
				if (token != null) {
					walk(offset, token.getStart()); // ""
					output.spaceIf(options.insert_space_before_comma_in_superinterfaces);
					output.append(",");
					spaceRequired = options.insert_space_after_comma_in_superinterfaces;
					if (split) {
						switch (wrapStyle) {
						case Alignment.M_NEXT_SHIFTED_SPLIT:
							if (!wrapIndent) {
								wrap++;
								output.wrap(+1);
								wrapIndent = true;
							}
							break;
						}
					}
					offset = token.getEnd();
				} else {
					// first [second to "extends"] element
					if (split) {
						switch (wrapStyle) {
						case Alignment.M_NEXT_SHIFTED_SPLIT:
							if (wrapped) {
								wrap++;
								output.wrap(+1);
								wrapIndent = true;
							}
							break;
						}
					}
				}
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						int width = output.getPosition()
								+ identifier.getLength() + wrapDelta;
						if (width > options.page_width) {
							output.newLine();
							spaceRequired = false;
						}
						break;
					case Alignment.M_NEXT_SHIFTED_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						output.newLine();
						spaceRequired = false;
						break;
					}
				}
				output.spaceIf(spaceRequired);
				walk(offset, identifier.getStart());
				identifier.accept(this);
				offset = identifier.getEnd();
			}
			if (split) {
				if (wrap > 0) {
					output.wrap(-wrap);
				}
				output.markPosition(0);
			}
		}

		walk(offset, body.getStart()); // ""
		body.accept(this);
		walk(body.getEnd(), interfaceDeclaration.getEnd()); // ""

		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {
		List<FormalParameter> formalParameters = lambdaFunctionDeclaration
				.formalParameters();
		List<Expression> lexicalVariables = lambdaFunctionDeclaration
				.lexicalVariables();
		Block body = lambdaFunctionDeclaration.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_method_declaration;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_method_declaration;
		blockData.options_indent_body = options.indent_statements_compare_to_body;
		blockDataStack.push(blockData);

		int wrapStyle = getWrapStyle(options.alignment_for_parameters_in_method_declaration);
		int indentStyle = getIndentStyle(options.alignment_for_parameters_in_method_declaration);
		boolean forceSplit = isForceSplit(options.alignment_for_parameters_in_method_declaration);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrap = 0;
		int wrapDelta = 1
				+ boolDigit(options.insert_space_before_comma_in_method_declaration_parameters)
				+ boolDigit(options.insert_space_after_comma_in_method_declaration_parameters);
		boolean spaceRequired = false;
		boolean wrapIndent = false;
		boolean wrapped = false;
		int markedPosition = 0;

		int offset = lambdaFunctionDeclaration.getStart();
		int end;
		if (!formalParameters.isEmpty()) {
			end = formalParameters.get(0).getStart();
		} else if (!lexicalVariables.isEmpty()) {
			end = lexicalVariables.get(0).getStart();
		} else {
			end = body.getStart();
		}
		Token token = findToken(offset, end, TokenTypes.PHP_FUNCTION);
		if (token != null) {
			walk(offset, token.getEnd()); // "function"
			offset = token.getEnd();
			output.space();
		} else {
			PEXUIPlugin.warning("function", lambdaFunctionDeclaration,
					offset, end);
		}
		if (lambdaFunctionDeclaration.isReference()) {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, "&");
			if (token != null) {
				walk(offset, token.getStart()); // "function"
				output.append(" &");
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("&", lambdaFunctionDeclaration, offset,
						end);
			}
		}

		boolean isFirst = true;
		for (FormalParameter formalParameter : formalParameters) {
			token = findToken(offset, formalParameter.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_method_declaration_parameters);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_method_declaration_parameters;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
							wrapped = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0) {
						output.wrap(wrap);
						wrapped = true;
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
							spaceRequired = false;
						}
						break;
					}
				}
			}
			walk(offset, formalParameter.getStart()); // ""
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition()
							+ formalParameter.getLength() + wrapDelta;
					if (width > options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
			}
			formalParameter.accept(this);
			offset = formalParameter.getEnd();
			isFirst = false;
		}

		if (!lexicalVariables.isEmpty()) {
			end = lexicalVariables.get(0).getStart();
		} else {
			end = body.getStart();
		}
		token = findToken(offset, end, TokenTypes.PHP_USE);
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.append(" " + holder.getText(token).trim() + " "); // "use"
			offset = token.getEnd();
		}
		spaceRequired = false;
		isFirst = true;
		for (Expression lexicalVariable : lexicalVariables) {
			token = findToken(offset, lexicalVariable.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_method_declaration_parameters);
				output.append(",");
				spaceRequired = options.insert_space_after_comma_in_method_declaration_parameters;
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!wrapIndent) {
							wrap++;
							output.wrap(+1);
							wrapIndent = true;
						}
						break;
					}
				}
				offset = token.getEnd();
			} else {
				// first element
				if (split) {
					wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (wrap > 0 && !wrapped) {
						output.wrap(wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
							spaceRequired = false;
						}
						break;
					}
				}
			}
			walk(offset, lexicalVariable.getStart()); // ""
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition()
							+ lexicalVariable.getLength() + wrapDelta;
					if (width > options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				if (markedPosition == 0) {
					markedPosition = output.getMarkedPosition();
				}
				output.markPosition();
			}
			lexicalVariable.accept(this);
			offset = lexicalVariable.getEnd();
			isFirst = false;
		}

		if (split) {
			if (wrap > 0) {
				output.wrap(-wrap);
			}
			output.markPosition(markedPosition);
		}

		if (body != null) {
			walk(offset, body.getStart()); // ")"
			body.accept(this);
			offset = body.getEnd();
		}

		walk(offset, lambdaFunctionDeclaration.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(ListVariable listVariable) {
		List<VariableBase> variables = listVariable.variables();

		int offset = listVariable.getStart();
		for (VariableBase variable : variables) {
			Token token = findToken(offset, variable.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_allocation_expression); // 代用
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_allocation_expression); // 代用
				offset = token.getEnd();
			} else {
				// skip first interface
			}
			walk(offset, variable.getStart()); // "list(" ","
			variable.accept(this);
			offset = variable.getEnd();
		}
		walk(offset, listVariable.getEnd()); // ")"
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		Comment comment = methodDeclaration.getComment();
		FunctionDeclaration function = methodDeclaration.getFunction();

		int offset = methodDeclaration.getStart();
		if (comment != null) {
			walk(offset, comment.getStart());
			comment.accept(this);
			offset = comment.getEnd();
		}
		walk(offset, function.getStart());
		function.accept(this);
		walk(function.getEnd(), methodDeclaration.getEnd());
		return false;
	}

	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		VariableBase dispatcher = methodInvocation.getDispatcher();
		FunctionInvocation method = methodInvocation.getMethod();

		int wrapStyle = getWrapStyle(options.alignment_for_selector_in_method_invocation);
		int indentStyle = getIndentStyle(options.alignment_for_selector_in_method_invocation);
		boolean forceSplit = isForceSplit(options.alignment_for_selector_in_method_invocation);
		boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
		int wrapDelta = 2
				+ boolDigit(options.insert_space_before_object_operator)
				+ boolDigit(options.insert_space_after_object_operator);
		boolean spaceRequired = false;
		int markedPosition = 0;
		ASTNode parent = methodInvocation.getParent();
		if (!(parent instanceof MethodInvocation || parent instanceof FieldAccess)
				|| methodInvocationStack.isEmpty()) {
			methodInvocationStack.push(new MethodInvocationData());
		}
		MethodInvocationData invocationData = methodInvocationStack.peek();
		invocationData.count++;
		if (invocationData.depth < invocationData.count) {
			invocationData.depth = invocationData.count;
		}

		walk(methodInvocation.getStart(), dispatcher.getStart());
		dispatcher.accept(this);
		boolean isFirst = invocationData.depth == invocationData.count;

		int offset = dispatcher.getEnd();
		int end = method.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_OBJECT_OPERATOR);
		if (token != null) {
			walk(offset, token.getStart()); // ""
			spaceRequired = options.insert_space_before_object_operator;
			if (split) {
				if (isFirst) {
					invocationData.wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
							: options.continuation_indentation;
					if (invocationData.wrap > 0) {
						output.wrap(invocationData.wrap);
					}
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
						if (forceSplit) {
							output.newLine();
						}
						break;
					}
				} else {
					switch (wrapStyle) {
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						if (!invocationData.wrapIndent) {
							invocationData.wrap++;
							output.wrap(+1);
							invocationData.wrapIndent = true;
						}
						break;
					}
				}
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
					int width = output.getPosition() + method.getLength()
							+ wrapDelta;
					if (forceSplit || width >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				case Alignment.M_ONE_PER_LINE_SPLIT:
				case Alignment.M_NEXT_SHIFTED_SPLIT:
					output.newLine();
					spaceRequired = false;
					break;
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (!isFirst) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			if (split && isFirst && indentStyle == Alignment.M_INDENT_ON_COLUMN) {
				markedPosition = output.getMarkedPosition();
				output.markPosition();
				invocationData.markedPosition = output.getMarkedPosition();
			}
			output.append("->");
			output.spaceIf(options.insert_space_after_object_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("->", methodInvocation, offset, end);
		}

		walk(offset, method.getStart()); // ""
		method.accept(this);
		walk(method.getEnd(), methodInvocation.getEnd());
		if (split) {
			if (--invocationData.count == 0) {
				if (invocationData.wrap > 0) {
					output.wrap(-invocationData.wrap);
				}
				methodInvocationStack.pop();
				if (methodInvocationStack.isEmpty()) {
					output.markPosition(markedPosition);
				} else {
					output.markPosition(methodInvocationStack.peek().markedPosition);
				}
			}
		}
		return false;
	}

	@Override
	public boolean visit(NamespaceDeclaration namespaceDeclaration) {
		NamespaceName name = namespaceDeclaration.getName();
		Block body = namespaceDeclaration.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_namespace_declaration;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_namespace_declaration;
		blockData.options_indent_body = options.indent_body_declarations_compare_to_namespace;
		blockDataStack.push(blockData);

		int offset = namespaceDeclaration.getStart();
		if (name != null) {
			walk(offset, name.getStart());
			name.accept(this);
			offset = name.getEnd();
		}

		walk(offset, body.getStart()); // ""
		body.accept(this);
		walk(body.getEnd(), namespaceDeclaration.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(NamespaceName namespaceName) {
		List<Identifier> segments = namespaceName.segments();

		int offset = namespaceName.getStart();
		for (Identifier segment : segments) {
			walk(offset, segment.getStart()); // "" "\"
			segment.accept(this);
			offset = segment.getEnd();
		}
		walk(offset, namespaceName.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(ParenthesisExpression parenthesisExpression) {
		Expression expression = parenthesisExpression.getExpression();

		int offset = parenthesisExpression.getStart();
		int end = (expression != null) ? expression.getStart()
				: parenthesisExpression.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_opening_paren_in_parenthesized_expression);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_parenthesized_expression);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", parenthesisExpression, offset, end);
		}

		if (expression != null) {
			walk(offset, expression.getStart());
			expression.accept(this);
			offset = expression.getEnd();
		}

		end = parenthesisExpression.getEnd();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_closing_paren_in_parenthesized_expression);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", parenthesisExpression, offset, end);
		}

		walk(offset, parenthesisExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(PostfixExpression postfixExpression) {
		VariableBase variable = postfixExpression.getVariable();

		walk(postfixExpression.getStart(), variable.getStart()); // ""
		variable.accept(this);

		int offset = variable.getEnd();
		int end = postfixExpression.getEnd();
		String op = postfixExpression.getOperationString();
		Token token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_postfix_operator);
			output.append(op); // "++" "--"
			output.spaceIf(options.insert_space_after_postfix_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, postfixExpression, offset, end);
		}
		walk(offset, postfixExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(PrefixExpression prefixExpression) {
		VariableBase variable = prefixExpression.getVariable();

		int offset = prefixExpression.getStart();
		int end = variable.getStart();
		String op = prefixExpression.getOperationString();
		Token token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_prefix_operator);
			output.append(op); // "++" "--"
			output.spaceIf(options.insert_space_after_prefix_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, prefixExpression, offset, end);
		}

		walk(offset, variable.getStart()); // ""
		variable.accept(this);
		walk(variable.getEnd(), prefixExpression.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Program program) {
		List<Statement> statements = program.statements();

		int offset = visit(statements, program.getStart(), program.getEnd());
		// ending comment is out of program
		int end = 0;
		for (Comment comment : program.comments()) {
			if (comment.getStart() >= program.getEnd()) {
				end = comment.getEnd();
			}
		}
		if (end > 0) {
			walk(offset, end);
			offset = end;
		}
		// check final trailing new lines
		Token token = holder.getToken(offset);
		if (token != null) {
			walk(offset, token.getEnd());
		}
		// eof
		if (options.insert_new_line_at_end_of_file_if_missing) {
			output.newLine();
		}
		return false;
	}

	@Override
	public boolean visit(Quote quote) {
		List<Expression> expressions = quote.expressions();

		int offset = quote.getStart();

		if (quote.getQuoteType() == Quote.QT_HEREDOC) {
			inHereDoc = true;
			int size = expressions.size();
			Expression[] expression = expressions.toArray(new Expression[size]);
			int end = 0;
			Token tag = holder.getToken(offset);
			if (tag != null
					&& tag.getTokenType().equals(TokenTypes.PHP_HEREDOC_TAG)) {
				end = tag.getEnd();
			}
			walk(offset, end); // HereDocTag
			offset = end;
			for (int i = 0; i < size - 1; i++) {
				walk(offset, expression[i].getStart()); // ""
				expression[i].accept(this);
				offset = expression[i].getEnd();
			}
			try {
				String value = holder.getDocument().get(offset,
						quote.getEnd() - offset);
				output.appendRaw(value, false);
				offset = quote.getEnd();
			} catch (BadLocationException e) {
				PEXUIPlugin.log(e);
			}
			walk(offset, quote.getEnd()); // ""
			inHereDoc = false;
			return false;
		}

		for (Expression expression : expressions) {
			walk(offset, expression.getStart()); // ""
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, quote.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Reference reference) {
		Expression expression = reference.getExpression();

		walk(reference.getStart(), expression.getStart());
		expression.accept(this);
		walk(expression.getEnd(), reference.getEnd());
		return false;
	}

	@Override
	public boolean visit(ReflectionVariable reflectionVariable) {
		Expression variable = reflectionVariable.getName();

		walk(reflectionVariable.getStart(), variable.getStart());
		variable.accept(this);
		walk(variable.getEnd(), reflectionVariable.getEnd());
		return false;
	}

	@Override
	public boolean visit(ReturnStatement returnStatement) {
		Expression expression = returnStatement.getExpression();

		int offset = returnStatement.getStart();
		if (expression != null) {
			walk(offset, expression.getStart()); // "return"
			if (expression instanceof ParenthesisExpression) {
				if (!options.insert_space_before_opening_paren_in_parenthesized_expression) {
					output.spaceIf(options.insert_space_before_parenthesized_expression_in_return);
				}
			} else {
				output.space();
			}
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, returnStatement.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Scalar scalar) {
		String value = scalar.getStringValue();
		if (inHereDoc) {
			output.appendRaw(value, false);
		} else if (Utils.hasCrLf(value)) {
			output.appendRaw(value, true);
		} else {
			output.append(value);
		}
		return false;
	}

	@Override
	public boolean visit(SingleFieldDeclaration singleFieldDeclaration) {
		Variable name = singleFieldDeclaration.getName();
		Expression value = singleFieldDeclaration.getValue();

		walk(singleFieldDeclaration.getStart(), name.getStart());
		name.accept(this);

		int offset = name.getEnd();
		if (value != null) {
			int wrapStyle = getWrapStyle(options.alignment_for_assignment);
			int indentStyle = getIndentStyle(options.alignment_for_assignment);
			boolean split = wrapStyle != Alignment.M_NO_ALIGNMENT;
			int wrap = 0;
			int wrapDelta = 1
					+ boolDigit(options.insert_space_before_assignment_operator)
					+ boolDigit(options.insert_space_after_assignment_operator);
			boolean spaceRequired = options.insert_space_after_assignment_operator;

			int end = value.getStart();
			Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "=");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_assignment_operator);
				output.append("=");
				// output.spaceIf(options.insert_space_after_assignment_operator);
				if (split) {
					switch (wrapStyle) {
					case Alignment.M_COMPACT_SPLIT:
					case Alignment.M_ONE_PER_LINE_SPLIT:
					case Alignment.M_NEXT_PER_LINE_SPLIT:
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						wrap = (indentStyle == Alignment.M_INDENT_BY_ONE) ? 1
								: options.continuation_indentation;
						if (wrap > 0) {
							output.wrap(wrap);
						}
						break;
					}
					switch (wrapStyle) {
					case Alignment.M_ONE_PER_LINE_SPLIT:
					case Alignment.M_NEXT_SHIFTED_SPLIT:
						output.newLine();
						spaceRequired = false;
						break;
					case Alignment.M_COMPACT_SPLIT:
					case Alignment.M_NEXT_PER_LINE_SPLIT:
						break;
					}
				}
				offset = token.getEnd();
			} else {
				PEXUIPlugin.warning("=", singleFieldDeclaration, offset,
						end);
			}
			walk(offset, value.getStart()); // "="
			if (split) {
				switch (wrapStyle) {
				case Alignment.M_COMPACT_SPLIT:
				case Alignment.M_NEXT_PER_LINE_SPLIT:
					if (output.getPosition() + value.getLength() + wrapDelta >= options.page_width) {
						output.newLine();
						spaceRequired = false;
					}
					break;
				}
			}
			output.spaceIf(spaceRequired);
			value.accept(this);
			if (split) {
				if (wrap > 0) {
					output.wrap(-wrap);
				}
			}
			offset = value.getEnd();
		}
		walk(offset, singleFieldDeclaration.getEnd());
		return false;
	}

	@Override
	public boolean visit(StaticConstantAccess classConstantAccess) {
		Expression className = classConstantAccess.getClassName();
		Identifier constant = classConstantAccess.getConstant();

		walk(classConstantAccess.getStart(), className.getStart());
		className.accept(this);

		int offset = className.getEnd();
		int end = constant.getStart();
		Token token = findToken(offset, end,
				TokenTypes.PHP_PAAMAYIM_NEKUDOTAYIM);
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_double_colon_operator);
			output.append("::");
			output.spaceIf(options.insert_space_after_double_colon_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("::", classConstantAccess, offset, end);
		}

		walk(offset, constant.getStart()); // ""
		constant.accept(this);
		walk(constant.getEnd(), classConstantAccess.getEnd());
		return false;
	}

	@Override
	public boolean visit(StaticFieldAccess staticFieldAccess) {
		Expression className = staticFieldAccess.getClassName();
		Variable field = staticFieldAccess.getField();

		walk(staticFieldAccess.getStart(), className.getStart());
		className.accept(this);

		int offset = className.getEnd();
		int end = field.getStart();
		Token token = findToken(offset, end,
				TokenTypes.PHP_PAAMAYIM_NEKUDOTAYIM);
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_double_colon_operator);
			output.append("::");
			output.spaceIf(options.insert_space_after_double_colon_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("::", staticFieldAccess, offset, end);
		}

		walk(offset, field.getStart()); // ""
		field.accept(this);
		walk(field.getEnd(), staticFieldAccess.getEnd());
		return false;
	}

	@Override
	public boolean visit(StaticMethodInvocation staticMethodInvocation) {
		Expression className = staticMethodInvocation.getClassName();
		FunctionInvocation method = staticMethodInvocation.getMethod();

		walk(staticMethodInvocation.getStart(), className.getStart());
		className.accept(this);

		int offset = className.getEnd();
		int end = method.getStart();
		Token token = findToken(offset, end,
				TokenTypes.PHP_PAAMAYIM_NEKUDOTAYIM);
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_double_colon_operator);
			output.append("::");
			output.spaceIf(options.insert_space_after_double_colon_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("::", staticMethodInvocation, offset, end);
		}

		walk(offset, method.getStart()); // ""
		method.accept(this);
		walk(method.getEnd(), staticMethodInvocation.getEnd());
		return false;
	}

	@Override
	public boolean visit(StaticStatement staticStatement) {
		List<Expression> expressions = staticStatement.expressions();

		int offset = staticStatement.getStart();
		for (Expression expression : expressions) {
			Token token = findToken(offset, expression.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart());
				output.spaceIf(options.insert_space_before_comma_in_multiple_field_declarations); // 代用
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_multiple_field_declarations); // 代用
				offset = token.getEnd();
			} else {
				// skip first expression
			}
			walk(offset, expression.getStart()); // "static" ""
			expression.accept(this);
			offset = expression.getEnd();
		}
		walk(offset, staticStatement.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(SwitchCase switchCase) {
		Expression value = switchCase.getValue();
		List<Statement> actions = switchCase.actions();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block_in_case;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockDataStack.push(blockData);

		int offset = switchCase.getStart();
		if (value != null) {
			walk(offset, value.getStart()); // "case"
			value.accept(this);
			offset = value.getEnd();
		}

		int end = (!actions.isEmpty()) ? actions.get(0).getStart() : switchCase
				.getEnd();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, ":");
		if (token == null) {
			token = findToken(offset, end, TokenTypes.PHP_SEMICOLON);
		}
		if (token != null) {
			walk(offset, token.getStart()); // ""
			if (switchCase.isDefault()) {
				output.spaceIf(options.insert_space_before_colon_in_default);
			} else {
				output.spaceIf(options.insert_space_before_colon_in_case);
			}
			if (token.getTokenType().equals(TokenTypes.PHP_SEMICOLON)) {
				output.append(";");
			} else {
				output.append(":");
			}
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(":/;", switchCase, offset, end);
		}
		if (!actions.isEmpty()) {
			walk(offset, end); // ""
			offset = end;
		}
		output.indentIf(options.indent_switchstatements_compare_to_cases);
		if (!actions.isEmpty()) {
			if (!(actions.get(0) instanceof Block)) {
				output.newLine();
			}
			offset = visit(actions, offset, switchCase.getEnd());
		} else {
			offset = trailer(offset);
		}
		walk(offset, switchCase.getEnd()); // ""
		output.unindentIf(options.indent_switchstatements_compare_to_cases);
		output.newLine();
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(SwitchStatement switchStatement) {
		Expression expression = switchStatement.getExpression();
		Block body = switchStatement.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_switch;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_switch;
		blockData.options_indent_body = options.indent_switchstatements_compare_to_switch;
		blockDataStack.push(blockData);

		int offset = switchStatement.getStart();
		int end = body.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "switch"
			output.spaceIf(options.insert_space_before_opening_paren_in_switch);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_switch);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", switchStatement, offset, end);
		}
		walk(offset, expression.getStart()); // ""

		expression.accept(this);
		offset = expression.getEnd();

		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_switch);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", switchStatement, offset, end);
		}

		walk(offset, body.getStart()); // ""
		body.accept(this);
		walk(body.getEnd(), switchStatement.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(ThrowStatement throwStatement) {
		Expression expression = throwStatement.getExpression();

		walk(throwStatement.getStart(), expression.getStart()); // "throw"
		if (expression instanceof ParenthesisExpression) {
			if (!options.insert_space_before_opening_paren_in_parenthesized_expression) {
				output.spaceIf(options.insert_space_before_parenthesized_expression_in_throw);
			}
		} else {
			output.space();
		}
		expression.accept(this);
		walk(expression.getEnd(), throwStatement.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(TryStatement tryStatement) {
		Block body = tryStatement.getBody();
		List<CatchClause> catchClauses = tryStatement.catchClauses();
		blockDataStack.push(null);

		walk(tryStatement.getStart(), body.getStart()); // "try"
		body.accept(this);
		int offset = body.getEnd();
		for (CatchClause catchClause : catchClauses) {
			walk(offset, catchClause.getStart()); // ""
			catchClause.accept(this);
			offset = catchClause.getEnd();
		}
		walk(offset, tryStatement.getEnd()); // ""
		blockDataStack.pop();
		return false;
	}

	@Override
	public boolean visit(UnaryOperation unaryOperation) {
		Expression expression = unaryOperation.getExpression();

		int offset = unaryOperation.getStart();
		int end = expression.getStart();
		String op = unaryOperation.getOperationString();
		Token token;
		if (op.equals("+") || op.equals("-") || op.equals("!")
				|| op.equals("~")) {
			token = findToken(offset, end, TokenTypes.PHP_TOKEN, op);
		} else {
			token = findToken(offset, end, TokenTypes.PHP_OPERATOR, op);
		}
		if (token != null) {
			walk(offset, token.getStart());
			output.spaceIf(options.insert_space_before_unary_operator);
			output.append(op); // "+" "-" "!" ...
			output.spaceIf(options.insert_space_after_unary_operator);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(op, unaryOperation, offset, end);
		}

		walk(offset, expression.getStart()); // ""
		expression.accept(this);
		walk(expression.getEnd(), unaryOperation.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(UseStatement useStatement) {
		
		List<UseStatementPart> parts = useStatement.parts();

		int offset = useStatement.getStart();
		for (UseStatementPart part : parts) {
			Token token = findToken(offset, part.getStart(),
					TokenTypes.PHP_TOKEN, ",");
			if (token != null) {
				walk(offset, token.getStart()); // ""
				output.spaceIf(options.insert_space_before_comma_in_superinterfaces); // 代用
				output.append(",");
				output.spaceIf(options.insert_space_after_comma_in_superinterfaces); // 代用
				offset = token.getEnd();
			} else {
				// skip first expression
			}
			walk(offset, part.getStart()); // "use" ""
			part.accept(this);
			offset = part.getEnd();
		}

		walk(offset, useStatement.getEnd()); // ";"
		return false;
	}

	@Override
	public boolean visit(UseStatementPart useStatementPart) {
		
		NamespaceName name = useStatementPart.getName();
		Identifier alias = useStatementPart.getAlias();

		walk(useStatementPart.getStart(), name.getStart()); // ""
		name.accept(this);
		int offset = name.getEnd();

		if (alias != null) {
			walk(offset, alias.getStart()); // "as"
			alias.accept(this);
			offset = alias.getEnd();
		}

		walk(offset, useStatementPart.getEnd()); // ""
		return false;
	}

	@Override
	public boolean visit(Variable variable) {
		Expression name = variable.getName();

		walk(variable.getStart(), name.getStart());
		name.accept(this);
		walk(name.getEnd(), variable.getEnd());
		return false;
	}

	@Override
	public boolean visit(WhileStatement whileStatement) {
		Expression condition = whileStatement.getCondition();
		Statement body = whileStatement.getBody();

		BlockData blockData = new BlockData();
		blockData.options_brace_position = options.brace_position_for_block;
		blockData.options_insert_space_before_opening_brace = options.insert_space_before_opening_brace_in_block;
		blockData.options_indent_body = options.indent_statements_compare_to_block;
		blockDataStack.push(blockData);

		int offset = whileStatement.getStart();
		int end = condition.getStart();
		Token token = findToken(offset, end, TokenTypes.PHP_TOKEN, "(");
		if (token != null) {
			walk(offset, token.getStart()); // "while"
			output.spaceIf(options.insert_space_before_opening_paren_in_while);
			output.append("(");
			output.spaceIf(options.insert_space_after_opening_paren_in_while);
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning("(", whileStatement, offset, end);
		}

		walk(offset, condition.getStart()); // ""
		condition.accept(this);
		offset = condition.getEnd();

		end = body.getStart();
		token = findToken(offset, end, TokenTypes.PHP_TOKEN, ")");
		if (token != null) {
			walk(offset, token.getStart()); // ""
			output.spaceIf(options.insert_space_before_closing_paren_in_while);
			output.append(")");
			offset = token.getEnd();
		} else {
			PEXUIPlugin.warning(")", whileStatement, offset, end);
		}

		walk(offset, body.getStart()); // ""
		offset = body.getEnd();
		if (body instanceof Block) {
			body.accept(this);
		} else {
			// single statement
			output.indentIf(options.indent_statements_compare_to_block);
			output.newLine();
			body.accept(this);
			offset = trailer(offset);
			output.unindentIf(options.indent_statements_compare_to_block);
			output.newLine();
		}

		walk(offset, whileStatement.getEnd()); // ";" "endwhile"
		blockDataStack.pop();
		return false;
	}
}
