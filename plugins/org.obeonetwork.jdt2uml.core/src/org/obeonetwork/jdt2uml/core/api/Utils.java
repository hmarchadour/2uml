/*******************************************************************************
 * Copyright (c) 2014 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jdt2uml.core.api;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public final class Utils {

	public static boolean isClass(IType type) {
		boolean result = false;
		try {
			result = type.isClass();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isInterface(IType type) {
		boolean result = false;
		try {
			result = type.isInterface();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isAnnotation(IType type) {
		boolean result = false;
		try {
			result = type.isAnnotation();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isEnum(IType type) {
		boolean result = false;
		try {
			result = type.isEnum();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Set<IType> getType(IType superType, String typeIdent) {
		Set<IType> types = new HashSet<IType>();
		if (typeIdent != null) {
			String[][] resolveType = resolve(superType, typeIdent);
			if (resolveType != null) {
				Set<IType> retrieveTypes = retrieveTypes(superType.getJavaProject(),
						Utils.resolveQualifiedName(resolveType));
				for (IType type : retrieveTypes) {
					types.add(type);
				}
			}
		}
		return types;
	}

	public static Set<String> getQualifiedNames(IField field) {
		Set<String> qualifiedNames = new HashSet<String>();
		try {
			String typeIdent = Signature.getSimpleName(Signature.toString(field.getTypeSignature()));
			String[][] resolveType = resolve(field.getDeclaringType(), typeIdent);
			if (resolveType != null) {
				qualifiedNames.add(Utils.resolveQualifiedName(resolveType));
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return qualifiedNames;
	}

	public static Set<IType> retrieveTypes(IJavaProject javaProject, String qualifiedName) {
		Set<IType> types = new HashSet<IType>();
		try {
			IType findType = javaProject.findType(qualifiedName);
			if (findType != null) {
				types.add(findType);
			} else {
				System.out.println(qualifiedName + " not retrieve in this project");
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return types;
	}

	public static String[][] resolve(IType type, String typeIdent) {
		try {
			return type.resolveType(typeIdent);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Set<String> getQualifiedNames(IMethod method) {
		Set<String> qualifiedNames = new HashSet<String>();
		IType declaringType = method.getDeclaringType();
		try {
			String typeIdent = Signature.getSimpleName(Signature.toString(method.getReturnType()));
			String[][] resolveType = resolve(declaringType, typeIdent);
			if (resolveType != null) {
				qualifiedNames.add(Utils.resolveQualifiedName(resolveType));
			}

			String[] parameterTypes = Signature.getParameterTypes(method.getSignature());
			for (String parameterType : parameterTypes) {
				String typeParamIdent = Signature.getSimpleName(Signature.toString(parameterType));

				String[][] resolveParamType = resolve(declaringType, typeParamIdent);
				if (resolveParamType != null) {
					String qualifiedParamTypeIdent = Utils.resolveQualifiedName(resolveParamType);
					qualifiedNames.add(qualifiedParamTypeIdent);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return qualifiedNames;
	}

	public static boolean isExternal(IType type) {
		IPackageFragment packageFragment = type.getPackageFragment();
		return isExternal(packageFragment);
	}

	public static boolean isExternal(IPackageFragment packageFragment) {
		boolean isExternal = false;
		IJavaElement parent = packageFragment.getParent();
		if (parent instanceof IPackageFragmentRoot) {
			isExternal = ((IPackageFragmentRoot)parent).isExternal();
		} else if (parent instanceof IPackageFragment) {
			isExternal = isExternal((IPackageFragment)parent);
		}
		return isExternal;
	}

	public static String resolveQualifiedName(String[][] resolveType) {

		StringBuilder builder = new StringBuilder();
		boolean first = true;
		if (resolveType != null) {
			for (String[] l1 : resolveType) {
				for (String l2 : l1) {
					if (first) {
						first = false;
					} else {
						builder.append('.');
					}
					builder.append(l2);
				}
			}
		}
		return builder.toString();
	}
}
