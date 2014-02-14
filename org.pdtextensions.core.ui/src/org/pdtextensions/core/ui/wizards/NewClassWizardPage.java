/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.wizards;

import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.jface.viewers.ISelection;
import org.pdtextensions.core.codegenerator.ClassGenerator;
import org.pdtextensions.core.ui.PDTPluginImages;

public class NewClassWizardPage extends NewElementWizardPage {

	public NewClassWizardPage(final ISelection selection, String initialFileName) {
		super();
		setImageDescriptor(PDTPluginImages.DESC_WIZBAN_NEW_PHPCLASS);
		this.selection = selection;
		this.initialFilename = initialFileName;

		status = new StatusInfo();
	}

	public NewClassWizardPage(final ISelection selection, String initialFileName, String namespace, String className,
			IScriptFolder scriptFolder) {
		this(selection, initialFileName);

		this.initialNamespace = namespace;
		this.initialClassName = className;
		this.initialFolder = scriptFolder;
		setScriptFolder(scriptFolder, true);
	}

	@Override
	protected String getPageTitle() {
		return "PHP Class";
	}

	@Override
	protected String getPageDescription() {

		return "Create a new PHP Class";
	}

	protected String generateFileContent() throws Exception {
		
		ClassGenerator classGenerator = new ClassGenerator();

		classGenerator.setName(getElementname());
//		classStubParameter.setAbstractClass(isAbstract());
//		classStubParameter.setFinalClass(isFinal());
		classGenerator.setNamespace(getNamespace());
//		classStubParameter.setInterfaces(getInterfaces());
		classGenerator.setSuperclass(getSuperclass().getFullyQualifiedName("\\"));
//		classStubParameter.setInheritedMethods(isGenerateMethodStubs());
//		classStubParameter.setConstructor(isGenerateConstructorStubs());
//		classStubParameter.setComments(isGenerateComments());
		
		return classGenerator.generateCode();

	}

	protected void createControls() {
		createNameControls();
		createClassModifierControls();
		createFileNameControls();
		createNamespaceControls();
		createSuperClassControls();
		createInterfaceControls("Interfa&ces:");
//		createMethodStubControls();
//		createCommentsControls();
	}

}
