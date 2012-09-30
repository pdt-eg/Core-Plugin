package org.pdtextensions.core.ui.formatter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.php.internal.core.documentModel.parser.PHPRegionContext;
import org.eclipse.php.internal.core.documentModel.parser.PHPTokenizer;
import org.eclipse.php.internal.core.documentModel.parser.regions.PhpScriptRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.parser.ContextRegionContainer;
import org.eclipse.wst.xml.core.internal.parser.regions.XMLCDataTextRegion;
import org.eclipse.wst.xml.core.internal.parser.regions.XMLContentRegion;
import org.pdtextensions.core.ui.PEXUIPlugin;

public class TokenHolder {

	private IDocument document;
	private IProject project;
	private Token[] tokens;

	public TokenHolder(IDocument document, IProject project) {
		this.document = document;
		this.project = project;
		this.tokens = tokenize(document, project);
	}

	public Token[] getTokens(int start, int end) {
		List<Token> list = new LinkedList<Token>();
		for (Token token : tokens) {
			if (token.getEnd() <= start) {
				continue;
			}
			if (token.getStart() >= end) {
				break;
			}
			list.add(token);
		}
		return list.toArray(new Token[list.size()]);
	}

	public Token getToken(int offset) {
		for (Token token : tokens) {
			if (token.getEnd() <= offset) {
				continue;
			}
			return token;
		}
		return null;
	}

	/**
	 * only for ASTFormatter.trailer(int)
	 */
	public Token getNextToken(int tokenIndex) {
		if (tokenIndex >= 0) {
			while (++tokenIndex < tokens.length) {
				return tokens[tokenIndex];
			}
		}
		return null;
	}

	public IDocument getDocument() {
		return document;
	}

	public IProject getProject() {
		return project;
	}

	public int getLineOffset(int offset) throws BadLocationException {
		return document.getLineInformationOfOffset(offset).getOffset();
	}

	public String getLineLeader(int offset) {
		if (offset >= 0 && offset < document.getLength()) {
			try {
				IRegion line = document.getLineInformationOfOffset(offset);
				return document
						.get(line.getOffset(), offset - line.getOffset());
			} catch (BadLocationException e) {
				PEXUIPlugin.log(e);
			}
		}
		return "";
	}

	public String getText(Token token) {
		try {
			return document.get(token.getStart(), token.getLength());
		} catch (BadLocationException e) {
			PEXUIPlugin.log(e);
		}
		return "";
	}

	public Token[] tokenize(IDocument document, IProject project) {
		PHPTokenizer tokenizer = new PHPTokenizer();
		tokenizer.setProject(project);
		tokenizer.reset(document.get().toCharArray());
		List<Token> list = new LinkedList<Token>();
		Iterator<?> it = tokenizer.getRegions().iterator();
		int[] index = new int[] { 0 };
		tokenize(it, 0, list, index);
		return list.toArray(new Token[list.size()]);
	}

	private void tokenize(Iterator<?> it, int baseOffset, List<Token> list,
			int[] index) {
		while (it.hasNext()) {
			ITextRegion textRegion = (ITextRegion) it.next();
			int offset = baseOffset + textRegion.getStart();
			if (textRegion instanceof PhpScriptRegion) {
				try {
					ITextRegion[] phpRegions = ((PhpScriptRegion) textRegion)
							.getPhpTokens(0, textRegion.getLength());
					for (ITextRegion phpRegion : phpRegions) {
						list.add(new Token(phpRegion, true, offset, index[0]++));
					}
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
			} else if (textRegion instanceof XMLCDataTextRegion) {
				list.add(new Token(textRegion, false, baseOffset, index[0]++));
				PHPTokenizer tokenizer = new PHPTokenizer();
				tokenizer.setProject(project);
				try {
					offset += textRegion.getLength();
					String restData = document.get(offset, document.getLength()
							- offset);
					tokenizer.reset(restData.toCharArray());
					Iterator<?> its = tokenizer.getRegions().iterator();
					tokenize(its, offset, list, index);
					return;
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
			} else if (textRegion instanceof ContextRegionContainer) {
				Iterator<?> itc = ((ContextRegionContainer) textRegion)
						.getRegions().iterator();
				tokenize(itc, offset, list, index);
			} else {
				list.add(new Token(textRegion, false, baseOffset, index[0]++));
			}
		}
	}

	public static boolean verify(IDocument prev, IDocument post,
			IProject project) {
		try {
			int prevPos = 0;
			int postPos = 0;
			char prevChar = 0;
			char postChar = 0;
			boolean matched = true;
			while (prevPos < prev.getLength() && postPos < post.getLength()) {
				while (prevPos < prev.getLength()) {
					prevChar = prev.getChar(prevPos++);
					if (!Character.isWhitespace(prevChar)) {
						break;
					}
					prevChar = 0;
				}
				while (postPos < post.getLength()) {
					postChar = post.getChar(postPos++);
					if (!Character.isWhitespace(postChar)) {
						break;
					}
					postChar = 0;
				}
				if (prevChar != postChar) {
					matched = false;
					break;
				}
			}
			if (matched) {
				while (prevPos < prev.getLength()) {
					prevChar = prev.getChar(prevPos++);
					if (!Character.isWhitespace(prevChar)) {
						matched = false;
						break;
					}
				}
				if (matched) {
					while (postPos < post.getLength()) {
						postChar = post.getChar(postPos++);
						if (!Character.isWhitespace(postChar)) {
							matched = false;
							break;
						}
					}
				}
			}
			if (!matched) {
				PEXUIPlugin.log(IStatus.ERROR, "content mismatch");
				return false;
			}
		} catch (BadLocationException e) {
		}

		TokenHolder prevHolder = new TokenHolder(prev, project);
		TokenHolder postHolder = new TokenHolder(post, project);
		Token[] prevTokens = prevHolder.getTokens(0, prev.getLength());
		Token[] postTokens = postHolder.getTokens(0, post.getLength());
		int prevIndex = 0;
		int postIndex = 0;
		while (prevIndex < prevTokens.length && postIndex < postTokens.length) {
			Token prevToken = prevTokens[prevIndex];
			Token postToken = postTokens[postIndex];
			TokenTypes prevType = prevToken.getTokenType();
			TokenTypes postType = postToken.getTokenType();
			if (!prevType.equals(postType)) {
				if (prevType.equals(TokenTypes.WHITESPACE)) {
					prevIndex++;
					continue;
				}
				if (postType.equals(TokenTypes.WHITESPACE)) {
					postIndex++;
					continue;
				}
				if (prevToken.getTextRegion() instanceof XMLContentRegion) {
					prevIndex++;
					continue;
				}
				if (postToken.getTextRegion() instanceof XMLContentRegion) {
					postIndex++;
					continue;
				}
				if (prevToken.getType().equals(PHPRegionContext.PHP_OPEN)) {
					prevIndex++;
					continue;
				}
				if (postToken.getType().equals(PHPRegionContext.PHP_OPEN)) {
					postIndex++;
					continue;
				}
				if (prevToken.getLength() == 0) {
					prevIndex++;
					continue;
				}
				if (postToken.getLength() == 0) {
					postIndex++;
					continue;
				}
				PEXUIPlugin.log(IStatus.ERROR, "token type mismatch");
				PEXUIPlugin.log(IStatus.ERROR, "prev(" + prevIndex + "):"
						+ prevType + "[" + prevToken.getStart() + ","
						+ prevToken.getLength() + "]" + ",post(" + postIndex
						+ "):" + postType);
				return false;
			}
			prevIndex++;
			postIndex++;
		}
		if (prevIndex != prevTokens.length || postIndex != postTokens.length) {
			boolean mismatch = false;
			for (; prevIndex < prevTokens.length; prevIndex++) {
				if (!prevTokens[prevIndex].isPhpToken()) {
					continue;
				}
				if (prevTokens[prevIndex].getTokenType().equals(
						TokenTypes.WHITESPACE)) {
					continue;
				}
				mismatch = true;
				break;
			}
			for (; postIndex < postTokens.length; postIndex++) {
				if (!postTokens[postIndex].isPhpToken()) {
					continue;
				}
				if (postTokens[postIndex].getTokenType().equals(
						TokenTypes.WHITESPACE)) {
					continue;
				}
				mismatch = true;
				break;
			}
			if (mismatch) {
				PEXUIPlugin.log(IStatus.ERROR, "token length mismatch");
				return false;
			}
		}
		return true;
	}
}
