/*******************************************************************************
 * Copyright (c) 2005, 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.core.ui.formatter.profile;

import org.pdtextensions.core.ui.formatter.CodeFormatterConstants;
import org.pdtextensions.core.ui.formatter.CodeFormatterOptions;

/**
 * @since 0.17.0
 * @see https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-2-coding-style-guide.md
 */
public class PSR2FormatterOptions extends CodeFormatterOptions {
	
	public static CodeFormatterOptions getDefaultSettings() {
		PSR2FormatterOptions options = new PSR2FormatterOptions();
		options.setDefaultSettings();
		
		return options;
	}

	@Override
	public void setDefaultSettings() {

		super.setDefaultSettings();
		this.tab_char = SPACE;
		this.tab_size = 4;
		this.brace_position_for_type_declaration = CodeFormatterConstants.NEXT_LINE;
		this.brace_position_for_method_declaration = CodeFormatterConstants.NEXT_LINE;
		this.brace_position_for_constructor_declaration = CodeFormatterConstants.NEXT_LINE;
		this.insert_new_line_after_namespace_declaration = true;
		this.blank_lines_before_first_class_body_declaration = 1;
	}	
}
