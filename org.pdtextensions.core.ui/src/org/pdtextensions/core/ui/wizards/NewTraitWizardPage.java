/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.wizards;

import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.jface.viewers.ISelection;
import org.pdtextensions.core.codegenerator.IElementGenerator;
import org.pdtextensions.core.codegenerator.TraitGenerator;
import org.pdtextensions.core.ui.PDTPluginImages;

public class NewTraitWizardPage extends NewElementWizardPage {

	public NewTraitWizardPage(final ISelection selection, String initialFileName) {
		super();
		setImageDescriptor(PDTPluginImages.DESC_WIZBAN_NEW_PHPCLASS);
		this.selection = selection;
		this.initialFilename = initialFileName;

		status = new StatusInfo();
	}

	public NewTraitWizardPage(final ISelection selection, String initialFileName, String namespace,
			String className, IScriptFolder scriptFolder) {
		this(selection, initialFileName);

		this.initialNamespace = namespace;
		this.initialClassName = className;
		this.initialFolder = scriptFolder;
	}

	@Override
	protected void createControls() {
		super.createNameControls();
		super.createFileNameControls();
		super.createNamespaceControls();
		super.createInterfaceControls("Extended interfa&ces:");
//		super.createCommentsControls();
	}

	@Override
	protected String getPageTitle() {

		return "PHP Trait";
	}

	@Override
	protected String getPageDescription() {

		return "Create a new PHP Trait";
	}

	@Override
	protected String generateFileContent() throws Exception {
			IElementGenerator traitGenerator = new TraitGenerator();
			traitGenerator.setName(getElementname());
			traitGenerator.setNamespace(getNamespace());
			for (IType interfaceName : getInterfaces()) {
				traitGenerator.addInterface(interfaceName.getFullyQualifiedName("\\"));
			}

			return traitGenerator.toString();
	}

}