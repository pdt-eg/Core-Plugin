/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     John Kaplan, johnkaplantech@gmail.com - 108071 [code templates] template for body of newly created class
 *     Sebastian Davids, sdavids@gmx.de - 187316 [preferences] Mark Occurences Pref Page; Link to
 *     Benjamin Muskalla <b.muskalla@gmx.net> - [preferences] Add preference for new compiler warning: MissingSynchronizedModifierInInheritedMethod - https://bugs.eclipse.org/bugs/show_bug.cgi?id=245240
 *     Guven Demir <guven.internet+eclipse@gmail.com> - [package explorer] Alternative package name shortening: abbreviation - https://bugs.eclipse.org/bugs/show_bug.cgi?id=299514
 *******************************************************************************/
package org.pdtextensions.core.ui.preferences;

import org.eclipse.osgi.util.NLS;

public final class PreferencesMessages extends NLS {

	private static final String BUNDLE_NAME = PreferencesMessages.class
			.getName();

	private PreferencesMessages() {
		// Do not instantiate
	}

	public static String CodeFormatterPreferencePage_title;
	public static String CodeFormatterPreferencePage_description;
	public static String PropertyAndPreferencePage_useworkspacesettings_change;
	public static String PropertyAndPreferencePage_showprojectspecificsettings_label;
	public static String PropertyAndPreferencePage_useprojectsettings_label;
	public static String IndentGuidePreferencePage_description;
	public static String IndentGuidePreferencePage_enabled_label;
	public static String IndentGuidePreferencePage_group_label;
	public static String IndentGuidePreferencePage_alpha_label;
	public static String IndentGuidePreferencePage_style_label;
	public static String IndentGuidePreferencePage_style_solid;
	public static String IndentGuidePreferencePage_style_dash;
	public static String IndentGuidePreferencePage_style_dot;
	public static String IndentGuidePreferencePage_style_dash_dot;
	public static String IndentGuidePreferencePage_style_dash_dot_dot;
	public static String IndentGuidePreferencePage_width_label;
	public static String IndentGuidePreferencePage_shift_label;
	public static String IndentGuidePreferencePage_color_label;
	public static String IndentGuidePreferencePage_group2_label;
	public static String IndentGuidePreferencePage_draw_left_end_label;
	public static String IndentGuidePreferencePage_draw_blank_line_label;
	public static String IndentGuidePreferencePage_skip_comment_block_label;	

	public static String ConfigurationBlockFiller_link_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, PreferencesMessages.class);
	}
}
