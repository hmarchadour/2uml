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
package org.obeonetwork.jar2uml.core.internal.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;

public class ModelStore {
	final Model model;

	final Map<Element, Class<?>> indexUML2Java;

	final Map<Class<?>, Element> indexJava2UML;

	public ModelStore(Model model) {
		this.model = model;
		indexUML2Java = new HashMap<Element, Class<?>>();
		indexJava2UML = new HashMap<Class<?>, Element>();
	}

	public Model getModel() {
		return model;
	}

	public void add(Class<?> clazz, Element elem) {
		if (elem instanceof NamedElement) {
			// System.out.println("Add Java/UML " + clazz.getName() + "<-->" +
			// ((NamedElement)elem).getName());
		} else {
			// System.out.println("Add Java/UML " + clazz.getName() + "<-->" + elem);
		}
		indexUML2Java.put(elem, clazz);
		indexJava2UML.put(clazz, elem);
	}

	public void addAll(Map<Element, Class<?>> map) {
		for (Entry<Element, Class<?>> entry : map.entrySet()) {
			add(entry.getValue(), entry.getKey());
		}
	}

	public Map<Element, Class<?>> getUML2JavaBinding() {
		return Collections.unmodifiableMap(indexUML2Java);
	}

	public Map<Class<?>, Element> getJava2UMLBinding() {
		return Collections.unmodifiableMap(indexJava2UML);
	}
}
