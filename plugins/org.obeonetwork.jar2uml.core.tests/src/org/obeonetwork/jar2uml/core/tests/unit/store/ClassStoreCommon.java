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

public class ClassStoreCommon {

	@Test
	public void defaultConstructor() {
		ClassStore classStore = Factory.createClassStore();
		Utils.testStoreSize(classStore, 0, 0, 0, 0, 0);
	}

	@Test
	public void oneOfEach() {
		ClassStore classStore = Factory.createClassStore();
		File file = EasyMock.createMock(File.class);
		classStore.add(file, Class.class);
		classStore.add(file, Override.class);
		classStore.add(file, Thread.State.class);
		classStore.add(file, Cloneable.class);
		Utils.testStoreSize(classStore, 1, 1, 1, 1, 1);
	}
}
