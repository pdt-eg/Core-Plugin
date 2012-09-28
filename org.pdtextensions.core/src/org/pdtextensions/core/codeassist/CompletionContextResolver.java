/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.codeassist;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.pdtextensions.core.codeassist.context.SuperclassMethodContext;


@SuppressWarnings("restriction")
public class CompletionContextResolver 
	extends org.eclipse.php.internal.core.codeassist.contexts.CompletionContextResolver  {


	@Override
	public ICompletionContext[] createContexts() {

		return new ICompletionContext[] { 
				new SuperclassMethodContext()
		};
		

	}

}
