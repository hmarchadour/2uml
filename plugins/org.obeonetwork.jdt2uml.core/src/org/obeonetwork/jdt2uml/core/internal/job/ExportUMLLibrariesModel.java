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
package org.obeonetwork.jdt2uml.core.internal.job;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

import com.google.common.collect.Maps;

public class ExportUMLLibrariesModel extends AbstractExportUML {

	public ExportUMLLibrariesModel(IJavaProject project, String fileName) {
		super("Export Libraries Model in " + project.getElementName(), project, fileName);
		try {
			IProject[] referencedProjects = getJavaProject().getProject().getReferencedProjects();
			for (IProject referencedProject : referencedProjects) {
				if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
					IJavaProject referencedJDTProject = JavaCore.create(referencedProject);
					ExportUML exportUMLLibrariesModel = new ExportUMLLibrariesModel(referencedJDTProject,
							getFileName());
					getSubExportsToDo().add(exportUMLLibrariesModel);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {

		recursiveCallOnRelatedProjects(monitor);

		final URI semanticModelURI = URI.createPlatformResourceURI('/' + getJavaProject().getElementName()
				+ '/' + getFileName() + ".uml", true);
		Resource resource = new ResourceSetImpl().createResource(semanticModelURI);

		Model libraries = UMLFactory.eINSTANCE.createModel();
		resource.getContents().add(libraries);
		libraries.setName(getFileName());
		Utils.importPrimitiveTypes(libraries, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);
		JDTVisitorHandler jdtVisitorHandler = Factory.createJDTLibrariesVisitorHandler(libraries, monitor);
		JDTVisitor jdtVisitor = Factory.createJDTVisitor(jdtVisitorHandler);
		setCurrentResult(libraries);
		long taskTime = 0;
		long startTime = System.nanoTime();

		monitor.setTaskName(getTitle());
		jdtVisitor.visit(getJavaProject());

		try {
			resource.save(Maps.newHashMap());
		} catch (IOException e) {
			throw new InterruptedException();
		}
		taskTime = System.nanoTime() - startTime;

		System.out.println("Export libraries " + getJavaProject().getElementName() + " time:"
				+ (((double)taskTime) / 1000000000.0));

		return Status.OK_STATUS;
	}

}
