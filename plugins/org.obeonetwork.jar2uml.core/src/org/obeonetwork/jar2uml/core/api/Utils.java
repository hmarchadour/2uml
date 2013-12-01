/*******************************************************************************
 * Copyright (c) 2013 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

public final class Utils {

	public static boolean isClass(Class<?> clazz) {
		return !isPrimitive(clazz) && !isInterface(clazz) && !isAnnotation(clazz) && !isEnum(clazz);
	}

	public static boolean isPrimitive(Class<?> clazz) {
		if (clazz.isArray()) {
			return isPrimitive(clazz.getComponentType());
		} else {
			return clazz.isPrimitive();
		}
	}

	public static boolean isPrimitive(Field field) {
		return isPrimitive(field.getType());
	}

	public static boolean isInterface(Class<?> clazz) {
		return clazz.isInterface();
	}

	public static boolean isAnnotation(Class<?> clazz) {
		return clazz.isAnnotation();
	}

	public static boolean isEnum(Class<?> clazz) {
		return clazz.isEnum();
	}

	public static Class<?> findSuperclass(Class<?> clazz) {
		try {
			return clazz.getSuperclass();
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findSuperclass " + e.getMessage());
			return null;
		}
	}

	public static List<Class<?>> findInterfaces(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getInterfaces());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findInterfaces " + e.getMessage());
			return Collections.<Class<?>> emptyList();
		}
	}

	public static List<Constructor<?>> findConstructors(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getDeclaredConstructors());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodes " + e.getMessage());
			return Collections.<Constructor<?>> emptyList();
		}
	}

	public static List<Field> findAttributes(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getFields());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findAttributes " + e.getMessage());
			return Collections.<Field> emptyList();
		}
	}

	public static List<Method> findMethodes(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getDeclaredMethods());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodes " + e.getMessage());
			return Collections.<Method> emptyList();
		}
	}

	public static Class<?> findMethodReturn(Method method) {
		try {
			return method.getReturnType();
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodReturn " + e.getMessage());
			return null;
		}
	}

	public static List<Class<?>> findMethodParams(Method method) {
		try {
			return Arrays.asList(method.getParameterTypes());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodeParams " + e.getMessage());
			return Collections.<Class<?>> emptyList();
		}
	}

	public static org.eclipse.uml2.uml.Package handlePackage(Component parent, java.lang.Package pack) {
		String[] subpackages = pack.getName().split("\\.");
		org.eclipse.uml2.uml.Package current = null;
		for (String subpackage : subpackages) {
			if (current == null) {
				List<PackageableElement> packagedElements = parent.getPackagedElements();
				for (PackageableElement packageableElement : packagedElements) {
					if (packageableElement instanceof org.eclipse.uml2.uml.Package) {
						if (subpackage.equals(packageableElement.getName())) {
							current = (org.eclipse.uml2.uml.Package) packageableElement;
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

	public static boolean validJavaItem(Class<?> item) {
		return item != null && item.getSimpleName() != null && item.getSimpleName().length() > 0;
	}

	public static boolean validJavaItem(Constructor constructor) {
		return constructor != null;
	}

	public static boolean validJavaItem(Field item) {
		return item != null && validJavaItem(item.getType());
	}

	public static boolean validJavaItem(Method item) {
		return item != null;
	}

	/**
	 * Check if the given element's name match the given String.
	 * 
	 * @param namedElt
	 *            the {@link NamedElement} to check.
	 * @param name
	 *            the name to match.
	 * @return <code>true</code> if the name match, <code>false</code> otherwise.
	 */
	public static boolean nameMatches(NamedElement namedElt, String name) {
		if (namedElt != null && namedElt.getName() != null && name != null) {
			return namedElt.getName().trim().equalsIgnoreCase(name.trim());
		} else {
			return false;
		}
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

		final Package root = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		// We check if a package import already exists
		if (!namespace.getImportedPackages().contains(root)) {
			namespace.createPackageImport(root);
		}
	}
}
