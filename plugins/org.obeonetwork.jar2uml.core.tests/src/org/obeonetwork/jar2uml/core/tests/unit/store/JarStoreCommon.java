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
package org.obeonetwork.jar2uml.core.tests.unit.store;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

public class JarStoreCommon {

	@Test
	public void defaultConstructor() {
		JarStore jarStore = Factory.createJarStore();
		TestUtils.testStoreSize(jarStore, 0, 0, 0, 0, 0);
	}

	@Test
	public void oneOfEach() {
		JarStore jarStore = Factory.createJarStore();
		File file = new File("");
		jarStore.add(file, Class.class);
		jarStore.add(file, Override.class);
		jarStore.add(file, Thread.State.class);
		jarStore.add(file, Cloneable.class);
		TestUtils.testStoreSize(jarStore, 1, 1, 1, 1, 1);
	}
}
