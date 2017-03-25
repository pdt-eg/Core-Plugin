/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.util;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.php.core.compiler.IPHPModifiers;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.core.compiler.ast.nodes.PHPDocBlock;
import org.eclipse.php.core.compiler.ast.nodes.PHPDocTag;
import org.eclipse.php.core.compiler.ast.nodes.PHPFieldDeclaration;
import org.eclipse.php.core.compiler.ast.nodes.PHPModuleDeclaration;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.php.ui.CodeGeneration;
import org.pdtextensions.core.util.Inflector;
import org.pdtextensions.core.util.PDTModelUtils;

/**
 * 
 * Utility class for getter/setter generation.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class GetterSetterUtil {

	public static final String GET = "get";
	public static final String SET = "set";
	private static final String[] bin = { "bool", "int", "integer", "string" };

	private static class FieldReferenceParser extends PHPASTVisitor {

		private final IField field;

		private TypeReference reference = null;

		public FieldReferenceParser(IField field) {

			this.field = field;
		}

		public boolean visit(PHPFieldDeclaration s) throws Exception {

			if (s.getName().equals(field.getElementName())) {
				PHPDocBlock doc = s.getPHPDoc();
				if (doc != null) {
					if (doc.getTags().length == 1) {
						PHPDocTag[] tags = doc.getTags();
						if (tags[0].getTypeReferences().size() == 1) {
							List<TypeReference> refs = tags[0].getTypeReferences();
							if (refs.size() == 1) {
								reference = refs.get(0);
							}
						}
					}
				}
				return false;
			}
			return true;
		}

		public TypeReference getReference() {

			return reference;
		}
	}

	public static String getTypeReference(final IField field) {

		String type = null;

		try {

			PHPModuleDeclaration module = (PHPModuleDeclaration) SourceParserUtil.parse(field.getSourceModule(), null);
			FieldReferenceParser typeParser = new FieldReferenceParser(field);
			module.traverse(typeParser);

			if (typeParser.getReference() != null) {
				TypeReference ref = typeParser.getReference();
				type = ref.getName();

				if (!PDTModelUtils.isValidType(type, field.getScriptProject())) {
					type = "";
				}
			}

		} catch (Exception e1) {
			// Logger.logException(e1);
		}

		return type;

	}

	public static String getSetterName(final IField field) {

		String name = prepareField(field);
		return SET + name;

	}

	public static String getGetterName(IField field) {

		String name = prepareField(field);
		return GET + name;

	}

	private static String prepareField(IField field) {

		String name = field.getElementName().replace("$", "");
		StringBuffer buffer = new StringBuffer(name);
		while (buffer.length() > 0 && buffer.charAt(0) == '_') {
			buffer.deleteCharAt(0);
		}
		buffer.replace(0, 1, Character.toString(Character.toUpperCase(buffer.charAt(0))));
		
		String prepared = buffer.toString();
		if (prepared.contains("_")) {
			prepared = Inflector.camelCase(prepared);
		}

		return prepared;

	}

	public static String getFieldName(IField iField) {

		Assert.isNotNull(iField);
		return iField.getElementName().replace("$", "");

	}

	/**
	 * Create a stub for a getter of the given field using getter/setter
	 * templates. The resulting code has to be formatted and indented.
	 * 
	 * @param field
	 *            The field to create a getter for
	 * @param getterName
	 *            The chosen name for the getter
	 * @param addComments
	 *            If <code>true</code>, comments will be added.
	 * @param modifiers
	 *            The modifiers signaling visibility, if static, synchronized or
	 *            final
	 * @return Returns the generated stub.
	 * @throws CoreException
	 */
	public static String getGetterStub(IField field, String getterName, boolean addComments, int flags, String indent)
			throws CoreException {
		String fieldName = field.getElementName();
		boolean isStatic = PHPFlags.isStatic(field.getFlags());
		IType parentType = field.getDeclaringType();

		String typeName = "function";
		String accessorName = field.getType();

		if (accessorName == null)
			accessorName = "unknown_type";

		String lineDelim = "\n"; // Use default line delimiter, as //$NON-NLS-1$
									// generated stub has to be formatted anyway
		StringBuffer buf = new StringBuffer();
		if (addComments) {
			String comment = CodeGeneration.getGetterComment(field.getScriptProject(),
					parentType.getFullyQualifiedName(), getterName, field.getElementName(), typeName, accessorName,
					lineDelim);
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
		}

		buf.append(PHPFlags.toString(flags | (isStatic ? IPHPModifiers.AccStatic : 0)));
		buf.append(' ');

		buf.append(typeName);
		buf.append(' ');
		buf.append(getterName);
		buf.append("() {"); //$NON-NLS-1$
		buf.append(lineDelim);
		if (isStatic) {
			fieldName = "self::" + fieldName; //$NON-NLS-1$
		} else {
			fieldName = "$this->" + fieldName.replace("$", ""); //$NON-NLS-1$
		}

		String body = org.eclipse.php.ui.CodeGeneration.getGetterMethodBodyContent(field.getScriptProject(),
				parentType.getTypeQualifiedName(), getterName, fieldName, lineDelim);
		if (body != null) {
			buf.append(indent + body);
		}

		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		return buf.toString();
	}

	/**
	 * Create a stub for a getter of the given field using getter/setter
	 * templates. The resulting code has to be formatted and indented.
	 * 
	 * @param field
	 *            The field to create a getter for
	 * @param setterName
	 *            The chosen name for the setter
	 * @param addComments
	 *            If <code>true</code>, comments will be added.
	 * @param modifiers
	 *            The modifiers signaling visibility, if static, synchronized or
	 *            final
	 * @param fluent
	 * @return Returns the generated stub.
	 * @throws CoreException
	 */
	public static String getSetterStub(IField field, String setterName, String typeName, boolean addComments, int flags,
			String indent, boolean fluent) throws CoreException {

		String fieldName = field.getElementName();
		IType parentType = field.getDeclaringType();
		boolean isStatic = PHPFlags.isStatic(field.getFlags());
		IScriptProject project = field.getScriptProject();
		String accessorName = fieldName;

		String commentTypeName = typeName != null ? typeName : "unknown_type";

		String lineDelim = "\n"; // Use default line delimiter, as //$NON-NLS-1$
									// generated stub has to be formatted anyway
		StringBuffer buf = new StringBuffer();
		if (addComments) {
			String comment = org.eclipse.php.ui.CodeGeneration.getSetterComment(project,
					parentType.getFullyQualifiedName(), setterName, field.getElementName(), commentTypeName, fieldName,
					accessorName, lineDelim);
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
		}
		buf.append(PHPFlags.toString(flags | (isStatic ? IPHPModifiers.AccStatic : 0)));
		buf.append(' ');

		buf.append("function "); //$NON-NLS-1$
		buf.append(setterName);
		buf.append('(');

		if (typeName != null) {
			buf.append(typeName);
			buf.append(' ');
		}

		buf.append(fieldName);
		buf.append(") {"); //$NON-NLS-1$
		buf.append(lineDelim);

		String argname = fieldName;
		if (isStatic) {
			fieldName = "self::" + fieldName; //$NON-NLS-1$;
		} else {
			fieldName = "$this->" + fieldName.replace("$", ""); //$NON-NLS-1$
		}
		

		String body = org.eclipse.php.ui.CodeGeneration.getSetterMethodBodyContent(project,
				parentType.getFullyQualifiedName(), setterName, fieldName, argname, lineDelim);
		if (body != null) {
			buf.append(indent + body);
		}

		if (fluent && !isStatic) {
			buf.append(lineDelim);
			buf.append(indent + "return $this;");
		}

		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		return buf.toString();
	}

}
