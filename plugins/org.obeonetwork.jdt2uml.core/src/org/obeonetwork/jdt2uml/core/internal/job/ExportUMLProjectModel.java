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

public class ExportUMLProjectModel extends AbstractExportUML {

	public ExportUMLProjectModel(IJavaProject project, String fileName) {
		super("Export Project Model in " + project.getElementName(), project, fileName);

		try {
			IProject[] referencedProjects = getJavaProject().getProject().getReferencedProjects();
			for (IProject referencedProject : referencedProjects) {
				if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
					IJavaProject referencedJDTProject = JavaCore.create(referencedProject);
					ExportUML exportUMLProjectJob = new ExportUMLProjectModel(referencedJDTProject,
							getFileName());
					getSubExportsToDo().add(exportUMLProjectJob);
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

		Model model = UMLFactory.eINSTANCE.createModel();
		resource.getContents().add(model);
		model.setName(getFileName());
		Utils.importPrimitiveTypes(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);
		JDTVisitorHandler jdtVisitorHandler = Factory.createJDTProjectVisitorHandler(model, monitor);
		JDTVisitor jdtVisitor = Factory.createJDTVisitor(jdtVisitorHandler);
		setCurrentResult(model);
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

		System.out.println("Export project " + getJavaProject().getElementName() + " time:"
				+ (((double)taskTime) / 1000000000.0));

		return Status.OK_STATUS;
	}
}
