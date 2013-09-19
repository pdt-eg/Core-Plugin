/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.integration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;

public class BuildParticipantFactory implements IBuildParticipantFactory {
	@Override
	public IBuildParticipant createBuildParticipant(IScriptProject project) throws CoreException {
		return ContextInjectionFactory.make(BuildParticipant.class, EclipseContextFactory.getServiceContext(PEXAnalysisPlugin.getContext()));
	}
}