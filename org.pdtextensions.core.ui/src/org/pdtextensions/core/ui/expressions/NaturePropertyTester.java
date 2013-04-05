package org.pdtextensions.core.ui.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.documentModel.dom.ElementImplForPhp;
import org.eclipse.php.internal.core.documentModel.dom.IImplForPhp;
import org.pdtextensions.core.log.Logger;

/**
 * 
 * Checks if the project which contains the selected file has the required nature.
 * 
 * Not sure if this is possible without a custom {@link PropertyTester}. If think it is, 
 * let me know ;)
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 */
@SuppressWarnings("restriction")
public class NaturePropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		try {
			
			if (expectedValue == null || property == null || !"parentProjectNature".equals(property))  {
				return false;
			}
			
			IProject project = null;
			
			if (receiver instanceof IImplForPhp) {
				
				IImplForPhp phpElement = (ElementImplForPhp) receiver;
				IModelElement element = phpElement.getModelElement();
				
				if (element == null) {
					return false;
				}
				
				project = element.getScriptProject().getProject();
			} else if (receiver instanceof ISourceModule) {
				ISourceModule source = (ISourceModule) receiver;
				project = source.getScriptProject().getProject();
			}
			
			if (project != null && project.hasNature(expectedValue.toString())) {
				return true;
			}
		} catch (Exception e) {
			Logger.logException(e);
		}
		
		return false;
	}
}
