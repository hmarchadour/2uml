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
import java.net.Proxy;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

public class JarStoreEnum {

	@Test
	public void oneEnum() {
		JarStore jarStore = Factory.createJarStore();
		jarStore.add(new File(""), Thread.State.class);
		TestUtils.testStoreSize(jarStore, 0, 0, 1, 0, 1);
	}

	@Test
	public void twoEnums() {
		JarStore jarStore = Factory.createJarStore();
		File file = new File("");
		jarStore.add(file, Thread.State.class);
		jarStore.add(file, Proxy.Type.class);
		TestUtils.testStoreSize(jarStore, 0, 0, 2, 0, 1);
	}

	@Test
	public void twoEnumsInTwoFiles() {
		JarStore jarStore = Factory.createJarStore();
		jarStore.add(new File("fileMock1"), Thread.State.class);
		jarStore.add(new File("fileMock2"), Proxy.Type.class);
		TestUtils.testStoreSize(jarStore, 0, 0, 2, 0, 2);
	}
}
