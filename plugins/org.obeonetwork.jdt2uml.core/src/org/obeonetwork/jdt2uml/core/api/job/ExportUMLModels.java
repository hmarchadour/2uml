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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.internal.job.ExportUMLImpl;

public class ExportUMLModels extends Job {

	private final IJavaProject javaProject;

	public ExportUMLModels(IJavaProject project) {
		super("Export UML Models");
		this.javaProject = project;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			JDTCreatorHandler projectHandler = Factory.createJDTProjectVisitorHandler(monitor);
			JDTCreatorHandler libsHandler = Factory.createJDTLibrariesVisitorHandler(monitor);

			UMLJob exportLibraries = new ExportUMLImpl("Export Libraries Model in "
					+ javaProject.getElementName(), javaProject, projectHandler);
			UMLJob exportProject = new ExportUMLImpl("Export Project Model in "
					+ javaProject.getElementName(), javaProject, libsHandler);
			try {
				int totalWork = exportLibraries.countMonitorWork() + exportProject.countMonitorWork();

				monitor.beginTask("Export UML Models", totalWork);

				exportLibraries.run(monitor);
				exportProject.run(monitor);
			} catch (JavaModelException e) {
				return Status.CANCEL_STATUS;
			}

		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}
}
