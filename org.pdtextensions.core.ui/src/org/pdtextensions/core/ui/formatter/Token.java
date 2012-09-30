package org.pdtextensions.core.ui.formatter;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class Token {

	private ITextRegion textRegion;
	private boolean isPhpToken;
	private int startOffset;
	private int index;

	public Token(ITextRegion region, boolean isPhp, int offset, int index) {
		this.textRegion = region;
		this.isPhpToken = isPhp;
		this.startOffset = offset;
		this.index = index;
	}

	public ITextRegion getTextRegion() {
		return textRegion;
	}

	public boolean isPhpToken() {
		return isPhpToken;
	}

	public TokenTypes getTokenType() {
		if (isPhpToken) {
			return TokenTypes.valueOf(textRegion.getType());
		} else {
			return TokenTypes.UNKNOWN_TOKEN;
		}
	}

	public String getType() {
		return textRegion.getType();
	}

	public int getStart() {
		return textRegion.getStart() + startOffset;
	}

	public int getEnd() {
		return textRegion.getEnd() + startOffset;
	}

	public int getLength() {
		return textRegion.getLength();
	}

	public int getTextLength() {
		return textRegion.getTextLength();
	}

	public int getTokenIndex() {
		return index;
	}

	public boolean isPHPDoc() {
		switch (this.getTokenType()) {
		case PHPDOC_ABSTRACT:
		case PHPDOC_ACCESS:
		case PHPDOC_AUTHOR:
		case PHPDOC_CATEGORY:
		case PHPDOC_COMMENT:
		case PHPDOC_COMMENT_END:
		case PHPDOC_COMMENT_START:
		case PHPDOC_COPYRIGHT:
		case PHPDOC_DEPRECATED:
		case PHPDOC_DESC:
		case PHPDOC_EXAMPLE:
		case PHPDOC_EXCEPTION:
		case PHPDOC_FILESOURCE:
		case PHPDOC_FINAL:
		case PHPDOC_GLOBAL:
		case PHPDOC_IGNORE:
		case PHPDOC_INTERNAL:
		case PHPDOC_LICENSE:
		case PHPDOC_LINK:
		case PHPDOC_MAGIC:
		case PHPDOC_METHOD:
		case PHPDOC_NAME:
		case PHPDOC_PACKAGE:
		case PHPDOC_PARAM:
		case PHPDOC_PROPERTY:
		case PHPDOC_RETURN:
		case PHPDOC_SEE:
		case PHPDOC_SINCE:
		case PHPDOC_STATIC:
		case PHPDOC_STATICVAR:
		case PHPDOC_SUBPACKAGE:
		case PHPDOC_THROWS:
		case PHPDOC_TODO:
		case PHPDOC_TUTORIAL:
		case PHPDOC_USES:
		case PHPDOC_VAR:
		case PHPDOC_VERSION:
			return true;
		}
		return false;
	}
}
