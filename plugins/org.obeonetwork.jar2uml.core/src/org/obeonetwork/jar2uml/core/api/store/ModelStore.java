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
package org.obeonetwork.jar2uml.core.api.store;

import java.util.Map;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;

public interface ModelStore {

	Model getModel();

	void add(Class<?> clazz, Element elem);

	void addAll(Map<Element, Class<?>> map);

	Map<Element, Class<?>> getUML2JavaBinding();

	Map<Class<?>, Element> getJava2UMLBinding();

}
