/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.codeassist;

import org.eclipse.dltk.core.ISourceModule;

/**
 *
 */
public class PDTCompletionInfo {
	
	private final ISourceModule source;
	
	public PDTCompletionInfo(ISourceModule sourcemodule) {
		
		this.source = sourcemodule;
	}

	public ISourceModule getSource() {
		return source;
	}

}
