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
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;

import com.google.common.collect.Maps;

/**
 * @author Hugo Marchadour
 * 
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
	public static void createModel(IProject project, String modelName, Map<File, Map<String, List<Class<?>>>> map) {

		final URI semanticModelURI = URI.createPlatformResourceURI('/' + project.getName() + '/' + modelName, true);
		Resource res = new ResourceSetImpl().createResource(semanticModelURI);
		final Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(modelName.replace(".uml", ""));
		model.createOwnedPrimitiveType("java.lang.String");
		model.createOwnedPrimitiveType("java.lang.Integer");
		model.createOwnedPrimitiveType("java.lang.Boolean");
		model.createOwnedPrimitiveType("java.lang.Object");
		model.createOwnedPrimitiveType("char");
		model.createOwnedPrimitiveType("byte");
		model.createOwnedPrimitiveType("short");
		model.createOwnedPrimitiveType("long");
		model.createOwnedPrimitiveType("float");
		model.createOwnedPrimitiveType("double");
		model.createOwnedPrimitiveType("boolean");
		model.createOwnedPrimitiveType("int");

		for (File file : map.keySet()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName(file.getName());
			Map<Map<Element, Class<?>>, Map<Class<?>, Element>> index = createElements(createdComponent, map.get(file));
			handleElementRelations(model, index);
		}
		if (model != null) {
			res.getContents().add(model);
		}
		try {
			res.save(Maps.newHashMap());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void handleElementRelations(Model model, Map<Map<Element, Class<?>>, Map<Class<?>, Element>> index) {

		Set<Entry<Map<Element, Class<?>>, Map<Class<?>, Element>>> entrySet = index.entrySet();
		for (Entry<Map<Element, Class<?>>, Map<Class<?>, Element>> entry : entrySet) {
			Map<Element, Class<?>> indexUML2Java = entry.getKey();
			Map<Class<?>, Element> indexJava2UML = entry.getValue();

			for (Element element : indexUML2Java.keySet()) {
				if (element instanceof Interface) {
					Class<?> javaInterface = indexUML2Java.get(element);
					Interface anInterface = (Interface) element;
					// TODO : handle Interface
				} else if (element instanceof org.eclipse.uml2.uml.Class) {
					Class<?> javaClass = indexUML2Java.get(element);
					org.eclipse.uml2.uml.Class aClass = (org.eclipse.uml2.uml.Class) element;

					// Methodes
					for (Method method : javaClass.getMethods()) {
						aClass.createOwnedOperation(method.getName(), null, null);
						// TODO handle method args and return
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
	}

	private static Map<Map<Element, Class<?>>, Map<Class<?>, Element>> createElements(Component parent,
			Map<String, List<Class<?>>> foundItems) {

		final Map<Element, Class<?>> indexUML2Java = new HashMap<Element, Class<?>>();
		final Map<Class<?>, Element> indexJava2UML = new HashMap<Class<?>, Element>();
		final Map<Map<Element, Class<?>>, Map<Class<?>, Element>> index = new HashMap<Map<Element, Class<?>>, Map<Class<?>, Element>>();
		index.put(indexUML2Java, indexJava2UML);

		final List<Class<?>> classes = foundItems.get(Jars2UML.CLASS_KEY);

		for (Class<?> aClass : classes) {
			if (aClass.getSimpleName() != null && aClass.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, aClass.getPackage());
				org.eclipse.uml2.uml.Class createClass = pack.createOwnedClass(aClass.getSimpleName(),
						Modifier.isAbstract(aClass.getModifiers()));
				indexUML2Java.put(createClass, aClass);
				indexJava2UML.put(aClass, createClass);
			}
		}

		final List<Class<?>> interfaces = foundItems.get(Jars2UML.INTERFACE_KEY);

		for (Class<?> anInterface : interfaces) {
			if (anInterface.getSimpleName() != null && anInterface.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, anInterface.getPackage());
				Interface createInterface = pack.createOwnedInterface(anInterface.getSimpleName());
				indexUML2Java.put(createInterface, anInterface);
				indexJava2UML.put(anInterface, createInterface);
			}
		}

		final List<Class<?>> enums = foundItems.get(Jars2UML.ENUM_KEY);
		for (Class<?> anEnum : enums) {
			if (anEnum.getSimpleName() != null && anEnum.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, anEnum.getPackage());
				Enumeration createEnumeration = pack.createOwnedEnumeration(anEnum.getSimpleName());
				indexUML2Java.put(createEnumeration, anEnum);
				indexJava2UML.put(anEnum, createEnumeration);
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
}
