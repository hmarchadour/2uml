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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

import demo.ArrayFields;
import demo.Fields;

/**
 * In this testCase, we have already visited the internal classes. We will handle external links and internal
 * links.
 */
@RunWith(Parameterized.class)
public class JavaRelationHandlerFields {

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

	public JavaRelationHandlerFields(String testCaseName, Field fieldToUse, Class<?>[] internalItemsToFind,
			Class<?>[] externalItemsToFind) {
		this.fieldToUse = fieldToUse;
		this.internalItemsToFind = internalItemsToFind;
		this.externalItemsToFind = externalItemsToFind;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();

		Set<Field> fieldsToTest = new HashSet<Field>();

		fieldsToTest.addAll(Arrays.asList(Fields.class.getDeclaredFields()));
		fieldsToTest.addAll(Arrays.asList(ArrayFields.class.getDeclaredFields()));

		for (Field fieldToTest : fieldsToTest) {

			String testCaseName = fieldToTest.getName();
			Class<?> fieldType = fieldToTest.getType();
			if (fieldType.isArray()) {
				fieldType = fieldToTest.getType().getComponentType();
			}

			if (fieldType.isPrimitive()) {
				params.add(new Object[] {testCaseName, fieldToTest,
						new Class<?>[] {fieldToTest.getDeclaringClass()}, new Class<?>[] {}});
			} else {
				params.add(new Object[] {testCaseName, fieldToTest,
						new Class<?>[] {fieldToTest.getDeclaringClass()}, new Class<?>[] {fieldType}});
			}
		}
		return params;
	}

	@Test
	public void caseField() {
		internal.add(fieldToUse.getDeclaringClass());
		// We need to visit the class owner before its field
		javaRelationHandler.caseClass(fieldToUse.getDeclaringClass());
		javaRelationHandler.caseField(fieldToUse);

		TestUtils.checkStores(internal, external, internalItemsToFind, externalItemsToFind);
	}

}
