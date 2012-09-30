package org.pdtextensions.core.ui.formatter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



public class OutputBuffer {

	private CodeFormatterOptions options;
	private List<StringBuffer> outputBuffer;
	private StringBuffer lineBuffer;
	private int lineLeader;
	private int indentLevel;
	private int wrapLevel;
	private int blankLines;
	private int markedPosition;
	private int lineNumber;

	private static final char TAB_CHAR = '\t';
	private static final char SPACE_CHAR = ' ';

	public OutputBuffer(CodeFormatterOptions options) {
		outputBuffer = new LinkedList<StringBuffer>();
		lineBuffer = new StringBuffer();
		this.options = options;
		lineLeader = 0;
		indentLevel = 0;
		indent(0);
		wrapLevel = 0;
		wrap(0);
		blankLines = 0;
		markedPosition = 0;
		lineNumber = 0;
	}

	public void setLineLeader(int length) {
		lineLeader = length;
	}

	public String getOutput() {
		StringBuffer buf = new StringBuffer();
		for (StringBuffer line : outputBuffer) {
			buf.append(line.toString());
		}
		buf.append(lineBuffer.toString());
		return buf.toString();
	}

	public OutputBuffer append(String str) {
		if (str.equals("")) {
			return this;
		}
		if (Utils.hasCrLf(str)) {
			str = Utils.convertCrLf(str);
			String[] strings = str.split("\\n");
			for (int i = 0; i < strings.length; i++) {
				append(strings[i]);
				if (i < strings.length - 1) {
					newLine();
				}
			}
			if (str.endsWith("\n")) {
				newLine();
			}
		} else {
			if (lineBuffer.length() == 0) {
				lineBuffer.append(lineHeader());
			}
			lineBuffer.append(str);
		}
		return this;
	}

	public OutputBuffer appendRaw(String str, boolean head) {
		if (str.equals("")) {
			return this;
		}
		if (Utils.hasCrLf(str)) {
			str = Utils.convertCrLf(str);
			String[] strings = str.split("\\n");
			for (int i = 0; i < strings.length; i++) {
				if (head && i == 0 && lineBuffer.length() == 0) {
					lineBuffer.append(lineHeader());
				}
				lineBuffer.append(strings[i]);
				if (i < strings.length - 1) {
					newLine(true);
				}
			}
			if (str.endsWith("\n")) {
				newLine(true);
			}
		} else {
			lineBuffer.append(str);
		}
		return this;
	}

	public OutputBuffer join(String str) {
		if (lineBuffer.toString().trim().length() == 0) {
			int index = outputBuffer.size() - 1;
			if (index >= 0) {
				lineBuffer = outputBuffer.get(index);
				lineBuffer.setLength(lineBuffer.length()
						- options.line_separator.length());
				outputBuffer.remove(index);
			}
		}
		space();
		append(str);
		return this;
	}

	public OutputBuffer space() {
		if (!lineBuffer.toString().endsWith(" ")) {
			lineBuffer.append(" ");
		}
		return this;
	}

	public OutputBuffer spaceIf(boolean bool) {
		return bool ? space() : this;
	}

	public OutputBuffer newLine() {
		return newLine(false);
	}

	public OutputBuffer newLineIf(boolean bool) {
		return bool ? newLine(false) : this;
	}

	public OutputBuffer newLine(boolean force) {
		if (force || lineBuffer.length() > 0) {
			if (lineBuffer.length() == 0) {
				if (blankLines >= options.number_of_empty_lines_to_preserve) {
					return this;
				}
				blankLines++;
				if (options.indent_empty_lines) {
					lineBuffer.append(lineHeader());
				}
			} else {
				blankLines = 0;
			}
			lineBuffer.append(options.line_separator);
			outputBuffer.add(new StringBuffer(lineBuffer.toString()));
			lineNumber++;
		}
		lineBuffer.setLength(0);
		return this;
	}

	public boolean isNewLine() {
		return lineBuffer.toString().trim().length() == 0;
	}

	public void blankLines(int lines) {
		int index = outputBuffer.size();
		while (index-- > 0) {
			if (outputBuffer.get(index).toString().trim().equals("")) {
				lines--;
			} else {
				break;
			}
		}
		if (lines > 0) {
			StringBuffer tempBuffer = new StringBuffer();
			tempBuffer.setLength(0);
			if (options.indent_empty_lines) {
				tempBuffer.append(lineHeader());
			}
			tempBuffer.append(options.line_separator);
			while (lines-- > 0) {
				outputBuffer.add(new StringBuffer(tempBuffer.toString()));
				lineNumber++;
			}
		}
	}

	public int wrap(int delta) {
		wrapLevel += delta;
		if (wrapLevel < 0) {
			wrapLevel = 0;
		}
		return wrapLevel;
	}

	public int indent(int delta) {
		indentLevel += delta;
		if (indentLevel < 0) {
			indentLevel = 0;
		}
		return indentLevel;
	}

	public int indentIf(boolean bool) {
		if (bool) {
			return indent(+1);
		}
		return indentLevel;
	}

	public int unindentIf(boolean bool) {
		if (bool) {
			return indent(-1);
		}
		return indentLevel;
	}

	public void markPosition() {
		markedPosition = getPosition();
	}

	public void markPosition(int pos) {
		markedPosition = pos;
	}

	public int getMarkedPosition() {
		return markedPosition;
	}

	public int getPosition() {
		int length = 0;
		StringBuffer temp = new StringBuffer();
		if (lineBuffer.length() == 0) {
			temp.append(lineHeader());
		} else {
			temp.append(lineBuffer);
		}
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == TAB_CHAR) {
				length += options.tab_size;
			} else {
				length++;
			}
		}
		return length;
	}

	public String tabs(int size) {
		char[] tab = new char[size];
		Arrays.fill(tab, TAB_CHAR);
		return new String(tab);
	}

	public String spaces(int size) {
		char[] spc = new char[size];
		Arrays.fill(spc, SPACE_CHAR);
		return new String(spc);
	}

	public String lineHeader() {
		StringBuffer header = new StringBuffer();
		String indentString = spaces(options.tab_size * indentLevel);
		String wrapString = spaces(options.tab_size * wrapLevel);
		StringBuffer buffer = new StringBuffer();
		buffer.append(spaces(lineLeader));
		buffer.append(indentString);
		switch (options.tab_char) {
		case CodeFormatterOptions.TAB:
			if (!options.use_tabs_only_for_leading_indentations) {
				buffer.append(wrapString);
				if (markedPosition > buffer.length()) {
					buffer.append(spaces(markedPosition - buffer.length()));
				}
			}
			if (options.tab_size > 0) {
				header.append(tabs(buffer.length() / options.tab_size));
				header.append(spaces(buffer.length() % options.tab_size));
			} else {
				header.append(spaces(buffer.length()));
			}
			if (options.use_tabs_only_for_leading_indentations) {
				header.append(wrapString);
				if (markedPosition > header.length()) {
					header.append(spaces(markedPosition - header.length()));
				}
			}
			break;
		case CodeFormatterOptions.SPACE:
			buffer.append(wrapString);
			if (markedPosition > buffer.length()) {
				buffer.append(spaces(markedPosition - buffer.length()));
			}
			header.append(buffer.toString());
			break;
		}
		return header.toString();
	}

	public OutputBuffer appendToLeader(String text) {
		StringBuffer buffer = new StringBuffer();
		switch (options.tab_char) {
		case CodeFormatterOptions.TAB:
			if (options.tab_size > 0) {
				buffer.append(tabs(lineLeader / options.tab_size));
				buffer.append(spaces(lineLeader % options.tab_size));
			} else {
				buffer.append(spaces(lineLeader));
			}
			break;
		case CodeFormatterOptions.SPACE:
			buffer.append(spaces(lineLeader));
			break;
		}
		buffer.append(text);
		if (lineBuffer.length() > 0) {
			newLine();
		}
		lineBuffer.append(buffer.toString());
		return this;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
