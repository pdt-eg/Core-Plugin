/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.semanticanalysis.internal.integration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.pdtextensions.semanticanalysis.PEXAnalysisPlugin;



/**
 * Used to report resource dependencies
 * 
 * TODO: Move to PDT
 * @author Dawid zulus Pakula <zulus@w3des.net>
 */
@SuppressWarnings("restriction")
public class DependencyBuilder implements IScriptBuilder {

	@Override
	public boolean initialize(IScriptProject project) {
		return true;
	}

	@Override
	public void prepare(IBuildChange change, IBuildState state,
			IProgressMonitor monitor) throws CoreException {
		
	}

	@Override
	public void build(IBuildChange change, IBuildState state,
			IProgressMonitor monitor) throws CoreException {
		for (ISourceModule file : change.getSourceModules(IBuildChange.DEFAULT)) {
			report(file, state);
		}
	}
	
	
	private void report(final ISourceModule module, final IBuildState state) {
		final ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(module);
		
		try {
			moduleDeclaration.traverse(new Visitor(module, state));
		} catch (Exception e) {
			PEXAnalysisPlugin.error(e);
		}
	}

	@Override
	public void clean(IScriptProject arg0, IProgressMonitor arg1) {
		
	}

	@Override
	public void endBuild(IScriptProject arg0, IBuildState arg1, IProgressMonitor arg2) {
		
	}
	
	private class Visitor extends PHPASTVisitor {
		final private IBuildState state;
		final private ISourceModule module;

		public Visitor(final ISourceModule module, final IBuildState state) {
			this.state = state;
			this.module = module;
		}
		
		@Override
		public boolean visit(UsePart s) throws Exception {
			String searchString = s.getNamespace().getFullyQualifiedName();
			IDLTKSearchScope searchScope = SearchEngine.createSearchScope(module.getScriptProject());
			IType[] types = PhpModelAccess.getDefault().findTypes(searchString, MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());

			for (IType type : types) {
				if (searchString.equals(type.getFullyQualifiedName("\\"))) { //$NON-NLS-1$
					state.recordDependency(module.getResource().getFullPath(), type.getResource() == null ? type.getPath() : type.getResource().getFullPath(), IBuildState.STRUCTURAL);
					return true;
				}
			}

			types = PhpModelAccess.getDefault().findTraits(searchString, MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());

			for (IType type : types) {
				if (searchString.equals(type.getFullyQualifiedName("\\"))) { //$NON-NLS-1$
					state.recordDependency(module.getResource().getFullPath(), type.getResource() == null ? type.getPath() : type.getResource().getFullPath(), IBuildState.STRUCTURAL);
					return true;
				}
			}
			
			return true;
		}
	}
}