/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.

 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.codeassist;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.codeassist.ICompletionStrategyFactory;
import org.pdtextensions.core.codeassist.context.SuperclassMethodContext;
import org.pdtextensions.core.codeassist.strategy.SuperclassMethodCompletionStrategy;

public class CompletionStrategyFactory implements ICompletionStrategyFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public ICompletionStrategy[] create(ICompletionContext[] contexts) {

		List<ICompletionStrategy> result = new LinkedList<ICompletionStrategy>();

		for (ICompletionContext context : contexts) {

			Class contextClass = context.getClass();

			if (contextClass == SuperclassMethodContext.class) {

				result.add(new SuperclassMethodCompletionStrategy(context));

			}
		}

		return (ICompletionStrategy[]) result.toArray(new ICompletionStrategy[result.size()]);
	}

}
