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
package org.obeonetwork.jar2uml.core.internal.visitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class DefaultJavaVisitor implements JavaVisitor {

	private final JavaVisitorHandler<?> handler;

	public DefaultJavaVisitor(JavaVisitorHandler<?> handler) {
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Class<?> clazz) {
		if (Utils.validJavaItem(clazz)) {

			if (Utils.isPrimitive(clazz)) {
				visitPrimitive(clazz);
			} else if (Utils.isAnnotation(clazz)) {
				visitAnnotation(clazz);
			} else if (Utils.isEnum(clazz)) {
				visitEnum(clazz);
			} else if (Utils.isInterface(clazz)) {
				visitInterface(clazz);
			} else if (Utils.isClass(clazz)) {
				visitClass(clazz);
			}

			// Super type
			Class<?> superclass = Utils.findSuperclass(clazz);
			if (Utils.validJavaItem(superclass)) {
				visitSuperClass(superclass);
			}

			// Implemented interface
			for (Class<?> implementedInterface : Utils.findInterfaces(clazz)) {
				if (Utils.validJavaItem(implementedInterface)) {
					visitImplementedInterface(implementedInterface);
				}
			}

			// Constructors
			for (Constructor<?> constructor : Utils.findConstructors(clazz)) {
				if (Utils.validJavaItem(constructor)) {
					visitConstructor(constructor);
				}
			}

			// Attributes
			for (Field field : Utils.findAttributes(clazz)) {
				if (Utils.validJavaItem(field)) {
					visitField(field);
				}
			}

			// Methods
			for (Method method : Utils.findMethodes(clazz)) {
				if (Utils.validJavaItem(method)) {
					visitMethod(method);
				}
			}
		}
	}

	protected void visitSuperClass(Class<?> aSuperClass) {
		handler.caseSuperClass(aSuperClass);
	}

	protected void visitImplementedInterface(Class<?> anImplInterface) {
		handler.caseImplementedInterface(anImplInterface);
	}

	protected void visitPrimitive(Class<?> aClass) {
		handler.casePrimitive(aClass);
	}

	protected void visitClass(Class<?> aClass) {
		handler.caseClass(aClass);
	}

	protected void visitConstructor(Constructor<?> constructor) {
		handler.caseConstructor(constructor);
	}

	protected void visitInterface(Class<?> anInterface) {
		handler.caseInterface(anInterface);
	}

	protected void visitAnnotation(Class<?> anAnnotation) {
		handler.caseAnnotation(anAnnotation);
	}

	protected void visitEnum(Class<?> anEnum) {
		handler.caseEnum(anEnum);
	}

	protected void visitField(Field aField) {
		handler.caseField(aField);
	}

	protected void visitMethod(Method method) {
		handler.caseMethod(method);
	}
}
