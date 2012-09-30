package org.pdtextensions.core.ui;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class Utils {

	public static boolean hasCrLf(String str) {
		return str.contains("\r") || str.contains("\n");
	}

	public static String convertCrLf(String str) {
		return str.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
	}

	public static int countCrLf(String str) {
		str = convertCrLf(str);
		Pattern pattern = Pattern.compile("(\\n)", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	public static String ltrim(String str) {
		int i = 0;
		while (i < str.length()) {
			switch (str.charAt(i)) {
			case ' ':
			case '\t':
				i++;
				continue;
			}
			break;
		}
		return str.substring(i);
	}

	public static String rtrim(String str) {
		int i = str.length();
		while (i > 0) {
			switch (str.charAt(i - 1)) {
			case ' ':
			case '\t':
				i--;
				continue;
			}
			break;
		}
		return str.substring(0, i);
	}

	public static char getPrecedingChar(IDocument document, int offset) {
		char ch = 0;
		try {
			while (offset >= 0) {
				ch = document.getChar(offset);
				if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
					offset--;
					continue;
				}
				break;
			}
		} catch (BadLocationException e) {
		}
		return ch;
	}

	public static char getFollowingChar(IDocument document, int offset) {
		char ch = 0;
		try {
			while (offset < document.getLength()) {
				ch = document.getChar(offset);
				if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
					offset++;
					continue;
				}
				break;
			}
		} catch (BadLocationException e) {
		}
		return ch;
	}

	public static String convertTabsToSpaces(String string, int tabSize,
			boolean leaderOnly) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '\t':
				char[] spc = new char[tabSize - (buf.length() % tabSize)];
				Arrays.fill(spc, ' ');
				buf.append(new String(spc));
				break;
			case ' ':
				buf.append(c);
				break;
			default:
				if (leaderOnly) {
					buf.append(string.substring(i));
					i = string.length();
				} else {
					buf.append(c);
				}
			}
		}
		return buf.toString();
	}

}
