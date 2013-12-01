package org.obeonetwork.jar2uml.core.tests.unit.store;

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreAddClass extends ClassStoreHelper {

	@Test
	public void oneClass() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = createMock(File.class);
		classStore.add(fileMock, Class.class);
		testStoreSize(classStore, 1, 0, 0, 0, 1);
	}

	@Test
	public void twoClasses() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = createMock(File.class);
		classStore.add(fileMock, Class.class);
		classStore.add(fileMock, Integer.class);
		testStoreSize(classStore, 2, 0, 0, 0, 1);
	}

	@Test
	public void twoClassesInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		File fileMock = createMock(File.class);
		classStore.add(fileMock, Class.class);
		File fileMock2 = createMock(File.class);
		classStore.add(fileMock2, Integer.class);
		testStoreSize(classStore, 2, 0, 0, 0, 2);
	}

}
