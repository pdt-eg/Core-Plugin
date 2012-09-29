/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.pdtextensions.core.log.Logger;
import org.pdtextensions.core.visitor.PDTVisitor;

public class PDTBuildParticipant implements IBuildParticipant {

	@Override
	public void build(IBuildContext context) throws CoreException {
		try {

			ISourceModule sourceModule = context.getSourceModule();
			ModuleDeclaration moduleDeclaration = SourceParserUtil
					.getModuleDeclaration(sourceModule);

			PDTVisitor visitor = new PDTVisitor(sourceModule, context);
			moduleDeclaration.traverse(visitor);

		} catch (Exception e) {
			Logger.debug(e.getMessage());
		}
	}
}
