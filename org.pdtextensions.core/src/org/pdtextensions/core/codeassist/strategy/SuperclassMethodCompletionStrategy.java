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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionReporter;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.eclipse.php.internal.core.model.PHPModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.pdtextensions.core.codeassist.PDTCompletionInfo;
import org.pdtextensions.core.codeassist.context.SuperclassMethodContext;

/**
 * Completionstrategy to insert method stubs from superclasses.
 *
 */
@SuppressWarnings({ "restriction"})
public class SuperclassMethodCompletionStrategy extends AbstractCompletionStrategy implements ICompletionStrategy {

	/**
	 * @param context
	 */
	public SuperclassMethodCompletionStrategy(ICompletionContext context) {
		super(context);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.core.codeassist.ICompletionStrategy#apply(org.eclipse.php
	 * .internal.core.codeassist.ICompletionReporter)
	 */
	@Override
	public void apply(ICompletionReporter reporter) throws Exception {

		SuperclassMethodContext context = (SuperclassMethodContext) getContext();
		ISourceModule module = context.getSourceModule();

		IModelElement element = module.getElementAt(context.getOffset());

		if (!(element instanceof SourceType)) {
			while (element.getParent() != null) {
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
		ISourceRange range = getReplacementRange(context);
		String prefix = context.getPrefix();

		IType[] projectTypes = PHPModelAccess.getDefault().findTypes(type.getElementName(), MatchRule.EXACT, 0, 0,
				scope, null);

		if (projectTypes.length != 1) {
			return;
		}

		IType currentType = projectTypes[0];
		IMethod[] unimplementedMethods = PHPModelUtils.getUnimplementedMethods(currentType, null);

		List<String> reported = new ArrayList<String>();

		for (IMethod method : unimplementedMethods) {

			IMethod moduleMethod = type.getMethod(method.getElementName());

			if (StringUtils.startsWithIgnoreCase(moduleMethod.getElementName(), prefix)
					&& !reported.contains(method.getElementName())) {
				reporter.reportMethod(method, "", range, new PDTCompletionInfo(module));
				reported.add(method.getElementName());
			}
		}
	}
}
