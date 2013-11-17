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

import static org.obeonetwork.jar2uml.core.api.Utils.isAnnotation;
import static org.obeonetwork.jar2uml.core.api.Utils.isClass;
import static org.obeonetwork.jar2uml.core.api.Utils.isEnum;
import static org.obeonetwork.jar2uml.core.api.Utils.isInterface;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.obeonetwork.jar2uml.core.api.Utils;

import com.google.common.base.Optional;

public class ClassStore {

	private final Map<File, Set<Class<?>>> binding;

	private final Set<Class<?>> interfaces;

	private final Set<Class<?>> classes;

	private final Set<Class<?>> enums;

	private final Set<Class<?>> annotations;

	public ClassStore() {
		binding = new HashMap<File, Set<Class<?>>>();
		classes = new HashSet<Class<?>>();
		interfaces = new HashSet<Class<?>>();
		enums = new HashSet<Class<?>>();
		annotations = new HashSet<Class<?>>();
	}

	public Set<File> getFiles() {
		return Collections.unmodifiableSet(binding.keySet());
	}

	public Map<File, Set<Class<?>>> getFile2JavaItemsBinding() {
		return Collections.unmodifiableMap(binding);
	}

	public Optional<File> retrieveFile(Class<?> clazz) {
		Optional<File> result = Optional.absent();
		for (File file : binding.keySet()) {
			Set<Class<?>> classes = binding.get(file);
			if (classes.contains(clazz)) {
				result = Optional.of(file);
				break;
			}
		}
		return result;
	}

	public Set<Class<?>> getAllJavaItems() {
		Set<Class<?>> result = new HashSet<Class<?>>();
		result.addAll(classes);
		result.addAll(interfaces);
		result.addAll(enums);
		result.addAll(annotations);
		return Collections.unmodifiableSet(result);
	}

	public Set<Class<?>> getAllJavaClasses() {
		return Collections.unmodifiableSet(classes);
	}

	public Set<Class<?>> getAllJavaInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	public Set<Class<?>> getAllJavaAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	public Set<Class<?>> getAllJavaEnums() {
		return Collections.unmodifiableSet(enums);
	}

	public void add(File file, Class<?> clazz) {
		// System.out.println("Add Java " + clazz.getSimpleName());
		if (clazz.isArray()) {
			add(file, clazz.getComponentType());
		} else if (Utils.isInterface(clazz)) {
			addInterface(file, clazz);
		} else if (Utils.isEnum(clazz)) {
			addEnum(file, clazz);
		} else if (Utils.isAnnotation(clazz)) {
			addAnnotation(file, clazz);
		} else if (Utils.isClass(clazz)) {
			addClass(file, clazz);
		} else if (Utils.isPrimitive(clazz)) {
			// Primitive types are not kept
		} else {
			throw new IllegalArgumentException("class is not a class, an interface, an enum or an annotation");
		}
	}

	public void addClass(File file, Class<?> clazz) {
		if (!isClass(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java class");
		}
		classes.add(clazz);
		addBinding(file, clazz);
	}

	public void addInterface(File file, Class<?> clazz) {
		if (!isInterface(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java interface");
		}
		interfaces.add(clazz);
		addBinding(file, clazz);
	}

	public void addAnnotation(File file, Class<?> clazz) {
		if (!isAnnotation(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java annotation");
		}
		annotations.add(clazz);
		addBinding(file, clazz);
	}

	public void addEnum(File file, Class<?> clazz) {
		if (!isEnum(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java enum");
		}
		enums.add(clazz);
		addBinding(file, clazz);
	}

	protected void addBinding(File file, Class<?> clazz) {
		if (binding.containsKey(file)) {
			Set<Class<?>> classes = binding.get(file);
			classes.add(clazz);
		} else {
			HashSet<Class<?>> classes = new HashSet<Class<?>>();
			classes.add(clazz);
			binding.put(file, classes);
		}
	}
}
