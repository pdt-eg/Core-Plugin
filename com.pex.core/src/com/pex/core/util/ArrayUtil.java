package com.pex.core.util;

public class ArrayUtil {

	public static String implode(String glue, String[] inputArray) {

		StringBuilder sb = new StringBuilder();
		sb.append(inputArray[0]);

		for (int i = 1; i < inputArray.length; i++) {
			sb.append(glue);
			sb.append(inputArray[i]);
		}

		return sb.toString();
	}
}
