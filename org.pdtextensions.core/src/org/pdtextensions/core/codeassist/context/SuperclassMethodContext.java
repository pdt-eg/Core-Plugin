/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.

 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.codeassist.context;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.documentModel.partitioner.PHPPartitionTypes;

/**
 *
 * Checks if the cursor is in a context where methods from
 * superclasses can be added to the proposals.
 *
 *
 */
@SuppressWarnings("restriction")
public class SuperclassMethodContext extends AbstractCompletionContext {

	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext#isValid(org.eclipse.dltk.core.ISourceModule, int, org.eclipse.dltk.core.CompletionRequestor)
	 */
	@Override
	public boolean isValid(ISourceModule sourceModule, int offset,
			CompletionRequestor requestor) {

		if (!super.isValid(sourceModule, offset, requestor)) {
			return false;
		}
		if (getCompanion().getPartitionType() != PHPPartitionTypes.PHP_DEFAULT) {
			return false;
		}

		try {

			IModelElement elem = sourceModule.getElementAt(offset);

			if (elem instanceof SourceType) {
				SourceType type = (SourceType) elem;

				if (type.getSuperClasses().length > 0) {
					return true;
				}

			}

		} catch (ModelException e) {
			e.printStackTrace();
		}

		return false;
	}
}
