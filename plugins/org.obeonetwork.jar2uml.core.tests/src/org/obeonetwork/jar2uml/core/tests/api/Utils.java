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
package org.obeonetwork.jar2uml.core.tests.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.Set;

import org.obeonetwork.jar2uml.core.api.store.ClassStore;

public final class Utils {
	public static File getFile(String filename) {
		File file = null;
		try {
			URI uri = Utils.class.getClassLoader().getResource(filename).toURI();
			file = new File(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * This function test the store size.
	 * 
	 * @param classStore
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
	public static void testStoreSize(ClassStore classStore, int nbClass, int nbInterface, int nbEnum,
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
