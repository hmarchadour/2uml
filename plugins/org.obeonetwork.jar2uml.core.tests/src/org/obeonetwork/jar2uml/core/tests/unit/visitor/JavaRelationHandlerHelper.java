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

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import demo.ArrayFields;

@RunWith(Parameterized.class)
public abstract class JavaRelationHandlerHelper {

	protected final Class<?> clazz;

	protected final Class<?>[] internalToFind;

	protected final Class<?>[] externalToFind;

	protected ClassStore internal;

	protected ClassStore external;

	protected JavaVisitorHandler<Void> javaRelationHandler;

	@Before
	public void setUp() throws Exception {
		internal = Factory.createClassStore();
		external = Factory.createClassStore();
		javaRelationHandler = Factory.createJavaRelationHandler(internal, external);
	}

	public JavaRelationHandlerHelper(Class<?> clazzToUse, Class<?>[] internalToFind, Class<?>[] externalToFind) {
		this.clazz = clazzToUse;
		this.internalToFind = internalToFind;
		this.externalToFind = externalToFind;
	}

	@Parameters
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		params.add(new Object[] {ArrayFields.class, new Class<?>[] {}, new Class<?>[] {}});
		return params;
	}

}
