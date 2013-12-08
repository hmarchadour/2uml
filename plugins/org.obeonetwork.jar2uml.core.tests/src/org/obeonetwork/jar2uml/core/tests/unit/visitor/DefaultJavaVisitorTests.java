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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
		mockedVisitorHandler = createMock(JavaVisitorHandler.class);
		javaVisitor = Factory.createJavaVisitor(mockedVisitorHandler);
	}

	private void setDefaultMockClass(Class<?> clazz) {
		mockedVisitorHandler.caseClass(clazz);
		mockedVisitorHandler.caseSuperClass(Object.class);
		assertEquals(1, clazz.getConstructors().length);
		mockedVisitorHandler.caseConstructor(clazz.getConstructors()[0]);
	}

	private void testJavaVisitor(Class<?> clazz) {
		replay(mockedVisitorHandler);
		javaVisitor.visit(clazz);
		verify(mockedVisitorHandler);
	}

	@Test
	public void testFields() {
		setDefaultMockClass(Fields.class);
		Field[] fields = Fields.class.getFields();
		for (Field field : fields) {
			mockedVisitorHandler.caseField(field);
		}
		testJavaVisitor(Fields.class);
	}

	@Test
	public void testArrayFields() {
		setDefaultMockClass(ArrayFields.class);
		Field[] fields = ArrayFields.class.getFields();
		for (Field field : fields) {
			mockedVisitorHandler.caseField(field);
		}
		testJavaVisitor(ArrayFields.class);
	}

	@Test
	public void testMethods() {
		setDefaultMockClass(Methods.class);
		Method[] methods = Methods.class.getDeclaredMethods();
		for (Method method : methods) {
			mockedVisitorHandler.caseMethod(method);
		}
		testJavaVisitor(Methods.class);
	}

	@Test
	public void testPublicConstructors() {
		Constructor<?>[] constructors = PublicConstructors.class.getDeclaredConstructors();
		mockedVisitorHandler.caseClass(PublicConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);
		for (Constructor constructor : constructors) {
			mockedVisitorHandler.caseConstructor(constructor);
		}
		testJavaVisitor(PublicConstructors.class);
	}

	@Test
	public void testProtectedConstructors() {
		Constructor<?>[] constructors = ProtectedConstructors.class.getDeclaredConstructors();
		mockedVisitorHandler.caseClass(ProtectedConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);
		for (Constructor constructor : constructors) {
			mockedVisitorHandler.caseConstructor(constructor);
		}
		testJavaVisitor(ProtectedConstructors.class);
	}

	@Test
	public void testPrivateConstructors() {
		Constructor<?>[] constructors = PrivateConstructors.class.getDeclaredConstructors();
		mockedVisitorHandler.caseClass(PrivateConstructors.class);
		mockedVisitorHandler.caseSuperClass(Object.class);
		for (Constructor constructor : constructors) {
			mockedVisitorHandler.caseConstructor(constructor);
		}
		testJavaVisitor(PrivateConstructors.class);
	}
}
