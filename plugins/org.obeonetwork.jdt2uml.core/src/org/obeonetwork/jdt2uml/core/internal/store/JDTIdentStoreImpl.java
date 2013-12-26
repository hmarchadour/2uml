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
package org.obeonetwork.jdt2uml.core.internal.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;

public class JDTIdentStoreImpl implements JDTStore<String> {

	private final Set<String> interfaces;

	private final Set<String> classes;

	private final Set<String> enums;

	private final Set<String> annotations;

	public JDTIdentStoreImpl() {
		classes = new HashSet<String>();
		interfaces = new HashSet<String>();
		enums = new HashSet<String>();
		annotations = new HashSet<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAllJavaItems() {
		Set<String> result = new HashSet<String>();
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
	public Set<String> getAllJavaClasses() {
		return Collections.unmodifiableSet(classes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAllJavaInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAllJavaAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAllJavaEnums() {
		return Collections.unmodifiableSet(enums);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(IType type) {
		if (Utils.isAnnotation(type)) {
			addAnnotation(type);
		} else if (Utils.isEnum(type)) {
			addEnum(type);
		} else if (Utils.isInterface(type)) {
			addInterface(type);
		} else if (Utils.isClass(type)) {
			addClass(type);
		} else {
			throw new IllegalArgumentException("class " + type.getElementName()
					+ " is not a class, an interface, an enum or an annotation");
		}
	}

	protected void addClass(IType type) {
		if (!Utils.isClass(type)) {
			throw new IllegalArgumentException("type is not a Java class");
		}
		classes.add(type.getFullyQualifiedName());
	}

	protected void addInterface(IType type) {
		if (!Utils.isInterface(type)) {
			throw new IllegalArgumentException("type is not a Java interface");
		}
		interfaces.add(type.getFullyQualifiedName());
	}

	protected void addAnnotation(IType type) {
		if (!Utils.isAnnotation(type)) {
			throw new IllegalArgumentException("type is not a Java annotation");
		}
		annotations.add(type.getFullyQualifiedName());
	}

	protected void addEnum(IType type) {
		if (!Utils.isEnum(type)) {
			throw new IllegalArgumentException("type is not a Java enum");
		}
		enums.add(type.getFullyQualifiedName());
	}

	@Override
	public String toString() {
		return "Classes:" + classes.size() + " interfaces:" + interfaces.size() + " annotations:"
				+ annotations.size() + " enums:" + enums.size();
	}

	@Override
	public boolean exist(String item) {
		boolean exist = annotations.contains(item);
		if (!exist) {
			exist = enums.contains(item);
			if (!exist) {
				exist = interfaces.contains(item);
				if (!exist) {
					exist = classes.contains(item);
				}
			}
		}
		return exist;
	}

}
