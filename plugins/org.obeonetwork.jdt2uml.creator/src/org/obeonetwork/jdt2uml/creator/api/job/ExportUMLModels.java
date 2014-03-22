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
package org.obeonetwork.jdt2uml.creator.api.job;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.creator.api.CreatorFactory;
import org.obeonetwork.jdt2uml.creator.api.LibVisitor;
import org.obeonetwork.jdt2uml.creator.api.ProjectVisitor;
import org.obeonetwork.jdt2uml.creator.internal.job.ExportUMLModelsImpl;

public class ExportUMLModels implements IWorkspaceRunnable {

	private final Set<IJavaProject> javaProjects;

	public ExportUMLModels(Set<IJavaProject> projects) {
		this.javaProjects = projects;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		Set<UMLJob> exportLibraries = new HashSet<UMLJob>();
		Set<UMLJob> exportProjects = new HashSet<UMLJob>();
		int totalWork = 0;
		for (IJavaProject javaProject : javaProjects) {

			LibVisitor libVisitor = CreatorFactory.createLibVisitor(monitor);
			ProjectVisitor projectVisitor = CreatorFactory.createProjectVisitor(monitor);

			UMLJob exportLibrary = new ExportUMLModelsImpl("Export Libraries Model in "
					+ javaProject.getElementName(), javaProject, libVisitor);
			exportLibraries.add(exportLibrary);

			UMLJob exportModel = new ExportUMLModelsImpl("Export Project Model in "
					+ javaProject.getElementName(), javaProject, projectVisitor);
			exportProjects.add(exportModel);
			totalWork = exportLibrary.countMonitorWork() + exportModel.countMonitorWork();
		}
		monitor.beginTask("Export UML Models", totalWork);
		for (UMLJob umlJob : exportLibraries) {
			try {
				umlJob.run(monitor);
			} catch (InterruptedException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}
		for (UMLJob umlJob : exportProjects) {
			try {
				umlJob.run(monitor);
			} catch (InterruptedException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}

	}
}
