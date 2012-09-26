/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.server.ui.internal.lhttpd;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.pdtextensions.server.PEXServerPlugin;

/**
 * Wizard fragment for local httpd runtime
 * 
 * @author mepeisen
 */
public class RuntimeWizardFragment extends WizardFragment {

	private LHttpdRuntimeComposite comp;

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		
		if (runtime == null)
			return false;
		IStatus status = runtime.validate(null);
		return (status == null || status.getSeverity() != IStatus.ERROR);
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new LHttpdRuntimeComposite(parent, wizard);
		return comp;
	}

	@Override
	public void enter() {
		if (comp != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
			comp.setRuntime(runtime);
		}
	}

	@Override
	public void exit() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		IPath path = runtime.getLocation();
		if (runtime.validate(null).getSeverity() != IStatus.ERROR)
			PEXServerPlugin.setPreference("location" + runtime.getRuntimeType().getId(), path.toString()); //$NON-NLS-1$
	}

}
