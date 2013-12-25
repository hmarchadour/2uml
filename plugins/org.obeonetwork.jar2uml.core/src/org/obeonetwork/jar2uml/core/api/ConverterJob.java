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
package org.obeonetwork.jar2uml.core.api;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.internal.job.GenUML;
import org.obeonetwork.jar2uml.core.internal.job.VisitClassDependencies;
import org.obeonetwork.jar2uml.core.internal.job.VisitJars;

public class ConverterJob extends Job {

	private final IProject project;

	private final String fileName;

	private final Set<File> jarFiles;

	private final Resource resource;

	public ConverterJob(IProject project, String fileName, Set<File> jarFiles) {
		super(ConverterJob.class.getSimpleName());
		this.project = project;
		this.fileName = fileName;
		this.jarFiles = jarFiles;
		final URI semanticModelURI = URI.createPlatformResourceURI('/' + project.getName() + '/' + fileName
				+ ".uml", true);
		resource = new ResourceSetImpl().createResource(semanticModelURI);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			JarStore jarStore = visitJars(jarFiles, monitor);

			ClassStore findExternalClasses = findExternalClasses(jarStore.toClassStore(), monitor);

			Model model = java2UML(jarStore, findExternalClasses, monitor);
		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	protected JarStore visitJars(Set<File> jarFiles, IProgressMonitor monitor) throws InterruptedException {
		VisitJars visitJars = new VisitJars(jarFiles);
		visitJars.schedule();
		visitJars.join();
		return visitJars.getJarStore();
	}

	protected ClassStore findExternalClasses(ClassStore internalClasses, IProgressMonitor monitor)
			throws InterruptedException {
		VisitClassDependencies visitClassDependencies = new VisitClassDependencies(internalClasses);
		visitClassDependencies.schedule();
		visitClassDependencies.join();

		return visitClassDependencies.getExternalClasses();
	}

	protected Model java2UML(JarStore jarStore, ClassStore externalClasses, IProgressMonitor monitor)
			throws InterruptedException {
		GenUML genUML = new GenUML(jarStore, externalClasses, project.getName(), resource);
		genUML.schedule();
		genUML.join();

		return genUML.getModel();
	}
}
