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
package org.obeonetwork.jdt2uml.core.api;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

import com.google.common.collect.Maps;

public class JDTProjectConverterJob extends Job {

	private final IJavaProject javaProject;

	private final String fileName;

	public JDTProjectConverterJob(IJavaProject project, String fileName) {
		super(JDTProjectConverterJob.class.getSimpleName());
		this.javaProject = project;
		this.fileName = fileName;
	}

	private int countMonitorWork() throws JavaModelException {
		int totalWork = 0;
		for (IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots()) {
			if (!fragmentRoot.isExternal()) {
				IJavaElement[] children = fragmentRoot.getChildren();
				for (IJavaElement javaElement : children) {
					if (javaElement instanceof IPackageFragment) {
						totalWork++;
					}
				}
			}
		}
		return totalWork;
	}

	protected void recursiveConverterCall() {
		try {
			IProject[] referencedProjects = javaProject.getProject().getReferencedProjects();
			for (IProject referencedProject : referencedProjects) {
				if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
					IJavaProject referencedJDTProject = JavaCore.create(referencedProject);
					Job jdt2uml = new JDTProjectConverterJob(referencedJDTProject, "model");
					jdt2uml.schedule();
					try {
						jdt2uml.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		recursiveConverterCall();

		final URI semanticModelURI = URI.createPlatformResourceURI('/' + javaProject.getElementName() + '/'
				+ fileName + ".uml", true);
		Resource resource = new ResourceSetImpl().createResource(semanticModelURI);

		Model model = UMLFactory.eINSTANCE.createModel();
		resource.getContents().add(model);
		model.setName(fileName);
		Utils.importPrimitiveTypes(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		JDTVisitorHandler<String> jdtVisitorHandler = Factory.createJDTVisitorHandler(model, monitor);
		JDTVisitor jdtVisitor = Factory.createJDTVisitor(jdtVisitorHandler);
		long taskTime = 0;
		try {
			int totalWork = countMonitorWork();
			monitor.beginTask("Visit JDT items", totalWork);
			long startTime = System.nanoTime();
			for (IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (!fragmentRoot.isExternal()) {
					jdtVisitor.visit(fragmentRoot);
				}
			}
			try {
				resource.save(Maps.newHashMap());
			} catch (IOException e) {
				return Status.CANCEL_STATUS;
			}
			taskTime = System.nanoTime() - startTime;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		System.out.println("time:" + (((double)taskTime) / 1000000000.0));
		System.out.println("VisitJDT external " + jdtVisitorHandler.getExternal().getAllJavaItems().size()
				+ "\n" + "VisitJDT internal " + jdtVisitorHandler.getInternal().getAllJavaItems().size());

		return Status.OK_STATUS;
	}
}
