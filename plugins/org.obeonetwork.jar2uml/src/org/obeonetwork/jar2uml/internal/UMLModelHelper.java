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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
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
		for (Entry<File, Map<String, List<Class<?>>>> entry : map.entrySet()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName(entry.getKey().getName());
			createElements(createdComponent, map.get(entry.getKey()));
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

	private static void createElements(Component parent, Map<String, List<Class<?>>> foundItems) {

		final Map<Class<?>, Element> index = new HashMap<Class<?>, Element>(); // TODO : return this index

		final List<Class<?>> classes = foundItems.get(Jars2UML.CLASS_KEY);

		for (Class<?> aClass : classes) {
			if (aClass.getSimpleName() != null && aClass.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, aClass.getPackage());
				org.eclipse.uml2.uml.Class createClass = pack.createOwnedClass(aClass.getSimpleName(),
						Modifier.isAbstract(aClass.getModifiers()));
				// TODO: set in index
			}
		}

		final List<Class<?>> interfaces = foundItems.get(Jars2UML.INTERFACE_KEY);

		for (Class<?> anInterface : interfaces) {
			if (anInterface.getSimpleName() != null && anInterface.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, anInterface.getPackage());
				Interface createInterface = pack.createOwnedInterface(anInterface.getSimpleName());
				// TODO: set in index
			}
		}

		final List<Class<?>> enums = foundItems.get(Jars2UML.ENUM_KEY);
		for (Class<?> anEnum : enums) {
			if (anEnum.getSimpleName() != null && anEnum.getSimpleName().length() > 0) {
				org.eclipse.uml2.uml.Package pack = handlePackage(parent, anEnum.getPackage());
				Enumeration createEnumeration = pack.createOwnedEnumeration(anEnum.getSimpleName());
				// TODO: set in index
			}
		}
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
