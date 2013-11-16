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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class Jars2UML extends Job {

	public final static String EXTERNAL_KEY = "externals";

	public final static String INTERFACE_KEY = "interfaces";

	public final static String CLASS_KEY = "classes";

	public final static String ANNOTATION_KEY = "annotations";

	public final static String ENUM_KEY = "enums";

	private final IProject project;

	private final List<File> files;

	private ClassLoader classLoader;

	private Map<File, Set<Class<?>>> filesBinding;

	private Map<String, Set<Class<?>>> classStore;
	private Set<Class<?>> interfaces;
	private Set<Class<?>> clazzes;
	private Set<Class<?>> enums;
	private Set<Class<?>> annotations;

	private Map<String, Set<Class<?>>> externalStore;
	private Set<Class<?>> extInterfaces;
	private Set<Class<?>> extClazzes;
	private Set<Class<?>> extEnums;
	private Set<Class<?>> extAnnotations;

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

		filesBinding = new HashMap<File, Set<Class<?>>>();

		interfaces = new HashSet<Class<?>>();
		clazzes = new HashSet<Class<?>>();
		enums = new HashSet<Class<?>>();
		annotations = new HashSet<Class<?>>();

		classStore = new HashMap<String, Set<Class<?>>>();
		classStore.put(INTERFACE_KEY, interfaces);
		classStore.put(CLASS_KEY, clazzes);
		classStore.put(ANNOTATION_KEY, annotations);
		classStore.put(ENUM_KEY, enums);

		extInterfaces = new HashSet<Class<?>>();
		extClazzes = new HashSet<Class<?>>();
		extEnums = new HashSet<Class<?>>();
		extAnnotations = new HashSet<Class<?>>();

		externalStore = new HashMap<String, Set<Class<?>>>();
		externalStore.put(INTERFACE_KEY, extInterfaces);
		externalStore.put(CLASS_KEY, extClazzes);
		externalStore.put(ANNOTATION_KEY, extAnnotations);
		externalStore.put(ENUM_KEY, extEnums);

		monitor.beginTask("Handle selected jars", files.size());
		List<URL> urls = new ArrayList<URL>();
		for (File file : files) {
			try {
				urls.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());

		for (File file : files) {
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

		for (File file : files) {
			exploreRelations(file);
		}

		if (filesBinding.keySet().size() > 1) {
			UMLModelHelper.createModel(project, "jars2uml.uml", filesBinding, classStore, externalStore);
		} else {
			if (filesBinding.keySet().size() == 1) {
				File file = filesBinding.keySet().iterator().next();
				UMLModelHelper.createModel(project, file.getName().replace(".jar", ".uml"), filesBinding, classStore,
						externalStore);
			}
		}
		return Status.OK_STATUS;
	}

	private void addInMap(Class<?> clazz, Map<String, Set<Class<?>>> classes) {
		if (clazz != null && !clazz.isPrimitive()) {
			if (clazz.isArray()) {
				addInMap(clazz.getComponentType(), classes);
			} else {
				if (clazz.isInterface()) {
					classes.get(INTERFACE_KEY).add(clazz);
				} else if (clazz.isAnnotation()) {
					classes.get(ANNOTATION_KEY).add(clazz);
				} else if (clazz.isEnum()) {
					classes.get(ENUM_KEY).add(clazz);
				} else {
					classes.get(CLASS_KEY).add(clazz);
				}
			}
		}
	}

	private void scanClassEntries(File jarFile) throws ZipException, IOException {

		// Your jar file
		JarFile jar = new JarFile(jarFile);
		// Getting the files into the jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();

		Set<Class<?>> clazzes = new HashSet<Class<?>>();

		// Iterates into the files in the jar file
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.getName().endsWith(".MF")) {
				try {
					Map manifest = ManifestElement.parseBundleManifest(jar.getInputStream(zipEntry), null);
					String requiredBundles = (String) manifest.get("Require-Bundle");
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
					Class<?> clazz = resolveClassWithBundles(className);
					if (clazz != null) {
						clazzes.add(clazz);
						addInMap(clazz, classStore);
					}
				}
			}
		}
		filesBinding.put(jarFile, clazzes);
		jar.close();
	}

	private void exploreRelations(File jarFile) {

		for (Class<?> clazz : filesBinding.get(jarFile)) {

			// Supertype
			Class<?> superclass = findSuperclass(clazz);
			if (superclass != null && isExternal(clazz)) {
				addInMap(superclass, externalStore);
			}
			for (Class<?> implementedInterface : findInterfaces(clazz)) {
				if (isExternal(implementedInterface)) {
					addInMap(implementedInterface, externalStore);
				}
			}

			// Attributes
			for (Field field : findAttributes(clazz)) {
				if (isExternal(field.getType())) {
					addInMap(field.getType(), externalStore);
				}
			}
			// Methods
			for (Method method : findMethodes(clazz)) {
				Class<?> returnType = findMethodReturn(method);
				if (returnType != null && isExternal(returnType)) {
					addInMap(returnType, externalStore);
				}
				for (Class<?> parameterType : findMethodeParams(method)) {
					if (isExternal(parameterType)) {
						addInMap(parameterType, externalStore);
					}
				}
			}
		}
	}

	private Class<?> findSuperclass(Class<?> clazz) {
		try {
			return clazz.getSuperclass();
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findSuperclass " + e.getMessage());
			return null;
		}
	}

	private List<Class<?>> findInterfaces(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getInterfaces());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findInterfaces " + e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}

	private List<Field> findAttributes(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getFields());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findAttributes " + e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}

	private List<Method> findMethodes(Class<?> clazz) {
		try {
			return Arrays.asList(clazz.getMethods());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodes " + e.getMessage());
			return Collections.EMPTY_LIST;
		}
	}

	private Class<?> findMethodReturn(Method method) {
		try {
			return method.getReturnType();
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodReturn " + e.getMessage());
			return null;
		}
	}

	private List<Class<?>> findMethodeParams(Method method) {
		try {
			return Arrays.asList(method.getParameterTypes());
		} catch (NoClassDefFoundError e) {
			System.err.println("NoClassDefFoundError:findMethodeParams " + e.getMessage());
			return Collections.EMPTY_LIST;
		}
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

	private boolean isExternal(Class<?> clazz) {
		return !(interfaces.contains(clazz) || clazzes.contains(clazz) || enums.contains(clazz) || annotations
				.contains(clazz));
	}

	private Class<?> resolveClassWithBundles(String className) {
		// TODO: enhance the class resolver
		Bundle[] bundles = InternalPlatform.getDefault().getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			try {
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
