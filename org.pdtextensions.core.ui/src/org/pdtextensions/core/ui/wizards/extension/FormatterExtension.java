/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.wizards.extension;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ui.wizards.ISourceModuleWizard;
import org.eclipse.dltk.ui.wizards.ISourceModuleWizard.ICreateContext;
import org.eclipse.dltk.ui.wizards.ISourceModuleWizard.ICreateStep;
import org.eclipse.dltk.ui.wizards.ISourceModuleWizardExtension;
import org.eclipse.dltk.ui.wizards.ISourceModuleWizardMode;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceAlreadyExists;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.pdtextensions.core.util.PDTFormatterUtils;

/**
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class FormatterExtension implements ISourceModuleWizardExtension {

	@Override
	public boolean start(ISourceModuleWizard wizard) {
		return wizard instanceof NewSourceModuleWizard;
	}

	@Override
	public List<ISourceModuleWizardMode> getModes() {
		return null;
	}

	@Override
	public void initialize() {
	}

	@Override
	public IStatus validate() {
		return Status.OK_STATUS;
	}

	@Override
	public void prepare(ICreateContext context) {
		context.addStep(ICreateStep.KIND_FINALIZE, 0, new FormatModuleStep());
	}

	private class FormatModuleStep implements ICreateStep {

		@Override
		public void execute(ICreateContext context, IProgressMonitor monitor) throws CoreException {
			if (!context.getSourceModule().exists()) {
				return;
			}
			IPath path = context.getSourceModule().getPath();
			try {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				IStructuredModel model = StructuredModelManager.getModelManager().getNewModelForEdit(file, true);
				PDTFormatterUtils.format(model.getStructuredDocument(), new Region(0, model.getStructuredDocument().get().length()), context.getScriptProject()
						.getProject());
			} catch (ResourceAlreadyExists e) {
				e.printStackTrace();
			} catch (ResourceInUse e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.getClass();
			}

		}

	}
}
