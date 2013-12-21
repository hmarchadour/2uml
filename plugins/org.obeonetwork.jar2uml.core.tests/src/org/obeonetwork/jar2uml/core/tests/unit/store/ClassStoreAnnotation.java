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
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.tests.api.Utils;

public class ClassStoreAnnotation {

	@Test
	public void oneAnnotationInOneFile() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(new File(""), Override.class);
		Utils.testStoreSize(classStore, 0, 0, 0, 1, 1);
	}

	@Test
	public void twoAnnotationsInOneFile() {
		ClassStore classStore = Factory.createClassStore();
		File file = new File("");
		classStore.add(file, Override.class);
		classStore.add(file, Deprecated.class);
		Utils.testStoreSize(classStore, 0, 0, 0, 2, 1);
	}

	@Test
	public void twoAnnotationsInOneFileInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(new File(""), Override.class);
		classStore.add(new File(""), Deprecated.class);
		Utils.testStoreSize(classStore, 0, 0, 0, 2, 1);
	}

}
