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
package org.obeonetwork.jar2uml.core.tests.unit.store;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

public class JarStoreClass {

	@Test
	public void oneClass() {
		JarStore jarStore = Factory.createJarStore();
		File fileMock = new File("");
		jarStore.add(fileMock, Class.class);
		TestUtils.testStoreSize(jarStore, 1, 0, 0, 0, 1);
	}

	@Test
	public void twoClasses() {
		JarStore jarStore = Factory.createJarStore();
		File fileMock = new File("");
		jarStore.add(fileMock, Class.class);
		jarStore.add(fileMock, Integer.class);
		TestUtils.testStoreSize(jarStore, 2, 0, 0, 0, 1);
	}

	@Test
	public void twoClassesInTwoFiles() {
		JarStore jarStore = Factory.createJarStore();
		File fileMock = new File("fileMock");
		jarStore.add(fileMock, Class.class);
		File fileMock2 = new File("fileMock2");
		jarStore.add(fileMock2, Integer.class);
		TestUtils.testStoreSize(jarStore, 2, 0, 0, 0, 2);
	}

}
