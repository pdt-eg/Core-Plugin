/*******************************************************************************
 * Copyright (c) 2012 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core.ui.filter;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.php.internal.ui.outline.PHPOutlineContentProvider.UseStatementsNode;

public class UseStatementsFilter extends ViewerFilter {

	public UseStatementsFilter() {
	}


	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!(parentElement instanceof ISourceModule) || !(element instanceof UseStatementsNode)) {
			return true;
		}

		return false;
	}

}
