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
package org.obeonetwork.jar2uml.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.google.common.collect.Maps;

/**
 * @author Hugo Marchadour
 */
public final class UMLModelHelper {

	/**
	 * Create a new uml model with found items in the given project.
	 * 
	 * @param project
	 *            The project where we will save the model
	 * @param modelName
	 *            the model name
	 * @param map
	 *            The map of file/items (Classes, interfaces...)
	 */
	public static void createModel(IProject project, String modelName, Map<File, Set<Class<?>>> filesBinding,
			Map<String, Set<Class<?>>> classStore, Map<String, Set<Class<?>>> externalStore) {

		final URI semanticModelURI = URI.createPlatformResourceURI('/' + project.getName() + '/' + modelName, true);
		Resource res = new ResourceSetImpl().createResource(semanticModelURI);
		final Model model = UMLFactory.eINSTANCE.createModel();
		res.getContents().add(model);
		model.setName(modelName.replace(".uml", ""));
		importPrimitiveTypes(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		final Map<Element, Class<?>> indexUML2Java = new HashMap<Element, Class<?>>();
		final Map<Class<?>, Element> indexJava2UML = new HashMap<Class<?>, Element>();

		if (!externalStore.isEmpty()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName("external");
			for (String key : externalStore.keySet()) {
				Map<Map<Element, Class<?>>, Map<Class<?>, Element>> externalStoreIndex = createModelElements(
						createdComponent, externalStore.get(key));
				Set<Entry<Map<Element, Class<?>>, Map<Class<?>, Element>>> entrySet = externalStoreIndex.entrySet();
				for (Entry<Map<Element, Class<?>>, Map<Class<?>, Element>> entry : entrySet) {
					indexUML2Java.putAll(entry.getKey());
					indexJava2UML.putAll(entry.getValue());
				}
			}
		}

		for (File file : filesBinding.keySet()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName(file.getName());
			Map<Map<Element, Class<?>>, Map<Class<?>, Element>> storeIndex = createModelElements(createdComponent,
					filesBinding.get(file));

			Set<Entry<Map<Element, Class<?>>, Map<Class<?>, Element>>> entrySet = storeIndex.entrySet();
			for (Entry<Map<Element, Class<?>>, Map<Class<?>, Element>> entry : entrySet) {
				indexUML2Java.putAll(entry.getKey());
				indexJava2UML.putAll(entry.getValue());
			}
		}
		handleElementRelations(model, indexUML2Java, indexJava2UML);
		try {
			res.save(Maps.newHashMap());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void handleElementRelations(Model model, Map<Element, Class<?>> indexUML2Java,
			Map<Class<?>, Element> indexJava2UML) {

		for (Element element : indexUML2Java.keySet()) {
			if (element instanceof Interface) {
				Class<?> javaInterface = indexUML2Java.get(element);
				Interface anInterface = (Interface) element;
				// TODO : handle Interface
			} else if (element instanceof org.eclipse.uml2.uml.Class) {
				Class<?> javaClass = indexUML2Java.get(element);
				org.eclipse.uml2.uml.Class aClass = (org.eclipse.uml2.uml.Class) element;

				// Methods
				for (Method method : javaClass.getMethods()) {
					Operation createOwnedOperation = aClass.createOwnedOperation(method.getName(), null, null);
					Element returnType = indexJava2UML.get(method.getReturnType());
					if (returnType != null) {
						createOwnedOperation.createReturnResult("return", (Type) returnType);
					} else {
						List<Package> importedPackages = model.getImportedPackages();
						for (Package package1 : importedPackages) {
							NamedElement member = package1.getMember(method.getReturnType().getName());
							if (member instanceof Type) {
								returnType = member;
							}
						}
						if (returnType != null) {
							createOwnedOperation.createReturnResult("return", (Type) returnType);
						} else {
							String name = method.getReturnType().getName();
							if (!"void".equals(name)) {
								System.out.println("not found in the model " + method.getReturnType().getName());
							}
						}
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					for (Class<?> parameterType : parameterTypes) {
						Element paramType = indexJava2UML.get(parameterType);
						if (paramType != null) {
							createOwnedOperation.createOwnedParameter("", (Type) paramType);
						} else {
							List<Package> importedPackages = model.getImportedPackages();
							for (Package package1 : importedPackages) {
								NamedElement member = package1.getMember(parameterType.getName());
								if (member instanceof Type) {
									paramType = member;
								}
							}
							if (paramType != null) {
								createOwnedOperation.createOwnedParameter("", (Type) paramType);
							} else {
								if (!"void".equals(method.getReturnType().getName())) {
									System.out.println("not found in the model " + method.getReturnType().getName());
								}
							}
						}
					}
				}

				// Attributes
				for (Field field : javaClass.getFields()) {
					Class<?> fieldType = field.getType();
					if (indexJava2UML.containsKey(fieldType)) {
						Element fieldUMLType = indexJava2UML.get(fieldType);
						if (fieldUMLType instanceof Type) {
							aClass.createOwnedAttribute(field.getName(), (Type) fieldUMLType);
						} else {
							System.err.println(fieldUMLType);
						}
					} else {
						Class<?> currentType = field.getType();
						int cardianality = 1;
						if (currentType.isArray()) {
							cardianality = -1;
							currentType = currentType.getComponentType();
						}

						NamedElement member = model.getMember(currentType.getName());
						if (member instanceof Type) {
							Type foundType = (Type) member;
							aClass.createOwnedAttribute(field.getName(), foundType, 0, cardianality);
						} else {
							Class<? extends Class> class1 = field.getType().getClass();
							System.err.println("not handled type " + field.getType().getName());
						}
					}
				}
			} else if (element instanceof Enumeration) {
				Class<?> javaEnumeration = indexUML2Java.get(element);
				Enumeration anEnumeration = (Enumeration) element;
				// TODO : handle Enumeration
			}
		}
	}

	private static Map<Map<Element, Class<?>>, Map<Class<?>, Element>> createModelElements(Component parent,
			Set<Class<?>> foundItems) {

		final Map<Element, Class<?>> indexUML2Java = new HashMap<Element, Class<?>>();
		final Map<Class<?>, Element> indexJava2UML = new HashMap<Class<?>, Element>();
		final Map<Map<Element, Class<?>>, Map<Class<?>, Element>> index = new HashMap<Map<Element, Class<?>>, Map<Class<?>, Element>>();
		index.put(indexUML2Java, indexJava2UML);

		for (Class<?> item : foundItems) {

			if (item.isInterface()) {
				Class<?> anInterface = item;
				if (anInterface.getSimpleName() != null && anInterface.getSimpleName().length() > 0) {
					if (!anInterface.isPrimitive()) {
						org.eclipse.uml2.uml.Package pack = handlePackage(parent, anInterface.getPackage());
						Interface createInterface = pack.createOwnedInterface(anInterface.getSimpleName());
						indexUML2Java.put(createInterface, anInterface);
						indexJava2UML.put(anInterface, createInterface);
					}
				}
			} else if (item.isAnnotation()) {
				// TODO: handle annotation
			} else if (item.isEnum()) {
				Class<?> anEnum = item;
				if (anEnum.getSimpleName() != null && anEnum.getSimpleName().length() > 0) {
					org.eclipse.uml2.uml.Package pack = handlePackage(parent, anEnum.getPackage());
					Enumeration createEnumeration = pack.createOwnedEnumeration(anEnum.getSimpleName());
					indexUML2Java.put(createEnumeration, anEnum);
					indexJava2UML.put(anEnum, createEnumeration);
				}
			} else {
				Class<?> aClass = item;
				if (aClass.getSimpleName() != null && aClass.getSimpleName().length() > 0) {
					org.eclipse.uml2.uml.Package pack = handlePackage(parent, aClass.getPackage());
					org.eclipse.uml2.uml.Class createClass = pack.createOwnedClass(aClass.getSimpleName(),
							Modifier.isAbstract(aClass.getModifiers()));
					indexUML2Java.put(createClass, aClass);
					indexJava2UML.put(aClass, createClass);
				}
			}
		}

		return index;
	}

	private static org.eclipse.uml2.uml.Package handlePackage(Component parent, java.lang.Package pack) {
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

	/**
	 * Loads & import library into the {@link Namespace}.
	 * 
	 * @param namespace
	 *            the {@link Namespace} context
	 * @param libraryUri
	 *            the URI of the library to load.
	 */
	private static void importPrimitiveTypes(Namespace namespace, String libraryUri) {
		final ResourceSet resourceSet = namespace.eResource().getResourceSet();
		final Resource resource = resourceSet.getResource(URI.createURI(libraryUri), true);

		final Package root = (Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
		// We check if a package import already exists
		if (!namespace.getImportedPackages().contains(root)) {
			namespace.createPackageImport(root);
		}
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
	private static boolean nameMatches(NamedElement namedElt, String name) {
		if (namedElt != null && namedElt.getName() != null && name != null) {
			return namedElt.getName().trim().equalsIgnoreCase(name.trim());
		} else {
			return false;
		}
	}
}
