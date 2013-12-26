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
package org.obeonetwork.jar2uml.core.internal.store;

import static org.obeonetwork.jar2uml.core.api.Utils.isAnnotation;
import static org.obeonetwork.jar2uml.core.api.Utils.isClass;
import static org.obeonetwork.jar2uml.core.api.Utils.isEnum;
import static org.obeonetwork.jar2uml.core.api.Utils.isInterface;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreImpl implements ClassStore {

	private final Set<Class<?>> interfaces;

	private final Set<Class<?>> classes;

	private final Set<Class<?>> enums;

	private final Set<Class<?>> annotations;

	public ClassStoreImpl() {
		classes = new HashSet<Class<?>>();
		interfaces = new HashSet<Class<?>>();
		enums = new HashSet<Class<?>>();
		annotations = new HashSet<Class<?>>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getAllJavaItems() {
		Set<Class<?>> result = new HashSet<Class<?>>();
		result.addAll(classes);
		result.addAll(interfaces);
		result.addAll(enums);
		result.addAll(annotations);
		return Collections.unmodifiableSet(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getAllJavaClasses() {
		return Collections.unmodifiableSet(classes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getAllJavaInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getAllJavaAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getAllJavaEnums() {
		return Collections.unmodifiableSet(enums);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Class<?> clazz) {
		// System.out.println("Add Java " + clazz.getSimpleName());
		if (clazz.isArray()) {
			add(clazz.getComponentType());
		} else if (Utils.isAnnotation(clazz)) {
			addAnnotation(clazz);
		} else if (Utils.isInterface(clazz)) {
			addInterface(clazz);
		} else if (Utils.isEnum(clazz)) {
			addEnum(clazz);
		} else if (Utils.isClass(clazz)) {
			addClass(clazz);
		} else if (Utils.isPrimitive(clazz)) {
			// Primitive types are not kept
		} else {
			throw new IllegalArgumentException("class is not a class, an interface, an enum or an annotation");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addClass(Class<?> clazz) {
		if (!isClass(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java class");
		}
		classes.add(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInterface(Class<?> clazz) {
		if (!isInterface(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java interface");
		}
		interfaces.add(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAnnotation(Class<?> clazz) {
		if (!isAnnotation(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java annotation");
		}
		annotations.add(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEnum(Class<?> clazz) {
		if (!isEnum(clazz)) {
			throw new IllegalArgumentException("clazz is not a Java enum");
		}
		enums.add(clazz);
	}

}
