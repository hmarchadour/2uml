package org.obeonetwork.jar2uml.core.tests.unit.store;

import static org.easymock.EasyMock.createMock;

import java.io.File;

import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreAddAnnotation extends ClassStoreHelper {

	@Test
	public void oneAnnotation() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Override.class);
		testStoreSize(classStore, 0, 0, 0, 1, 1);
	}

	@Test
	public void twoAnnotations() {
		ClassStore classStore = Factory.createClassStore();
		File file = createMock(File.class);
		classStore.add(file, Override.class);
		classStore.add(file, Deprecated.class);
		testStoreSize(classStore, 0, 0, 0, 2, 1);
	}

	@Test
	public void twoAnnotationsInTwoFiles() {
		ClassStore classStore = Factory.createClassStore();
		classStore.add(createMock(File.class), Override.class);
		classStore.add(createMock(File.class), Deprecated.class);
		testStoreSize(classStore, 0, 0, 0, 2, 2);
	}

}
