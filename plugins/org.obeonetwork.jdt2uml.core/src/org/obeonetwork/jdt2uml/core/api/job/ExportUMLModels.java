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
package org.obeonetwork.jdt2uml.core.api.job;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.obeonetwork.jdt2uml.core.Jdt2UMLActivator;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.internal.job.ExportUMLImpl;

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

			JDTCreatorHandler projectHandler = Factory.createJDTProjectVisitorHandler(monitor);
			JDTCreatorHandler libsHandler = Factory.createJDTLibrariesVisitorHandler(monitor);

			UMLJob exportLibrary = new ExportUMLImpl("Export Libraries Model in "
					+ javaProject.getElementName(), javaProject, projectHandler);
			exportLibraries.add(exportLibrary);

			UMLJob exportModel = new ExportUMLImpl("Export Project Model in " + javaProject.getElementName(),
					javaProject, libsHandler);
			exportProjects.add(exportModel);
			totalWork = exportLibrary.countMonitorWork() + exportModel.countMonitorWork();
		}
		monitor.beginTask("Export UML Models", totalWork);
		for (UMLJob umlJob : exportLibraries) {
			try {
				umlJob.run(monitor);
			} catch (InterruptedException e) {
				Jdt2UMLActivator.logUnexpectedError(e);
			}
		}
		for (UMLJob umlJob : exportProjects) {
			try {
				umlJob.run(monitor);
			} catch (InterruptedException e) {
				Jdt2UMLActivator.logUnexpectedError(e);
			}
		}

	}
}
