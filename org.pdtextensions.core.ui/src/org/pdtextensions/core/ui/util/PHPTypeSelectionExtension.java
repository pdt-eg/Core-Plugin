/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Marek Maksimczyk <marek.maksimczyk@mandos.net.pl> 
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.util;

import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.dltk.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.dltk.ui.dialogs.ITypeInfoImageProvider;
import org.eclipse.dltk.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.dltk.ui.dialogs.TypeSelectionExtension;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

public class PHPTypeSelectionExtension extends TypeSelectionExtension {

	/**
	 * @see PHPFlags
	 */
	private int trueFlags = 0;
	private int falseFlags = 0;

	public PHPTypeSelectionExtension() {
	}

	public PHPTypeSelectionExtension(int trueFlags, int falseFlags) {
		super();
		this.trueFlags = trueFlags;
		this.falseFlags = falseFlags;
	}

	@Override
	public ITypeInfoFilterExtension getFilterExtension() {
		return new ITypeInfoFilterExtension() {
			@Override
			public boolean select(ITypeInfoRequestor typeInfoRequestor) {
				if (falseFlags != 0 && (falseFlags & typeInfoRequestor.getModifiers()) != 0) {
					
					// Try to filter by black list.
					return false;
				} else if (trueFlags == 0 || (trueFlags & typeInfoRequestor.getModifiers()) != 0) {
					
					// Try to filter by white list, if trueFlags == 0 this is fine 'couse we pass black list.
					return true;
				} else {
					
					// Rest is filter out.
					return false;
				}
			}
		};
	}

	@SuppressWarnings("restriction")
	@Override
	public ISelectionStatusValidator getSelectionValidator() {
		return new TypedElementSelectionValidator(new Class[] {IType.class, INamespace.class}, false);
	}
	
}
