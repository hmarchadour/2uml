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
import org.obeonetwork.jdt2uml.core.internal.job.ExportUML;
import org.obeonetwork.jdt2uml.core.internal.job.ExportUMLLibrariesModel;
import org.obeonetwork.jdt2uml.core.internal.job.ExportUMLProjectModel;

public class ExportUMLModels extends Job {

	private final IJavaProject javaProject;

	private final String modelFileName;

	private final String librariesFileName;

	public ExportUMLModels(IJavaProject project, String modelFileName, String librariesFileName) {
		super("Export UML Models");
		this.javaProject = project;
		this.modelFileName = modelFileName;
		this.librariesFileName = librariesFileName;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ExportUML exportUMLLibrariesJob = new ExportUMLLibrariesModel(javaProject, librariesFileName);
			ExportUML exportUMLProjectJob = new ExportUMLProjectModel(javaProject, modelFileName);
			try {
				int totalWork = exportUMLLibrariesJob.countMonitorWork()
						+ exportUMLProjectJob.countMonitorWork();

				monitor.beginTask("Export UML Models", totalWork);

				exportUMLLibrariesJob.run(monitor);
				exportUMLProjectJob.run(monitor);
			} catch (JavaModelException e) {
				return Status.CANCEL_STATUS;
			}

		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}
}
