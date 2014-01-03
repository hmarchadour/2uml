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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

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

			String fullQualifiedName = resolveFullQualifiedName(superType, typeIdent);
			if (fullQualifiedName != null && !fullQualifiedName.isEmpty()) {
				Set<IType> retrieveTypes = retrieveTypes(superType.getJavaProject(), fullQualifiedName);
				for (IType type : retrieveTypes) {
					types.add(type);
				}
			}
		}
		return types;
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

	public static Set<String> getQualifiedNames(IField field) {
		Set<String> qualifiedNames = new HashSet<String>();
		try {
			String typeIdent = Signature.getSimpleName(Signature.toString(field.getTypeSignature()));
			String fullQualifiedName = resolveFullQualifiedName(field.getDeclaringType(), typeIdent);
			if (fullQualifiedName != null && !fullQualifiedName.isEmpty()) {
				qualifiedNames.add(fullQualifiedName);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return qualifiedNames;
	}

	public static Set<String> getQualifiedNames(IMethod method) {
		Set<String> qualifiedNames = new HashSet<String>();
		IType declaringType = method.getDeclaringType();
		try {
			String typeIdent = Signature.getSimpleName(Signature.toString(method.getReturnType()));
			String fullQualifiedName = resolveFullQualifiedName(declaringType, typeIdent);
			if (fullQualifiedName != null && !fullQualifiedName.isEmpty()) {
				qualifiedNames.add(fullQualifiedName);
			}
			for (String parameterType : method.getParameterTypes()) {
				String typeParamIdent = Signature.getSimpleName(Signature.toString(parameterType));

				String fullQualifiedName2 = resolveFullQualifiedName(declaringType, typeParamIdent);
				if (fullQualifiedName2 != null && !fullQualifiedName2.isEmpty()) {
					qualifiedNames.add(fullQualifiedName2);
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

	public static String resolveFullQualifiedName(IType type, String typeIdent) {
		StringBuilder builder = new StringBuilder();
		try {
			String[][] resolveType = type.resolveType(typeIdent);
			builder.append(resolveQualifiedName(resolveType));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	private static String resolveQualifiedName(String[][] resolveType) {

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

	/**
	 * Loads & import library into the {@link Namespace}.
	 * 
	 * @param namespace
	 *            the {@link Namespace} context
	 * @param libraryUri
	 *            the URI of the library to load.
	 */
	public static void importPrimitiveTypes(Namespace namespace, String libraryUri) {
		final ResourceSet resourceSet = namespace.eResource().getResourceSet();
		final Resource resource = resourceSet.getResource(URI.createURI(libraryUri), true);

		final Package root = (Package)EcoreUtil.getObjectByType(resource.getContents(),
				UMLPackage.Literals.PACKAGE);
		// We check if a package import already exists
		if (!namespace.getImportedPackages().contains(root)) {
			namespace.createPackageImport(root);
		}
	}
}
