/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.pdtextensions.core.ui.formatter;

/**
 * Specification for a generic source code formatter.
 *
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class CodeFormatter {

	public static final String EMPTY_STRING = new String(new char[0]);

	/**
	 * Unknown kind
	 *<p>
	 * <b>Since 3.6</b>, if the corresponding comment options are set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 * </p>
	 */
	public static final int K_UNKNOWN = 0x00;

	/**
	 * Kind used to format an expression
	 * <p>
	 * Note that using this constant, the comments encountered while formatting
	 * the expression may be shifted to match the correct indentation but are not
	 * formatted.
	 * </p><p>
	 * <b>Since 3.6</b>, if the corresponding comment options are set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 * </p>
	 */
	public static final int K_EXPRESSION = 0x01;

	/**
	 * Kind used to format a set of statements
	 * <p>
	 * Note that using this constant, the comments encountered while formatting
	 * the statements may be shifted to match the correct indentation but are not
	 * formatted.
	 * </p><p>
	 * <b>Since 3.6</b>, if the corresponding comment options are set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 * </p>
	 */
	public static final int K_STATEMENTS = 0x02;

	/**
	 * Kind used to format a set of class body declarations
	 * <p>
	 * Note that using this constant, the comments encountered while formatting
	 * the body declarations may be shifted to match the correct indentation but
	 * are not formatted.
	 * </p><p>
	 * <b>Since 3.6</b>, if the corresponding comment options are set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 * </p>
	 */
	public static final int K_CLASS_BODY_DECLARATIONS = 0x04;

	/**
	 * Kind used to format a compilation unit
	 * <p>
	 * Note that using this constant, the comments are only indented while
	 * formatting the compilation unit.
	 * </p><p>
	 * <b>Since 3.4</b>, if the corresponding comment option is set to
	 * <code>true</code> then it is also possible to format the comments on the fly
	 * by adding the {@link #F_INCLUDE_COMMENTS} flag to this kind of format.
	 * </p>
	 */
	public static final int K_COMPILATION_UNIT = 0x08;

	/**
	 * Kind used to format a single-line comment
	 * @since 3.1
	 */
	public static final int K_SINGLE_LINE_COMMENT = 0x10;

	/**
	 * Kind used to format a multi-line comment
	 * @since 3.1
	 */
	public static final int K_MULTI_LINE_COMMENT = 0x20;

	/**
	 * Kind used to format a Javadoc comment
	 *
	 * @since 3.1
	 */
	public static final int K_JAVA_DOC = 0x40;

}
