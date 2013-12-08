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
import java.net.Proxy;

import org.easymock.EasyMock;
import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.tests.api.Utils;

public class ClassStoreEnum {

	@Test
	public void oneEnum() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(EasyMock.createMock(File.class), Thread.State.class);
		Utils.testStoreSize(classStore, 0, 0, 1, 0, 1);
	}

	@Test
	public void twoEnums() {
		ClassStore classStore = Factory.createClassStore();
		File file = EasyMock.createMock(File.class);
		classStore.add(file, Thread.State.class);
		classStore.add(file, Proxy.Type.class);
		Utils.testStoreSize(classStore, 0, 0, 2, 0, 1);
	}

	@Test
	public void twoEnumsInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(EasyMock.createMock(File.class), Thread.State.class);
		classStore.add(EasyMock.createMock(File.class), Proxy.Type.class);
		Utils.testStoreSize(classStore, 0, 0, 2, 0, 2);
	}
}
