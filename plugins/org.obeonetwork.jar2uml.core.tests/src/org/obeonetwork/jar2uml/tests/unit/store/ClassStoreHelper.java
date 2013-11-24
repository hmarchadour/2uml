package org.obeonetwork.jar2uml.tests.unit.store;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map.Entry;
import java.util.Set;

import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public class ClassStoreHelper {

	protected void testStoreSize(ClassStore classStore, int nbClass, int nbInterface, int nbEnum,
			int nbAnnotation, int nbFile) {
		int sum = nbClass + nbInterface + nbEnum + nbAnnotation;
		assertEquals("Java items", sum, classStore.getAllJavaItems().size());

		int count = 0;
		for (Entry<File, Set<Class<?>>> entry : classStore.getFile2JavaItemsBinding().entrySet()) {
			count += entry.getValue().size();
		}
		assertEquals("Java items", sum, count);

		assertEquals("Class items", nbClass, classStore.getAllJavaClasses().size());
		assertEquals("Interface items", nbInterface, classStore.getAllJavaInterfaces().size());
		assertEquals("Enum items", nbEnum, classStore.getAllJavaEnums().size());
		assertEquals("Annotation items", nbAnnotation, classStore.getAllJavaAnnotations().size());
		assertEquals("File items", nbFile, classStore.getFiles().size());
		assertEquals("File items", nbFile, classStore.getFile2JavaItemsBinding().keySet().size());
	}

}
