/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.extension;

import org.eclipse.dltk.core.IScriptFolder;

/**
 * 
 * Interface for the namespacersolver extension point.
 *
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface INamespaceResolver {

	/**
	 * Resolve the namespace of an IScriptFolder
	 * @param container
	 * @return the resolved namespace or null if unable to resolve
	 */
	String resolve(IScriptFolder container);

}
