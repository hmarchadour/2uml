/*******************************************************************************
 * Copyright (c) 2014 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.tests.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.Set;

import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.store.JarStore;

public final class TestUtils {

	public static String INVALID_INTERNAL_CTX = "Invalid internal context";

	public static String INVALID_EXTERNAL_CTX = "Invalid external context";

	public static String NOT_RETRIEVE_IN_INTERNAL = " not retrieve in internal items";

	public static String NOT_RETRIEVE_IN_EXTERNAL = " not retrieve in external items";

	public static File getFile(String filename) {
		File file = null;
		try {
			URI uri = TestUtils.class.getClassLoader().getResource(filename).toURI();
			file = new File(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * This function test the store size.
	 * 
	 * @param jarStore
	 *            the store under test
	 * @param nbClass
	 *            the expected number of classes
	 * @param nbInterface
	 *            the expected number of interface
	 * @param nbEnum
	 *            the expected number of enums
	 * @param nbAnnotation
	 *            the expected number of annotations
	 * @param nbFile
	 *            the expected number of files
	 */
	public static void testStoreSize(ClassStore jarStore, int nbClass, int nbInterface, int nbEnum,
			int nbAnnotation) {
		int sum = nbClass + nbInterface + nbEnum + nbAnnotation;
		assertEquals("Java items", sum, jarStore.getAllJavaItems().size());

		int count = 0;
		assertEquals("Java items", sum, count);

		assertEquals("Class items", nbClass, jarStore.getAllJavaClasses().size());
		assertEquals("Interface items", nbInterface, jarStore.getAllJavaInterfaces().size());
		assertEquals("Enum items", nbEnum, jarStore.getAllJavaEnums().size());
		assertEquals("Annotation items", nbAnnotation, jarStore.getAllJavaAnnotations().size());
	}

	/**
	 * This function test the store size.
	 * 
	 * @param jarStore
	 *            the store under test
	 * @param nbClass
	 *            the expected number of classes
	 * @param nbInterface
	 *            the expected number of interface
	 * @param nbEnum
	 *            the expected number of enums
	 * @param nbAnnotation
	 *            the expected number of annotations
	 * @param nbFile
	 *            the expected number of files
	 */
	public static void testStoreSize(JarStore jarStore, int nbClass, int nbInterface, int nbEnum,
			int nbAnnotation, int nbFile) {
		int sum = nbClass + nbInterface + nbEnum + nbAnnotation;
		assertEquals("Java items", sum, jarStore.getAllJavaItems().size());

		int count = 0;
		for (Entry<File, Set<Class<?>>> entry : jarStore.getFile2JavaItemsBinding().entrySet()) {
			count += entry.getValue().size();
		}
		assertEquals("Java items", sum, count);

		assertEquals("Class items", nbClass, jarStore.getAllJavaClasses().size());
		assertEquals("Interface items", nbInterface, jarStore.getAllJavaInterfaces().size());
		assertEquals("Enum items", nbEnum, jarStore.getAllJavaEnums().size());
		assertEquals("Annotation items", nbAnnotation, jarStore.getAllJavaAnnotations().size());
		assertEquals("File items", nbFile, jarStore.getFiles().size());
		assertEquals("File items", nbFile, jarStore.getFile2JavaItemsBinding().keySet().size());
	}

	/**
	 * Check that stores contain expected items
	 * 
	 * @param internal
	 *            internal store
	 * @param external
	 *            external store
	 * @param internalItemsToFind
	 *            internal items to find
	 * @param externalItemsToFind
	 *            external items to find
	 */
	public static void checkStores(JarStore internal, JarStore external, Class<?>[] internalItemsToFind,
			Class<?>[] externalItemsToFind) {
		for (Class<?> internalClassToFind : internalItemsToFind) {
			assertTrue(internalClassToFind.getName() + TestUtils.NOT_RETRIEVE_IN_INTERNAL, internal
					.getAllJavaItems().contains(internalClassToFind));
		}
		// Internal checks
		assertEquals(TestUtils.INVALID_INTERNAL_CTX, internalItemsToFind.length, internal.getAllJavaItems()
				.size());

		for (Class<?> externalClassToFind : externalItemsToFind) {
			assertTrue(externalClassToFind.getName() + TestUtils.NOT_RETRIEVE_IN_EXTERNAL, external
					.getAllJavaItems().contains(externalClassToFind));
		}
		// External checks
		assertEquals(TestUtils.INVALID_EXTERNAL_CTX, externalItemsToFind.length, external.getAllJavaItems()
				.size());
	}

	/**
	 * Check that stores contain expected items
	 * 
	 * @param internal
	 *            internal store
	 * @param external
	 *            external store
	 * @param internalItemsToFind
	 *            internal items to find
	 * @param externalItemsToFind
	 *            external items to find
	 */
	public static void checkStores(ClassStore internal, ClassStore external, Class<?>[] internalItemsToFind,
			Class<?>[] externalItemsToFind) {
		for (Class<?> internalClassToFind : internalItemsToFind) {
			assertTrue(internalClassToFind.getName() + TestUtils.NOT_RETRIEVE_IN_INTERNAL, internal
					.getAllJavaItems().contains(internalClassToFind));
		}
		// Internal checks
		assertEquals(TestUtils.INVALID_INTERNAL_CTX, internalItemsToFind.length, internal.getAllJavaItems()
				.size());

		for (Class<?> externalClassToFind : externalItemsToFind) {
			assertTrue(externalClassToFind.getName() + TestUtils.NOT_RETRIEVE_IN_EXTERNAL, external
					.getAllJavaItems().contains(externalClassToFind));
		}
		// External checks
		assertEquals(TestUtils.INVALID_EXTERNAL_CTX, externalItemsToFind.length, external.getAllJavaItems()
				.size());
	}
}
