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
import org.eclipse.jdt.core.JavaModelException;
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

public class JDTProjectConverterJob extends Job {

	private final IJavaProject javaProject;

	private final String fileName;

	private final Resource resource;

	public JDTProjectConverterJob(IJavaProject project, String fileName) {
		super(JDTProjectConverterJob.class.getSimpleName());
		this.javaProject = project;
		this.fileName = fileName;
		final URI semanticModelURI = URI.createPlatformResourceURI('/' + project.getProject().getName() + '/'
				+ fileName + ".uml", true);
		resource = new ResourceSetImpl().createResource(semanticModelURI);
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

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		JDTStore<String> internal;

		JDTStore<String> external;

		JDTVisitorHandler<String> jdtVisitorHandler = Factory.createJDTVisitorHandler(monitor);
		JDTVisitor jdtVisitor = Factory.createJDTVisitor(jdtVisitorHandler);

		try {
			int totalWork = countMonitorWork();
			monitor.beginTask("Visit Classes", totalWork);
			long startTime = System.nanoTime();
			for (IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (!fragmentRoot.isExternal()) {
					jdtVisitor.visit(fragmentRoot);
				}
			}
			long taskTime = System.nanoTime() - startTime;
			System.out.println("time:" + (((double)taskTime) / 1000000000.0));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		internal = jdtVisitorHandler.getInternal();
		external = jdtVisitorHandler.getExternal();
		System.out.println("VisitJDT external " + external.getAllJavaItems().size() + "\n"
				+ "VisitJDT internal " + internal.getAllJavaItems().size());

		return Status.OK_STATUS;
	}
}
