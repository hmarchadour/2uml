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
package org.obeonetwork.jar2uml.core.api.store;

public interface ClassStore extends JavaStore {

	void add(Class<?> clazz);

	void addClass(Class<?> clazz);

	void addInterface(Class<?> clazz);

	void addAnnotation(Class<?> clazz);

	void addEnum(Class<?> clazz);

}
