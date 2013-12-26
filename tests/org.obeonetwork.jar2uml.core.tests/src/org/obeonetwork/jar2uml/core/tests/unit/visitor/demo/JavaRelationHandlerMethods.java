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
package org.obeonetwork.jar2uml.core.tests.unit.visitor.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

import demo.Functions;
import demo.Methods;

@RunWith(Parameterized.class)
public class JavaRelationHandlerMethods {

	protected final Method methodToUse;

	protected final Class<?>[] internalItemsToFind;

	protected final Class<?>[] externalItemsToFind;

	protected ClassStore internal;

	protected ClassStore external;

	protected JavaVisitorHandler<Void> javaRelationHandler;

	@Before
	public void setUp() throws Exception {
		internal = Factory.createClassStore();
		external = Factory.createClassStore();
		javaRelationHandler = Factory.createJavaRelationHandler(internal, external);
	}

	public JavaRelationHandlerMethods(String testCaseName, Method methodToUse,
			Class<?>[] internalItemsToFind, Class<?>[] externalItemsToFind) {
		this.methodToUse = methodToUse;
		this.internalItemsToFind = internalItemsToFind;
		this.externalItemsToFind = externalItemsToFind;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();

		for (Method method : Methods.class.getDeclaredMethods()) {
			String testCaseName = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();
			assertEquals("We have on parameter by method", 1, parameterTypes.length);
			Class<?> paramClass = parameterTypes[0];
			if (paramClass.isArray()) {
				paramClass = paramClass.getComponentType();
			}
			if (paramClass.isPrimitive()) {
				params.add(new Object[] {testCaseName, method, new Class<?>[] {method.getDeclaringClass()},
						new Class<?>[] {}});
			} else {
				params.add(new Object[] {testCaseName, method, new Class<?>[] {method.getDeclaringClass()},
						new Class<?>[] {paramClass}});
			}
		}

		for (Method function : Functions.class.getDeclaredMethods()) {

			String testCaseName = function.getName();
			Class<?> returnType = function.getReturnType();
			assertNotNull("We have a return type by function", returnType);
			if (returnType.isArray()) {
				returnType = returnType.getComponentType();
			}
			if (returnType.isPrimitive()) {
				params.add(new Object[] {testCaseName, function,
						new Class<?>[] {function.getDeclaringClass()}, new Class<?>[] {}});
			} else {
				params.add(new Object[] {testCaseName, function,
						new Class<?>[] {function.getDeclaringClass()}, new Class<?>[] {returnType}});
			}
		}
		return params;
	}

	@Test
	public void caseMethod() {
		internal.add(methodToUse.getDeclaringClass());
		// We need to visit the class owner before its field
		javaRelationHandler.caseClass(methodToUse.getDeclaringClass());
		javaRelationHandler.caseMethod(methodToUse);
		TestUtils.checkStores(internal, external, internalItemsToFind, externalItemsToFind);
	}

}
