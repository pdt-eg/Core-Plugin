/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.codeassist.strategy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.internal.core.codeassist.CodeAssistUtils;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.pdtextensions.core.codeassist.PDTCompletionInfo;
import org.pdtextensions.core.codeassist.context.SuperclassMethodContext;


/**
 * Completionstrategy to insert method stubs from superclasses.
 *
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class SuperclassMethodCompletionStrategy extends
AbstractCompletionStrategy implements ICompletionStrategy {

	/**
	 * @param context
	 */
	public SuperclassMethodCompletionStrategy(ICompletionContext context) {
		super(context);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.php.core.codeassist.ICompletionStrategy#apply(org.eclipse.php.internal.core.codeassist.ICompletionReporter)
	 */
	@Override
	public void apply(ICompletionReporter reporter) throws Exception {

		SuperclassMethodContext context = (SuperclassMethodContext) getContext();
		ISourceModule module = context.getSourceModule();

		IModelElement element = module.getElementAt(context.getOffset());

		if (!(element instanceof SourceType)) {
			while(element.getParent() != null) {
				element = element.getParent();
				if (element instanceof SourceType) {
					break;
				}
			}
		}

		if (element == null || !(element instanceof SourceType)) {
			return;
		}

		IDLTKSearchScope scope = SearchEngine.createSearchScope(module.getScriptProject());
		SourceType type = (SourceType) element;
		SourceRange range = getReplacementRange(context);
		String prefix = context.getPrefix();

		IType[] projectTypes = PhpModelAccess.getDefault().findTypes(type.getElementName(), MatchRule.EXACT, 0, 0, scope, null);

		if (projectTypes.length != 1) {
			return;
		}

		IType currentType = projectTypes[0];
		ITypeHierarchy hierarchy = this.getCompanion().getSuperTypeHierarchy(type, new NullProgressMonitor());
		IType[] superTypes = hierarchy.getAllSupertypes(currentType);

		List<String> reported = new ArrayList<String>();

		for (IType superType : superTypes) {

			for (IMethod method : superType.getMethods()) {

				IMethod moduleMethod = type.getMethod(method.getElementName());

				try {
					// throws a ModelException for methods not declared inside this sourcemodule,
					// hence when it passes we can safely continue
					moduleMethod.getUnderlyingResource();
					continue;

				} catch (ModelException e) {

					if (CodeAssistUtils.startsWithIgnoreCase(moduleMethod.getElementName(), prefix) && !reported.contains(method.getElementName())) {
						reporter.reportMethod(method, "", range, new PDTCompletionInfo(module));
						reported.add(method.getElementName());
					}
				}
			}
		}
	}
}
