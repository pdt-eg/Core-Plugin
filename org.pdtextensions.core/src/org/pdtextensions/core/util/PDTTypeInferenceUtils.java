/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *     The PDT Extension Group - initial port to the PDT Extension Group Core Plugin
 *******************************************************************************/
package org.pdtextensions.core.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.evaluation.types.AmbiguousType;
import org.eclipse.dltk.evaluation.types.MultiTypeType;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallArgumentsList;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.internal.core.compiler.ast.parser.ASTUtils;
import org.eclipse.php.internal.core.typeinference.IModelAccessCache;
import org.eclipse.php.internal.core.typeinference.PHPThisClassType;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferenceUtils;
import org.eclipse.php.internal.core.typeinference.PHPTypeInferencer;
import org.eclipse.php.internal.core.typeinference.goals.MethodElementReturnTypeGoal;
import org.eclipse.php.internal.core.typeinference.goals.phpdoc.PHPDocMethodReturnTypeGoal;

/**
 * @see org.eclipse.php.internal.core.codeassist.CodeAssistUtils
 * @since 0.17.0
 */
@SuppressWarnings("restriction")
public class PDTTypeInferenceUtils {
	private static final IType[] EMPTY_TYPES = new IType[] {};
	private static final PHPTypeInferencer TYPE_INFERENCER = new PHPTypeInferencer();

	public static IType[] getTypes(ASTNode expression, ISourceModule sourceModule) {
		IContext context = ASTUtils.findContext(sourceModule, SourceParserUtil.getModuleDeclaration(sourceModule), expression.sourceStart());
		if (context != null) {
			IType[] types = getTypes(new PHPTypeInferencer().evaluateType(new ExpressionTypeGoal(context, expression)), context, expression.sourceStart());
			if (types != null) {
				return types;
			}
		}

		return EMPTY_TYPES;
	}

	public static IType[] getTypes(IEvaluatedType evaluatedType, IContext context, int position) {
		List<IEvaluatedType> possibleTypes = new LinkedList<IEvaluatedType>();
		if (evaluatedType instanceof MultiTypeType) {
			possibleTypes = ((MultiTypeType) evaluatedType).getTypes();
		} else if (evaluatedType instanceof AmbiguousType) {
			possibleTypes.addAll(Arrays.asList(((AmbiguousType) evaluatedType).getPossibleTypes()));
		} else {
			possibleTypes.add(evaluatedType);
		}

		List<IType> collectingTypes = new LinkedList<IType>();
		for (IEvaluatedType possibleType : possibleTypes) {
			IType[] types;
			if (possibleType instanceof MultiTypeType || possibleType instanceof AmbiguousType) {
				types = getTypes(possibleType, context, position);
			} else {
				types = PHPTypeInferenceUtils.getModelElements(possibleType, (ISourceModuleContext) context, position, (IModelAccessCache) null);
			}
			if (types != null) {
				collectingTypes.addAll(Arrays.asList(types));
			}
		}

		return collectingTypes.toArray(new IType[collectingTypes.size()]);
	}

	public static IType[] getTypes(PHPCallExpression expression, ISourceModule sourceModule) {
		IType[] receiverTypes = getTypes(expression.getReceiver(), sourceModule);
		if (receiverTypes != null && receiverTypes.length > 0) {
			IContext context = ASTUtils.findContext(sourceModule, SourceParserUtil.getModuleDeclaration(sourceModule), expression.sourceStart());
			return getFunctionReturnTypes(expression, sourceModule, receiverTypes, context);
		} else {
			return EMPTY_TYPES;
		}
	}

	private static IType[] getFunctionReturnTypes(PHPCallExpression expression, ISourceModule sourceModule, IType[] receiverTypes, IContext context) {
		IEvaluatedType evaluatedType = TYPE_INFERENCER.evaluateTypePHPDoc(new PHPDocMethodReturnTypeGoal(context, receiverTypes, expression.getName()));
		IType[] modelElements = PHPTypeInferenceUtils.getModelElements(evaluatedType, (ISourceModuleContext) context, expression.sourceStart());
		if (modelElements != null) {
			return modelElements;
		}

		evaluatedType = TYPE_INFERENCER.evaluateType(new MethodElementReturnTypeGoal(context, receiverTypes, expression.getName()));
		if (evaluatedType instanceof PHPThisClassType && ((PHPThisClassType) evaluatedType).getType() != null) {
			modelElements = new IType[] { ((PHPThisClassType) evaluatedType).getType() };
		} else {
			modelElements = PHPTypeInferenceUtils.getModelElements(evaluatedType, (ISourceModuleContext) context, expression.sourceStart());
		}
		if (modelElements != null) {
			return modelElements;
		}

		return EMPTY_TYPES;
	}

	private static IType[] getFunctionArrayReturnTypes(PHPCallExpression expression, ISourceModule sourceModule, IType[] receiverTypes, IContext context) {
		IEvaluatedType evaluatedType = TYPE_INFERENCER.evaluateTypePHPDoc(new PHPDocMethodReturnTypeGoal(context, receiverTypes, expression.getName()));
		if (evaluatedType instanceof MultiTypeType) {
			List<IType> collectingTypes = new LinkedList<IType>();
			for (IEvaluatedType possibleType : ((MultiTypeType) evaluatedType).getTypes()) {
				IType[] types = PHPTypeInferenceUtils.getModelElements(possibleType, (ISourceModuleContext) context, expression.sourceStart(), (IModelAccessCache) null);
				if (types != null) {
					collectingTypes.addAll(Arrays.asList(types));
				}
			}

			return collectingTypes.toArray(new IType[collectingTypes.size()]);
		}

		evaluatedType = TYPE_INFERENCER.evaluateType(new MethodElementReturnTypeGoal(context, receiverTypes, expression.getName()));
		if (evaluatedType instanceof MultiTypeType) {
			List<IType> collectingTypes = new LinkedList<IType>();
			for (IEvaluatedType possibleType : ((MultiTypeType) evaluatedType).getTypes()) {
				IType[] types = PHPTypeInferenceUtils.getModelElements(possibleType, (ISourceModuleContext) context, expression.sourceStart(), (IModelAccessCache) null);
				if (types != null) {
					collectingTypes.addAll(Arrays.asList(types));
				}
			}

			return collectingTypes.toArray(new IType[collectingTypes.size()]);
		}

		return EMPTY_TYPES;
	}
}
