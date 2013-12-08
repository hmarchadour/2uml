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
package org.obeonetwork.jar2uml.core.tests.unit.visitor;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import demo.ArrayFields;
import demo.Fields;
import demo.Methods;
import demo.PrivateConstructors;
import demo.ProtectedConstructors;
import demo.PublicConstructors;

public class DefaultJavaVisitorTests {

	private JavaVisitorHandler<Void> mockedVisitorHandler;

	private JavaVisitor javaVisitor;

	@Before
	public void setUp() throws Exception {
		mockedVisitorHandler = EasyMock.createMock(JavaVisitorHandler.class);
		javaVisitor = Factory.createJavaVisitor(mockedVisitorHandler);
	}

	@Test
	public void visitFields() {
		mockedVisitorHandler.caseClass(Fields.class);
		mockedVisitorHandler.caseSuperClass(Fields.class.getSuperclass());
		mockedVisitorHandler.caseConstructor(Fields.class.getConstructors()[0]);

		Field[] fields = Fields.class.getFields();
		for (Field field : fields) {
			// we expect to visit all fields
			mockedVisitorHandler.caseField(field);
		}
		launchVisitOn(Fields.class);
	}

	@Test
	public void visitArrayFields() {
		mockedVisitorHandler.caseClass(ArrayFields.class);
		mockedVisitorHandler.caseSuperClass(ArrayFields.class.getSuperclass());
		mockedVisitorHandler.caseConstructor(ArrayFields.class.getConstructors()[0]);

		Field[] fields = ArrayFields.class.getFields();
		for (Field field : fields) {
			// we expect to visit all array fields
			mockedVisitorHandler.caseField(field);
		}
		launchVisitOn(ArrayFields.class);
	}

	@Test
	public void visitMethods() {
		mockedVisitorHandler.caseClass(Methods.class);
		mockedVisitorHandler.caseSuperClass(Methods.class.getSuperclass());
		mockedVisitorHandler.caseConstructor(Methods.class.getConstructors()[0]);

		Method[] methods = Methods.class.getDeclaredMethods();
		for (Method method : methods) {
			// we expect to visit all methods
			mockedVisitorHandler.caseMethod(method);
		}
		launchVisitOn(Methods.class);
	}

	@Test
	public void visitPublicConstructors() {
		mockedVisitorHandler.caseClass(PublicConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);

		Constructor<?>[] constructors = PublicConstructors.class.getDeclaredConstructors();
		for (Constructor constructor : constructors) {
			// we expect to visit all constructors
			mockedVisitorHandler.caseConstructor(constructor);
		}
		launchVisitOn(PublicConstructors.class);
	}

	@Test
	public void visitProtectedConstructors() {
		mockedVisitorHandler.caseClass(ProtectedConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);

		Constructor<?>[] constructors = ProtectedConstructors.class.getDeclaredConstructors();
		for (Constructor constructor : constructors) {
			// we expect to visit all constructors
			mockedVisitorHandler.caseConstructor(constructor);
		}
		launchVisitOn(ProtectedConstructors.class);
	}

	@Test
	public void visitPrivateConstructors() {
		mockedVisitorHandler.caseClass(PrivateConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);

		Constructor<?>[] constructors = PrivateConstructors.class.getDeclaredConstructors();
		for (Constructor constructor : constructors) {
			// we expect to visit all constructors
			mockedVisitorHandler.caseConstructor(constructor);
		}
		launchVisitOn(PrivateConstructors.class);
	}

	private void launchVisitOn(Class<?> clazz) {
		replay(mockedVisitorHandler);
		javaVisitor.visit(clazz);
		verify(mockedVisitorHandler);
	}
}
