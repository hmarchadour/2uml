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

import org.easymock.EasyMock;
import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.tests.api.Utils;

public class ClassStoreAddClass {

	@Test
	public void oneClass() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = EasyMock.createMock(File.class);
		classStore.add(fileMock, Class.class);
		Utils.testStoreSize(classStore, 1, 0, 0, 0, 1);
	}

	@Test
	public void twoClasses() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = EasyMock.createMock(File.class);
		classStore.add(fileMock, Class.class);
		classStore.add(fileMock, Integer.class);
		Utils.testStoreSize(classStore, 2, 0, 0, 0, 1);
	}

	@Test
	public void twoClassesInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = EasyMock.createMock(File.class);
		classStore.add(fileMock, Class.class);
		File fileMock2 = EasyMock.createMock(File.class);
		classStore.add(fileMock2, Integer.class);
		Utils.testStoreSize(classStore, 2, 0, 0, 0, 2);
	}

}
