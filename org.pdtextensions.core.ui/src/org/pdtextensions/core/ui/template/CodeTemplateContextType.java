/*******************************************************************************
 * This file is part of the PDT Extensions eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package org.pdtextensions.core.ui.template;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.ContextTypeRegistry;

public class CodeTemplateContextType extends ScriptTemplateContextType {

	private static final String CONTROLLER_CONTEXTTYPE = "php_new_file_context";

	public CodeTemplateContextType(String contextName, String name) {
		super(contextName, name);

//		addResolver(new NamespaceVariableResolver("namespace", "Symfony Namespace Resolver"));
//		addResolver(new ClassNameVariableResolver("class_name", "Symfony Classname Resolver"));
//		addResolver(new UseStatementVariableResolver("use_parent", "Symfony UseStatement Resolver"));
//		addResolver(new ExtendsStatementVariableResolver("extends", "Symfony ExtendsStatement Resolver"));
//		addResolver(new InterfaceVariableResolver("interfaces", "Symfony Interface Resolver"));
//		addResolver(new InterfaceMethodsVariableResolver("supermethods", "Symfony Supermethods Resolver"));
//		addResolver(new ClassModifierVariableResolver("class_modifiers", "Symfony classmodifier resolver"));

	}	
	
	public CodeTemplateContextType(String contextName) {
		this(contextName, contextName);

	}

	
	public static void registerContextTypes(ContextTypeRegistry registry) {
		
		registry.addContextType(new CodeTemplateContextType(CodeTemplateContextType.CONTROLLER_CONTEXTTYPE));
		
		
	}

	@Override
	public ScriptTemplateContext createContext(IDocument document,
			int completionPosition, int length, ISourceModule sourceModule) {

		return new PDTTemplateContext(this, document, completionPosition, length, sourceModule);
	}	

}
