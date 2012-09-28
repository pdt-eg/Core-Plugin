/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.wizards;

import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.ui.wizards.NewSourceModulePage;
import org.eclipse.dltk.ui.wizards.NewSourceModuleWizard;


public class NewClassWizard extends NewSourceModuleWizard {
	
	private String className = null;
	private String namespace = null;
	private IScriptFolder scriptFolder;

	@Override
	protected NewSourceModulePage createNewSourceModulePage()
	{
		if (className != null) {
			return new NewClassWizardPage(getSelection(), className + ".php", namespace, className, scriptFolder); 
		}
		
		return new NewClassWizardPage(getSelection(), "");
	}

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public void setScriptFolder(IScriptFolder folder) {
		
		this.scriptFolder = folder;
		
	}	
}
