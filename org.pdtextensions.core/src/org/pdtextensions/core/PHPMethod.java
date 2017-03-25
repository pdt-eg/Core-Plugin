/*******************************************************************************
 * Copyright (c) 2013 The PDT Extension Group (https://github.com/pdt-eg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.pdtextensions.core;

import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.php.core.ast.nodes.Bindings;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;
import org.eclipse.php.ui.editor.SharedASTProvider;

/**
 * This class provides information about the method as a PHP method or function.
 *
 * @since 0.20.0
 */
@SuppressWarnings("restriction")
public class PHPMethod {
	private IMethod method;

	public PHPMethod(IMethod method) {
		Assert.isNotNull(method);

		this.method = method;
	}

	public IMethod getFarthestOverriddenMethod() throws CoreException {
		IMethod overriddenMethod = getOverriddenMethod();
		if (overriddenMethod == null) {
			return method;
		} else {
			return new PHPMethod(overriddenMethod).getFarthestOverriddenMethod();
		}
	}

	public IMethod getOverriddenMethod() throws CoreException {
		try {
			Program ast = SharedASTProvider.getAST(method.getSourceModule(), SharedASTProvider.WAIT_YES, new NullProgressMonitor());
			if (ast != null) {
				OverriddenMethodFinder overriddenMethodFinder = new OverriddenMethodFinder();
				ast.accept(overriddenMethodFinder);

				return overriddenMethodFinder.getOverriddenMethod();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, PEXCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}

		return null;
	}

	private class OverriddenMethodFinder extends AbstractVisitor {
		private IMethod overriddenMethod;

		@Override
		public boolean visit(org.eclipse.php.core.ast.nodes.MethodDeclaration node) {
			if (node.getFunction().getFunctionName().getName().equals(method.getElementName())) {
				IMethodBinding methodBinding = node.resolveMethodBinding();
				if (methodBinding != null) {
					IMethodBinding overriddenMethodBinding = Bindings.findOverriddenMethod(methodBinding, true);
					if (overriddenMethodBinding != null) {
						overriddenMethod = (IMethod) overriddenMethodBinding.getPHPElement();
					}
				}

				return false;
			}

			return true;
		}

		public IMethod getOverriddenMethod() {
			return overriddenMethod;
		}
	}
}
