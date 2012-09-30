/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * Modified by Marek Maksimczyk <marek.maksimczyk@mandos.net.pl>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.codemanipulation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.ui.CodeGeneration;
import org.pdtextensions.core.util.PDTModelUtils;

public class MethodStub {

	private String lineDelimiter = "\n";

	private String code = "";
	private IScriptProject scriptProject = null;

	private IMethod method;
	private boolean generateComments;

	public MethodStub(IScriptProject scriptProject, IMethod method, boolean generateComments) {
		this.scriptProject = scriptProject;
		this.method = method;
		this.generateComments = generateComments;
	}

	private void generateCode() throws CoreException {

		StringBuilder buffer = new StringBuilder();
		buffer.append(lineDelimiter);
		buffer.append(generateComments());
		buffer.append(lineDelimiter);
		buffer.append(generateAccess());
		buffer.append(" function " + method.getElementName());
		buffer.append("(" + generateParamsWithTypes() + ")");
		buffer.append(generateBody());

		code = buffer.toString();
	}

	private String generateParamsWithoutTypes() {
		return generateParams(false);
	}
	
	private String generateParamsWithTypes() {
		return generateParams(true);
	}
	
	private String generateParams(boolean withTypes) {
		String code = "";
		try {
			for (IParameter parameter : method.getParameters()) {
				if (withTypes == true && parameter.getType() != null) {
					code += parameter.getType();
				}
				code += " ";
				code += parameter.getName();
				if (parameter.getDefaultValue() != null) {
					code += "=" + parameter.getDefaultValue();
				}
				code += ",";
			}
			// Remove last ",", but only when we have some parameters.
			if (code.length() > 0) {
				code = code.substring(0, code.length() - 1);
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return code;
	}

	private String generateBody() {

		String body = "";
		body = "{ " + lineDelimiter + "// TODO Auto-generated method stub " + lineDelimiter;

		try {
			if (method.isConstructor()) {
				body += "parent::__construct(" + generateParamsWithoutTypes() + ");" + lineDelimiter;
			}
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		body += "}" + lineDelimiter;
		return body;
	}


	private String generateAccess() {
		String access = new String();
		try {
			if (PHPFlags.isPublic(method.getFlags())) {
				access = "public";
			} else if (PHPFlags.isProtected(method.getFlags())) {
				access = "protected";
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}

		return access;
	}

	private String generateComments() {
		String comment = "";
		if (generateComments == true) {
			try {
				comment = CodeGeneration.getMethodComment(scriptProject,
					null,
					method.getElementName(),
					null,
					null,
					null,
					method,
					lineDelimiter,
					null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return comment;
	}

	public String toString() {
		try {
			generateCode();
		} catch (ModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return code;
	}

	/**
	 * Retrieve the code stub for a given {@link IMethod}
	 */
	public static String getMethodStub(String parent, IMethod method, String indent, String lineDelim, boolean comments)
			throws ModelException {

		StringBuilder buffer = new StringBuilder();
		String comment = null;
		if (comments) {
			try {
				comment = org.eclipse.php.ui.CodeGeneration.getMethodComment(method, null, lineDelim);
				comment = indentPattern(lineDelim + comment, indent, lineDelim);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (comments && comment != null) {
			buffer.append(comment);
		}

		String modifier = "public";

		try {
			if (PHPFlags.isPrivate(method.getFlags())) {
				modifier = "private";
			} else if (PHPFlags.isProtected(method.getFlags())) {
				modifier = "protected";
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}

		String signatureIndent = comments ? "" : indent;
		buffer.append(signatureIndent + modifier + " function ");

		String methodName = method.isConstructor() ? parent : method.getElementName();
		buffer.append(methodName);
		buffer.append("(");

		int i = 0;
		int size = method.getParameters().length;

		for (IParameter param : method.getParameters()) {

			if (PDTModelUtils.isValidType(param.getType(), method.getScriptProject())) {
				buffer.append(param.getType() + " ");
			}

			buffer.append(param.getName());

			if (param.getDefaultValue() != null) {
				if ("array".equals(param.getType()) && param.getDefaultValue().trim().length() == 0) {
					buffer.append(" = array()");
				} else {
					buffer.append(" = " + param.getDefaultValue());
				}
			}
			if (i++ < size - 1) {
				buffer.append(", ");
			}
		}

		buffer.append(") {");
		buffer.append(lineDelim);

		buffer.append(indent + indent + "// TODO: Auto-generated method stub");
		buffer.append(lineDelim);
		buffer.append(lineDelim);

		buffer.append(indent + "}");
		buffer.append(lineDelim);

		return buffer.toString();

	}

	private static String indentPattern(String originalPattern, String indentation, String lineDelim) {

		String delimPlusIndent = lineDelim + indentation;
		String indentedPattern = originalPattern.replaceAll(lineDelim, delimPlusIndent) + delimPlusIndent;

		return indentedPattern;
	}

}
