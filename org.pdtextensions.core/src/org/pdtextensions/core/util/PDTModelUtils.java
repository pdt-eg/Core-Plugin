/*
 * This file is part of the PDT Extensions eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.pdtextensions.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.evaluation.types.MultiTypeType;
import org.eclipse.dltk.internal.core.util.LRUCache;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.internal.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocBlock;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocTag;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocTagKinds;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.index.IPHPDocAwareElement;
import org.eclipse.php.internal.core.model.PhpModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.php.internal.core.typeinference.PHPSimpleTypes;

/**
 * 
 * PHP Extension Utility class for the PHP model.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class PDTModelUtils {

	private final static Pattern ARRAY_TYPE_PATTERN = Pattern
			.compile("array\\[.*\\]");	

	private static LRUCache typeCache = new LRUCache();
	private static List<String> builtinTypes = new ArrayList<String>(Arrays.asList("array", "static", "self", "parent"));
	
	public static List<IEvaluatedType> collectUseStatements(List<IType> types, boolean includeAbstract) {
		
		List<IEvaluatedType> statements = new ArrayList<IEvaluatedType>();
		
		try {
			for (IType type : types) {
				
				IType currentNamespace = PHPModelUtils.getCurrentNamespace(type);
								
				boolean isInterface = PHPFlags.isInterface(type.getFlags());
				IEvaluatedType evaluated = getEvaluatedType(type.getElementName(), currentNamespace);
				
				if (!statements.contains(evaluated))
					statements.add(evaluated);

				for (IMethod method : type.getMethods()) {
				
					boolean isAbstract = PHPFlags.isAbstract(method.getFlags());
					
					if (!isInterface && isAbstract && includeAbstract) {						
						statements.addAll(collectParameterTypes(method));
					} else if (isInterface) {
						statements.addAll(collectParameterTypes(method));
					}
				}				
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
				
		return statements;		
		
	}
	
	public static List<IEvaluatedType> collectParameterTypes(IMethod method) throws ModelException {
		
		List<IEvaluatedType> evaluated = new ArrayList<IEvaluatedType>();
		
		IScriptProject project = method.getScriptProject();
		
		if (project == null)
			return evaluated;
		
		IType currentNamespace = PHPModelUtils.getCurrentNamespace(method);
		String[] typeNames = null;
		if (method instanceof IPHPDocAwareElement) {
			typeNames = ((IPHPDocAwareElement) method).getReturnTypes();
		} else {
			List<String> returnTypeList = new LinkedList<String>();
			PHPDocBlock docBlock = PHPModelUtils.getDocBlock(method);
			if (docBlock == null) {
				return null;
			}
			PHPDocTag[] tags = docBlock.getTags(PHPDocTagKinds.PARAM);
			if (tags != null && tags.length > 0) {
				for (PHPDocTag phpDocTag : tags) {
					if (phpDocTag.getReferences() != null
							&& phpDocTag.getReferences().length > 0) {
						for (SimpleReference ref : phpDocTag
								.getReferences()) {
							
							if (ref instanceof TypeReference) {
								String type = ref.getName();
								if (type != null && isValidType(type, project)) {
									returnTypeList.add(type);
								}								
							}
						}
					}
				}
			}
			typeNames = returnTypeList.toArray(new String[returnTypeList
					.size()]);
		}
		if (typeNames != null) {
			for (String typeName : typeNames) {
				Matcher m = ARRAY_TYPE_PATTERN.matcher(typeName);
				if (m.find()) {
					int offset = 0;
					try {
						offset = method.getSourceRange().getOffset();
					} catch (ModelException e) {
					}
					
					IEvaluatedType t = getArrayType(m.group(), currentNamespace, offset);
					String name = t.getTypeName();
					if (!evaluated.contains(name) && name.startsWith("$"))
						evaluated.add(t);
				} else {

						if (currentNamespace != null) {

							PHPDocBlock docBlock = PHPModelUtils
									.getDocBlock(method);
							ModuleDeclaration moduleDeclaration = SourceParserUtil
									.getModuleDeclaration(currentNamespace
											.getSourceModule());
							if (typeName
									.indexOf(NamespaceReference.NAMESPACE_SEPARATOR) > 0) {
								// check if the first part
								// is an
								// alias,then get the full
								// name
								String prefix = typeName
										.substring(
												0,
												typeName.indexOf(NamespaceReference.NAMESPACE_SEPARATOR));
								final Map<String, UsePart> result = PHPModelUtils
										.getAliasToNSMap(prefix,
												moduleDeclaration,
												docBlock.sourceStart(),
												currentNamespace, true);
								if (result.containsKey(prefix)) {
									String fullName = result.get(prefix)
											.getNamespace()
											.getFullyQualifiedName();
									typeName = typeName.replace(prefix,
											fullName);
								}
							} else if (typeName
									.indexOf(NamespaceReference.NAMESPACE_SEPARATOR) < 0) {

								String prefix = typeName;
								final Map<String, UsePart> result = PHPModelUtils
										.getAliasToNSMap(prefix,
												moduleDeclaration,
												docBlock.sourceStart(),
												currentNamespace, true);
								if (result.containsKey(prefix)) {
									String fullName = result.get(prefix)
											.getNamespace()
											.getFullyQualifiedName();
									typeName = fullName;
								}
							}
						}
						IEvaluatedType type = getEvaluatedType(typeName,
								currentNamespace);
						
						if (type != null && isValidType(typeName, project) && !evaluated.contains(type.getTypeName()) && !type.getTypeName().startsWith("$")) {							
							evaluated.add(type);
						}
					
				}
			}
		}
		
		return evaluated;		
		
	}
	
	public static IEvaluatedType getEvaluatedType(String typeName,
			IType currentNamespace) {
		IEvaluatedType type = PHPSimpleTypes.fromString(typeName);
		if (type == null) {
			if (typeName.indexOf(NamespaceReference.NAMESPACE_SEPARATOR) != -1
					|| currentNamespace == null) {
				type = new PHPClassType(typeName);
			} else if (currentNamespace != null) {
				type = new PHPClassType(currentNamespace.getElementName(),
						typeName);				
			}
		}
		return type;
	}
	
	public static MultiTypeType getArrayType(String type, IType currentNamespace,
			int offset) {
		int beginIndex = type.indexOf("[") + 1;
		int endIndex = type.lastIndexOf("]");
		type = type.substring(beginIndex, endIndex);
		MultiTypeType arrayType = new MultiTypeType();
		Matcher m = ARRAY_TYPE_PATTERN.matcher(type);
		if (m.find()) {
			arrayType
					.addType(getArrayType(m.group(), currentNamespace, offset));
			type = m.replaceAll("");
		}
		String[] typeNames = type.split(",");
		for (String name : typeNames) {
			if (!"".equals(name)) {

				if (name.indexOf(NamespaceReference.NAMESPACE_SEPARATOR) > 0
						&& currentNamespace != null) {
					// check if the first part is an
					// alias,then get the full name
					ModuleDeclaration moduleDeclaration = SourceParserUtil
							.getModuleDeclaration(currentNamespace
									.getSourceModule());
					String prefix = name.substring(0, name
							.indexOf(NamespaceReference.NAMESPACE_SEPARATOR));
					final Map<String, UsePart> result = PHPModelUtils
							.getAliasToNSMap(prefix, moduleDeclaration, offset,
									currentNamespace, true);
					if (result.containsKey(prefix)) {
						String fullName = result.get(prefix).getNamespace()
								.getFullyQualifiedName();
						name = name.replace(prefix, fullName);
					}
				}
				arrayType.addType(getEvaluatedType(name, currentNamespace));
			}
		}
		return arrayType;
	}
	
	
	public static String getMethodSignature(MethodDeclaration method, IScriptProject project) {
		
		String signature = method.getName().toLowerCase();
		return signature;
//		Integer num = new Integer(method.getArguments().size());
//		
//		if (signature.equals("current")) {
//			System.err.println("c: " + num);
//		}
		
//		return signature + num.toString();
		
//		for (Object o: method.getArguments()) {
//
//			try {
//				FormalParameter param = (FormalParameter) o;
//
//				SimpleReference type = param.getParameterType();
//				if (type != null && isValidType(type.getName(), project)) {
//					
//					if (signature.startsWith("setmetadatafor")) {
//						System.err.println(": " + param.getParameterType().getName().toLowerCase());	
//					}
//					
//					signature += param.getParameterType().getName().toLowerCase();										
//				}
//				
//			} catch (ClassCastException e) {
//
//			}
//		}
//		
//		return signature;
				
	}
	
	public static String getMethodSignature(IMethod method) {
		
		try {
			String methodSignature = method.getElementName().toLowerCase();
			return methodSignature;
			
//			Integer num = new Integer(method.getParameters().length);
//			
//			if (methodSignature.equals("current")) {
//					System.err.println(num);
//			}
//
//			return methodSignature + num;
		} catch (Exception e) {
			return "";
		}
		
//		try {
//			for (IParameter param: method.getParameters()) {				
//				try {
//					
//					if (isValidType(param.getType(), method.getScriptProject())) {						
//						if (methodSignature.startsWith("setmetadatafor")) {
//							System.err.println(param.getType());
//						}
//						methodSignature += param.getType().toLowerCase();						
//					}
//					
//				} catch (ClassCastException e) {
//
//				}
//			}
//		} catch (ModelException e) {
//			e.printStackTrace();
//		}
//		
//		return methodSignature;
		
		
	}	
	
	public static boolean isBuiltinType(String type) {
		
		return builtinTypes.contains(type);
	}

	public static boolean isValidType(String type, IScriptProject project) {
		
	    if (builtinTypes.contains(type)) {
	        return true;
	    }
	    
		String key = type + project.getElementName();
		
		if (typeCache.get(key) != null)
			return (Boolean) typeCache.get(key);
		
		if (type == null || "object".equals(type))
			return (Boolean) typeCache.put(key, new Boolean(false));
		
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);
		IType[] types = PhpModelAccess.getDefault().findTypes(type, MatchRule.EXACT, 0, 0, 
				scope, new NullProgressMonitor());
		
		return (Boolean) typeCache.put(key, new Boolean(types.length > 0));
		
	}
}