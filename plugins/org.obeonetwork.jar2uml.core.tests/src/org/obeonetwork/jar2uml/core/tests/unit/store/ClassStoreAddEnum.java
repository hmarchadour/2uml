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

import static org.easymock.EasyMock.createMock;

import java.io.File;
import java.net.Proxy;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreAddEnum extends ClassStoreHelper {

	@Test
	public void oneEnum() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Thread.State.class);
		testStoreSize(classStore, 0, 0, 1, 0, 1);
	}

	@Test
	public void twoEnums() {
		ClassStore classStore = Factory.createClassStore();
		File file = createMock(File.class);
		classStore.add(file, Thread.State.class);
		classStore.add(file, Proxy.Type.class);
		testStoreSize(classStore, 0, 0, 2, 0, 1);
	}

	@Test
	public void twoEnumsInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Thread.State.class);
		classStore.add(createMock(File.class), Proxy.Type.class);
		testStoreSize(classStore, 0, 0, 2, 0, 2);
	}
}
