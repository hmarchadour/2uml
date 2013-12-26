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

import java.lang.reflect.Constructor;
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

import demo.PrivateConstructors;
import demo.ProtectedConstructors;
import demo.PublicConstructors;

@RunWith(Parameterized.class)
public class JavaRelationHandlerConstructors {

	protected final Constructor constructorToUse;

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

	public JavaRelationHandlerConstructors(String testCaseName, Constructor constructorToUse,
			Class<?>[] internalItemsToFind, Class<?>[] externalItemsToFind) {
		this.constructorToUse = constructorToUse;
		this.internalItemsToFind = internalItemsToFind;
		this.externalItemsToFind = externalItemsToFind;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		Class<?>[] constructorClasses = new Class<?>[] {PublicConstructors.class,
				ProtectedConstructors.class, PrivateConstructors.class};
		for (Class<?> constructorClass : constructorClasses) {
			for (Constructor constructor : constructorClass.getDeclaredConstructors()) {

				String testCaseName;
				Class<?>[] parameterTypes = constructor.getParameterTypes();

				Class<?> paramClass = null;
				if (parameterTypes.length > 0) {
					assertEquals("We cannot have more that one parameter by constructor", 1,
							parameterTypes.length);

					paramClass = parameterTypes[0];
					if (paramClass.isArray()) {
						paramClass = paramClass.getComponentType();
					}
					testCaseName = constructorClass.getSimpleName() + " " + paramClass.toString();
				} else {
					testCaseName = constructorClass.getSimpleName() + " No arg";
				}

				if (paramClass == null || paramClass.isPrimitive()) {
					params.add(new Object[] {testCaseName, constructor,
							new Class<?>[] {constructor.getDeclaringClass()}, new Class<?>[] {}});
				} else {
					params.add(new Object[] {testCaseName, constructor,
							new Class<?>[] {constructor.getDeclaringClass()}, new Class<?>[] {paramClass}});
				}
			}
		}
		return params;
	}

	@Test
	public void caseConstructor() {
		internal.add(constructorToUse.getDeclaringClass());
		// We need to visit the class owner before its field
		javaRelationHandler.caseClass(constructorToUse.getDeclaringClass());
		javaRelationHandler.caseConstructor(constructorToUse);

		TestUtils.checkStores(internal, external, internalItemsToFind, externalItemsToFind);
	}

}
