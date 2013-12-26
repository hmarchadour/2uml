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

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

public interface JarStore extends JavaStore {

	Set<File> getFiles();

	Map<File, Set<Class<?>>> getFile2JavaItemsBinding();

	Optional<File> retrieveFile(Class<?> clazz);

	void add(File file, Class<?> clazz);

	void addClass(File file, Class<?> clazz);

	void addInterface(File file, Class<?> clazz);

	void addAnnotation(File file, Class<?> clazz);

	void addEnum(File file, Class<?> clazz);

	ClassStore toClassStore();
}
