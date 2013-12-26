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
package org.obeonetwork.jar2uml.core.internal.job;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.ManifestElement;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class VisitJars extends Job {

	private final Set<File> jarFiles;

	private ClassLoader classLoader;

	private JarStore jarStore;

	public VisitJars(Set<File> jarFiles) {
		super(VisitJars.class.getSimpleName());
		this.jarFiles = jarFiles;
		jarStore = Factory.createJarStore();
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public JarStore getJarStore() {
		return jarStore;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask("Visit jars", jarFiles.size());
		List<URL> urls = new ArrayList<URL>();
		for (File file : jarFiles) {
			try {
				urls.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());

		for (File file : jarFiles) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			monitor.subTask("Handle " + file.getName());
			try {
				scanClassEntries(file);
			} catch (ZipException e) {
				System.err.println("catched");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("catched");
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("catched");
				e.printStackTrace();
			}
			monitor.worked(1);
		}

		return Status.OK_STATUS;
	}

	private void scanClassEntries(File jarFile) throws ZipException, IOException {

		// Your jar file
		JarFile jar = new JarFile(jarFile);
		// Getting the files into the jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();

		// Iterates into the files in the jar file
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.getName().endsWith(".MF")) {
				try {
					Map<String, String> manifest = ManifestElement.parseBundleManifest(
							jar.getInputStream(zipEntry), null);
					String requiredBundles = (String)manifest.get("Require-Bundle");
					// TODO: handle requiredBundles
				} catch (BundleException e) {
					e.printStackTrace();
				}
			} else if (zipEntry.getName().endsWith(".class")) {
				// Is this a class?

				// Relative path of file into the jar.
				String className = zipEntry.getName();

				// Complete class name
				className = className.replace(".class", "").replace("/", ".");
				if (!className.contains("$")) {
					// Load class definition from JVM
					Class<?> clazz = resolveClass(className);
					if (clazz != null) {
						jarStore.add(jarFile, clazz);
					}
				}
			}
		}
		jar.close();
	}

	private Class<?> resolveClass(String className) {
		try {
			Class<?> loadClass = classLoader.loadClass(className);
			return loadClass;
		} catch (ClassNotFoundException e1) {
			return resolveClassWithBundles(className);
		} catch (NoClassDefFoundError e1) {
			return resolveClassWithBundles(className);
		}
	}

	private Class<?> resolveClassWithBundles(String className) {
		// TODO: enhance the class resolver
		String[] namespaces = Platform.getExtensionRegistry().getNamespaces();

		for (String namespace : namespaces) {
			try {
				Bundle bundle = Platform.getBundle(namespace);
				Class<?> loadClass = bundle.loadClass(className);
				return loadClass;
			} catch (ClassNotFoundException e) {
				// try another bundle
			}
		}
		System.err.println("Could not resolve " + className);
		return null;
	}

}
