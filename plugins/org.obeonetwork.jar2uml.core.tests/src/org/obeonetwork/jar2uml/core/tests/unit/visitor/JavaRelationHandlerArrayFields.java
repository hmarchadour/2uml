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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import demo.ArrayFields;

@RunWith(Parameterized.class)
public class JavaRelationHandlerArrayFields {

	protected final Field fieldToUse;

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

	/**
	 * @param clazzToUse
	 * @param internalToFind
	 * @param externalToFind
	 */
	public JavaRelationHandlerArrayFields(String testCaseName, Field fieldToUse,
			Class<?>[] internalItemsToFind, Class<?>[] externalItemsToFind) {
		this.fieldToUse = fieldToUse;
		this.internalItemsToFind = internalItemsToFind;
		this.externalItemsToFind = externalItemsToFind;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		Field[] declaredArrayFields = ArrayFields.class.getDeclaredFields();
		for (Field arrayField : declaredArrayFields) {
			String testCaseName = arrayField.getName();
			if (arrayField.getType().getComponentType().isPrimitive()) {
				params.add(new Object[] {testCaseName, arrayField,
						new Class<?>[] {arrayField.getDeclaringClass()}, new Class<?>[] {}});
			} else {
				params.add(new Object[] {testCaseName, arrayField,
						new Class<?>[] {arrayField.getDeclaringClass()},
						new Class<?>[] {arrayField.getType().getComponentType()}});
			}
		}
		return params;
	}

	@Test
	public void caseField() {
		internal.add(new File(""), fieldToUse.getDeclaringClass());
		// We need to visit the class owner before its field
		javaRelationHandler.caseClass(fieldToUse.getDeclaringClass());
		javaRelationHandler.caseField(fieldToUse);

		// Internal checks
		assertEquals("Invalid internal context", internalItemsToFind.length, internal.getAllJavaItems()
				.size());
		for (Class<?> internalClassToFind : internalItemsToFind) {
			assertTrue(internalClassToFind.getName() + " not retrieve in internal items", internal
					.getAllJavaItems().contains(internalClassToFind));
		}

		// External checks
		assertEquals("Invalid external context", externalItemsToFind.length, external.getAllJavaItems()
				.size());
		for (Class<?> externalClassToFind : externalItemsToFind) {
			assertTrue(externalClassToFind.getName() + " not retrieve in internal items", external
					.getAllJavaItems().contains(externalClassToFind));
		}
	}

}
