package org.obeonetwork.jar2uml.tests.unit.store;

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreAddInterface extends ClassStoreHelper {

	@Test
	public void oneInterface() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Cloneable.class);
		testStoreSize(classStore, 0, 1, 0, 0, 1);
	}

	@Test
	public void twoInterfaces() {
		ClassStore classStore = Factory.createClassStore();
		File file = createMock(File.class);
		classStore.add(file, Cloneable.class);
		classStore.add(file, Comparable.class);
		testStoreSize(classStore, 0, 2, 0, 0, 1);
	}

	@Test
	public void twoInterfacesInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Cloneable.class);
		classStore.add(createMock(File.class), Comparable.class);
		testStoreSize(classStore, 0, 2, 0, 0, 2);
	}

}
