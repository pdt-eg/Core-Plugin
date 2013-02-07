/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.validation;

public interface IPDTProblem {

	int InterfaceRelated = 0x01100000;
	int UsageRelated = 0x01200000;
	int Unresolvable = 0x01300000;
	int Duplicate =    0x01400000;
	
}
