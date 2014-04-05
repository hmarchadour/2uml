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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jdt2uml.core.CoreActivator;

public final class Utils {

	public static boolean isClass(IType type) {
		boolean result = false;
		try {
			result = type.isClass();
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return result;
	}

	public static boolean isInterface(IType type) {
		boolean result = false;
		try {
			result = type.isInterface();
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return result;
	}

	public static boolean isAnnotation(IType type) {
		boolean result = false;
		try {
			result = type.isAnnotation();
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return result;
	}

	public static boolean isEnum(IType type) {
		boolean result = false;
		try {
			result = type.isEnum();
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return result;
	}

	public static Set<IType> resolveType(IType currentType, String typeIdent) {
		Set<IType> types = new HashSet<IType>();
		if (typeIdent != null) {

			String fullQualifiedName = resolveFullQualifiedName(currentType, typeIdent);
			if (fullQualifiedName != null && !fullQualifiedName.isEmpty()) {
				Set<IType> retrieveTypes = retrieveTypes(currentType.getJavaProject(), fullQualifiedName);
				for (IType retrieveType : retrieveTypes) {
					types.add(retrieveType);
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
			CoreActivator.logUnexpectedError(e);
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
			CoreActivator.logUnexpectedError(e);
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
			CoreActivator.logUnexpectedError(e);
		}
		return qualifiedNames;
	}

	public static boolean isExternal(IType type) {
		return getPackageFragmentRoot(type).isExternal();
	}

	public static IPackageFragmentRoot getPackageFragmentRoot(IType type) {
		IJavaElement currentParent = type;
		do {
			currentParent = currentParent.getParent();
		} while (currentParent != null && !(currentParent instanceof IPackageFragmentRoot));

		return (IPackageFragmentRoot)currentParent;
	}

	public static String getPath(IJavaElement javaElement) {
		List<String> segments = new ArrayList<String>();
		IJavaElement currentParent = javaElement;
		segments.add(currentParent.getElementName());
		do {
			currentParent = currentParent.getParent();
			if (currentParent != null) {
				segments.add(currentParent.getElementName());
			}
		} while (currentParent != null && !(currentParent instanceof IPackageFragmentRoot));
		Collections.reverse(segments);
		StringBuilder strBuilder = new StringBuilder();
		for (String segment : segments) {
			strBuilder.append(segment);
			strBuilder.append('/');
		}
		return strBuilder.toString();
	}

	public static String resolveFullQualifiedName(IType type, String typeIdent) {
		StringBuilder builder = new StringBuilder();
		try {
			String[][] resolveType = type.resolveType(typeIdent);
			builder.append(resolveQualifiedName(resolveType));
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
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
	public static void importUMLResource(Namespace namespace, String libraryUri) {
		importUMLResource(namespace, URI.createURI(libraryUri));
	}

	public static void importUMLResource(Namespace namespace, URI libraryUri) {
		final ResourceSet resourceSet = namespace.eResource().getResourceSet();
		final Resource resource = resourceSet.getResource(libraryUri, true);

		final Package root = (Package)EcoreUtil.getObjectByType(resource.getContents(),
				UMLPackage.Literals.PACKAGE);
		// We check if a package import already exists
		if (!namespace.getImportedPackages().contains(root)) {
			namespace.createPackageImport(root);
		}
	}

	public static List<Component> searchAllImportedComponents(Package pack) {
		List<Component> components = new ArrayList<Component>();
		List<PackageImport> packageImports = pack.getPackageImports();
		for (PackageImport packageImport : packageImports) {
			Package importedPackage = packageImport.getImportedPackage();
			if (importedPackage != null) {
				List<PackageableElement> packagedElements = importedPackage.getPackagedElements();
				for (PackageableElement packageableElement : packagedElements) {
					if (packageableElement instanceof Component) {
						components.add((Component)packageableElement);
					}
				}
				components.addAll(searchAllImportedComponents(importedPackage));
			}
		}
		return components;
	}

	public static PrimitiveType searchPrimiveTypeInModels(Namespace namespace, String primitiveTypeName) {
		PrimitiveType result = null;
		final ResourceSet resourceSet = namespace.eResource().getResourceSet();
		final Resource resource = resourceSet.getResource(
				URI.createURI(UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI), true);

		final Package root = (Package)EcoreUtil.getObjectByType(resource.getContents(),
				UMLPackage.Literals.PACKAGE);

		NamedElement member = root.getMember(primitiveTypeName);
		if (member instanceof PrimitiveType) {
			result = (PrimitiveType)member;
		}
		return result;
	}

	public static Component searchComponentInModels(Namespace namespace, String componentName) {
		Component result = null;

		List<EObject> contents = namespace.eResource().getContents();
		List<Component> components = new ArrayList<Component>();
		for (EObject content : contents) {
			if (content instanceof Package) {
				Package rootPackage = (Package)content;
				List<PackageableElement> packagedElements = rootPackage.getPackagedElements();
				for (PackageableElement packageableElement : packagedElements) {
					if (packageableElement instanceof Component) {
						components.add((Component)packageableElement);
					}
				}
				components.addAll(searchAllImportedComponents(rootPackage));
			}
		}
		for (Component component : components) {
			if (componentName.equals(component.getName())) {
				result = component;
				break;
			}
		}
		return result;
	}

	public static Classifier searchClassifierInModels(Namespace namespace, String qualifiedName) {
		Classifier result = null;

		Resource eResource = namespace.eResource();
		if (eResource != null) {
			List<EObject> contents = eResource.getContents();
			List<Component> components = new ArrayList<Component>();
			for (EObject content : contents) {
				if (content instanceof Package) {
					Package rootPackage = (Package)content;
					List<PackageableElement> packagedElements = rootPackage.getPackagedElements();
					for (PackageableElement packageableElement : packagedElements) {
						if (packageableElement instanceof Component) {
							components.add((Component)packageableElement);
						}
					}
					components.addAll(searchAllImportedComponents(rootPackage));
				}
			}

			String[] subpackages = qualifiedName.split("\\.");
			for (Component component : components) {
				Namespace member = component;
				for (int i = 0; i < subpackages.length; i++) {
					member = (Namespace)member.getMember(subpackages[i]);
					if (member == null) {
						break;
					}
				}
				if (member != null && member instanceof Classifier) {
					result = (Classifier)member;
					break;
				}
			}
		} else {
			throw new IllegalStateException("Should not appended");
		}
		return result;
	}

	public static org.eclipse.uml2.uml.Package handlePackage(Component parent,
			IPackageFragment packageFragment) {
		String[] subpackages = packageFragment.getElementName().split("\\.");
		org.eclipse.uml2.uml.Package current = null;
		for (String subpackage : subpackages) {
			if (subpackage == null || subpackage.length() == 0) {
				break;
			}
			if (current == null) {
				List<PackageableElement> packagedElements = parent.getPackagedElements();
				for (PackageableElement packageableElement : packagedElements) {
					if (packageableElement instanceof org.eclipse.uml2.uml.Package) {
						if (subpackage.equals(packageableElement.getName())) {
							current = (org.eclipse.uml2.uml.Package)packageableElement;
							break;
						}
					}
				}
				if (current == null) {
					Package createPackage = UMLFactory.eINSTANCE.createPackage();
					createPackage.setName(subpackage);
					parent.getPackagedElements().add(createPackage);
					current = createPackage;
				}
			} else {
				org.eclipse.uml2.uml.Package nextPackage = current.getNestedPackage(subpackage);
				if (nextPackage == null) {
					nextPackage = current.createNestedPackage(subpackage);
				}
				current = nextPackage;
			}
		}
		return current;
	}

	public static int countJavaItems(IJavaProject javaElement) {
		int count = 0;
		try {
			IJavaElement[] children = javaElement.getChildren();
			for (IJavaElement iJavaElement : children) {
				if (iJavaElement instanceof IPackageFragmentRoot) {
					count += countJavaItems((IPackageFragmentRoot)iJavaElement);
				} else {
					System.out.println(iJavaElement);
				}
			}
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return count;
	}

	public static int countJavaItems(IPackageFragmentRoot javaElement) {
		int count = 1;
		try {
			IJavaElement[] children = javaElement.getChildren();
			for (IJavaElement iJavaElement : children) {
				if (iJavaElement instanceof IPackageFragment) {
					count += countJavaItems((IPackageFragment)iJavaElement);
				}
			}
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return count;
	}

	public static int countJavaItems(IPackageFragment javaElement) {
		int count = 1;
		try {
			IJavaElement[] children = javaElement.getChildren();
			for (IJavaElement iJavaElement : children) {
				if (iJavaElement instanceof IPackageFragment) {
					count += countJavaItems((IPackageFragment)iJavaElement);
				}
			}
		} catch (JavaModelException e) {
			CoreActivator.logUnexpectedError(e);
		}
		return count;
	}

	public static String getModelFileName(IJavaProject javaProject) {
		return javaProject.getElementName() + ".model";
	}

	public static String getLibrariesFileName(IJavaProject javaProject) {
		return javaProject.getElementName() + ".libraries";
	}

	public static String createModelPath(IJavaProject javaProject, String fileName) {
		return '/' + javaProject.getElementName() + "/target/uml/" + fileName + ".uml";
	}

	public static Set<Model> getModel(IJavaProject javaProject) {
		Set<Model> result = new HashSet<Model>();
		URI resourceURI = URI.createPlatformResourceURI(
				Utils.createModelPath(javaProject, Utils.getModelFileName(javaProject)), true);
		if (resourceURI.isFile()) {
			Resource resource = new ResourceSetImpl().getResource(resourceURI, true);
			if (resource != null) {
				List<EObject> contents = resource.getContents();
				for (EObject eObject : contents) {
					if (eObject instanceof Model) {
						result.add((Model)eObject);
					}
				}
			}
		}
		return result;
	}

	public static Set<Model> getLibraries(IJavaProject javaProject) {
		Set<Model> result = new HashSet<Model>();
		URI resourceURI = URI.createPlatformResourceURI(
				Utils.createModelPath(javaProject, Utils.getLibrariesFileName(javaProject)), true);
		Resource resource = new ResourceSetImpl().getResource(resourceURI, true);
		if (resource != null) {
			List<EObject> contents = resource.getContents();
			for (EObject eObject : contents) {
				if (eObject instanceof Model) {
					result.add((Model)eObject);
				}
			}
		}
		return result;
	}
}
