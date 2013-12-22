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
package org.obeonetwork.jar2uml.core.internal;

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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.api.store.JavaStore;
import org.obeonetwork.jar2uml.core.api.store.ModelStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.google.common.collect.Maps;

public class Jars2UML {

	private final IProject project;

	private final Set<File> jarFiles;

	private ClassLoader classLoader;

	private JarStore internal;

	private JarStore external;

	/**
	 * 
	 */
	public Jars2UML(IProject project, Set<File> jarFiles) {
		this.project = project;
		this.jarFiles = jarFiles;
		this.internal = Factory.createJarStore();
		this.external = Factory.createJarStore();
	}

	public IStatus run(IProgressMonitor monitor) {

		monitor.beginTask("Handle selected jars", jarFiles.size());
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

		exploreRelations();
		createModel("jars2uml.uml");
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
						internal.add(jarFile, clazz);
					}
				}
			}
		}
		jar.close();
	}

	private void exploreRelations() {
		final JavaVisitorHandler<Void> javaRelationHandler = Factory.createJavaRelationHandler(
				internal.toClassStore(), external.toClassStore());
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(javaRelationHandler);

		for (Class<?> javaItem : internal.getAllJavaClasses()) {
			javaVisitor.visit(javaItem);
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

	/**
	 * Create a new uml model with found items in the given project.
	 * 
	 * @param project
	 *            The project where we will save the model
	 * @param modelName
	 *            the model name
	 * @param map
	 *            The map of file/items (Classes, interfaces...)
	 */
	private void createModel(String modelName) {

		final URI semanticModelURI = URI.createPlatformResourceURI('/' + project.getName() + '/' + modelName,
				true);
		Resource res = new ResourceSetImpl().createResource(semanticModelURI);
		final Model model = UMLFactory.eINSTANCE.createModel();
		res.getContents().add(model);
		model.setName(modelName.replace(".uml", ""));
		Utils.importPrimitiveTypes(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		ModelStore modelStore = Factory.createModelStore(model);

		if (!external.getAllJavaItems().isEmpty()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName("external");
			Set<Element> createdElems = createModelElements(createdComponent, external, modelStore);
			System.out.println(createdElems);
		}

		Component createdComponent = UMLFactory.eINSTANCE.createComponent();
		model.getPackagedElements().add(createdComponent);
		createdComponent.setName("internal");
		Set<Element> createdElems = createModelElements(createdComponent, internal, modelStore);

		handleElementRelations(model, modelStore, createdElems);
		try {
			res.save(Maps.newHashMap());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void handleElementRelations(Model model, ModelStore modelStore, Set<Element> internalElems) {

		final JavaVisitorHandler<Void> modelRelationHandler = Factory.createUMLRelationHandler(modelStore);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(modelRelationHandler);

		for (Class<?> javaItem : modelStore.getJava2UMLBinding().keySet()) {
			javaVisitor.visit(javaItem);
		}

	}

	private static Set<Element> createModelElements(Component parent, JavaStore jarStore,
			ModelStore modelStore) {
		final JavaVisitorHandler<Set<Element>> modelInitializer = Factory.createInitializerHandler(parent,
				modelStore);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(modelInitializer);

		for (Class<?> javaItem : jarStore.getAllJavaItems()) {
			javaVisitor.visit(javaItem);
		}

		return modelInitializer.getResult();
	}
}
