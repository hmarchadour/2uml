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
package org.obeonetwork.jar2uml.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class Jars2UML extends Job {

	public final static String EXTERNAL_KEY = "externals";
	public final static String INTERFACE_KEY = "interfaces";
	public final static String CLASS_KEY = "classes";
	public final static String ANNOTATION_KEY = "annotations";
	public final static String ENUM_KEY = "enums";

	private final IProject project;
	private final List<File> files;

	/**
	 * 
	 */
	public Jars2UML(IProject project, List<File> files) {
		super("Jar2UML");
		this.project = project;
		this.files = files;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Map<File, Map<String, List<Class<?>>>> result = new HashMap<File, Map<String, List<Class<?>>>>();
		monitor.beginTask("Handle selected jars", files.size());
		for (File file : files) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.subTask("Handle " + file.getName());
			try {
				Map<String, List<Class<?>>> foundItems = loadAndScanJar(file);
				result.put(file, foundItems);
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			monitor.worked(1);
		}
		if (result.keySet().size() > 1) {
			UMLModelHelper.createModel(project, "jars2uml.uml", result);
		} else {
			if (result.keySet().size() == 1) {
				File file = result.keySet().iterator().next();
				UMLModelHelper.createModel(project, file.getName().replace(".jar", ".uml"), result);
			}
		}
		return Status.OK_STATUS;
	}

	private Map<String, List<Class<?>>> loadAndScanJar(File jarFile) throws ZipException, IOException {

		URL[] urls = new URL[] { jarFile.toURI().toURL() };
		URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());

		Map<String, List<Class<?>>> classes = new HashMap<String, List<Class<?>>>();

		List<Class<?>> externals = new ArrayList<Class<?>>();
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		List<Class<?>> enums = new ArrayList<Class<?>>();
		List<Class<?>> annotations = new ArrayList<Class<?>>();

		classes.put(EXTERNAL_KEY, externals);
		classes.put(INTERFACE_KEY, interfaces);
		classes.put(CLASS_KEY, clazzes);
		classes.put(ANNOTATION_KEY, annotations);
		classes.put(ENUM_KEY, enums);

		// Your jar file
		JarFile jar = new JarFile(jarFile);
		// Getting the files into the jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();

		// Iterates into the files in the jar file
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			// Is this a class?
			if (zipEntry.getName().endsWith(".class")) {

				// Relative path of file into the jar.
				String className = zipEntry.getName();

				// Complete class name
				className = className.replace(".class", "").replace("/", ".");
				// Load class definition from JVM
				try {
					Class<?> clazz = child.loadClass(className);

					try {
						// Verify the type of the "class"
						if (clazz.isInterface()) {
							interfaces.add(clazz);
						} else if (clazz.isAnnotation()) {
							annotations.add(clazz);
						} else if (clazz.isEnum()) {
							enums.add(clazz);
						} else {
							clazzes.add(clazz);
						}
					} catch (ClassCastException e) {

					}
				} catch (ClassNotFoundException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		jar.close();
		return classes;
	}
}
