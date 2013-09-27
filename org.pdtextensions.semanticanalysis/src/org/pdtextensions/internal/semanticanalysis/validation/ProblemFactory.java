/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.internal.semanticanalysis.validation;

import org.eclipse.dltk.compiler.problem.DefaultProblemFactory;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemSeverityTranslator;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.IScriptProject;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;
import org.pdtextensions.semanticanalysis.validation.IValidatorIdentifier;

/**
 * Extension that allow fix severities on markers
 * 
 * @author Dawid zulus Pakula <zulus@w3des.net>
 * @since 0.18
 */
public class ProblemFactory extends DefaultProblemFactory {

	@Override
	public IProblemSeverityTranslator createSeverityTranslator(IScriptProject project) {
		return new SeverityTranslator(project);
	}
	
	private class SeverityTranslator implements IProblemSeverityTranslator {
		private final IScriptProject project;
		
		public SeverityTranslator(IScriptProject project) {
			this.project = project;
		}

		@Override
		public ProblemSeverity getSeverity(IProblemIdentifier problemId, ProblemSeverity defaultServerity) {
			if (problemId instanceof IValidatorIdentifier) {
				return PEXAnalysisPlugin.getDefault().getValidatorManager().getSeverity(project, (IValidatorIdentifier) problemId);
			}
			
			return IProblemSeverityTranslator.IDENTITY.getSeverity(problemId, defaultServerity);
		}
		
	}

}
