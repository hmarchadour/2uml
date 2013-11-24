package org.obeonetwork.jar2uml.tests.unit.store;

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreAddCommon extends ClassStoreHelper {

	@Test
	public void defaultConstructor() {
		ClassStore classStore = Factory.createClassStore();
		testStoreSize(classStore, 0, 0, 0, 0, 0);
	}

	@Test
	public void oneOfEach() {
		ClassStore classStore = Factory.createClassStore();
		File file = createMock(File.class);
		classStore.add(file, Class.class);
		classStore.add(file, Override.class);
		classStore.add(file, Thread.State.class);
		classStore.add(file, Cloneable.class);
		testStoreSize(classStore, 1, 1, 1, 1, 1);
	}
}
