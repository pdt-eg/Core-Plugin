/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.semanticanalysis.validation;

import java.util.List;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;

@SuppressWarnings("restriction")
public class MissingMethodImplementation {

	final private List<IMethod> misses;
	final private ClassDeclaration clazz;
	
	public MissingMethodImplementation(ClassDeclaration clazz, List<IMethod> missingMethods) {
		
		this.clazz = clazz;
		this.misses = missingMethods;
		
	}

	public ClassDeclaration getClazz() {
		return clazz;
	}

	public List<IMethod> getMisses() {
		return misses;
	}

	public int getStart() {

		return clazz.getNameStart();
	}
	
	public int getEnd() {
		
		return clazz.getNameEnd();
	}
	
	public int getInjectionOffset() {

		return clazz.getBodyEnd() - 2;
		
	}

	public String getTypeName() {

		return clazz.getName();

	}

	public String getFirstMethodName() {

		if (misses.size() == 0)
			return "";
		
		return misses.get(0).getElementName();

	}
	
}
