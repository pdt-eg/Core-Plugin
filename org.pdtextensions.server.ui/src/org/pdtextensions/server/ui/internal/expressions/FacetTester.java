/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.pdtextensions.server.ui.internal.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author mepeisen
 *
 */
public class FacetTester extends PropertyTester {

	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (expectedValue instanceof String) {
			final String value = (String) expectedValue;
			IProject project = null;
			if (receiver instanceof IScriptProject) {
				project = ((IScriptProject) receiver).getProject();
			}
			else
			if (receiver instanceof IProject) {
				project = (IProject) receiver;
			}
			
			if (project != null) {
				IFacetedProject faceted;
				try {
					faceted = ProjectFacetsManager.create(project);
				} catch (CoreException e) {
					return false;
				}
				if (faceted != null) {
					final IProjectFacet facet = ProjectFacetsManager.getProjectFacet(value);
					return faceted.hasProjectFacet(facet);
				}
			}
		}
		return false;
	}

}
