/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.ui.internal.lhttpd;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;

/**
 * Editor part for configuration of local httpd runtime.
 * 
 * @author mepeisen
 */
public class ServerConfigurationEditorPart extends ServerEditorPart {

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		ScrolledForm form = toolkit.createScrolledForm(parent);
		toolkit.decorateFormHeading(form.getForm());
		form.setText(Messages.ServerConfigurationEditorPart_FormTitle);
		/* TODO form.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_WEB_MODULE));*/
		GridLayout layout = new GridLayout();
		layout.marginTop = 6;
		layout.marginLeft = 6;
		form.getBody().setLayout(layout);
		
		
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
