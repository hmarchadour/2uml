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
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.job.ProjectTODO;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.api.CreatorFactory;
import org.obeonetwork.jdt2uml.creator.api.LibVisitor;
import org.obeonetwork.jdt2uml.creator.api.ProjectVisitor;
import org.obeonetwork.jdt2uml.creator.internal.job.ExportModel;

public class ExportModels implements IWorkspaceRunnable {

	private final Set<IJavaProject> projectsToHandle;

	private int totalWork;

	public ExportModels(Set<IJavaProject> projects) {
		this.projectsToHandle = projects;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		totalWork = 0;

		Set<ProjectTODO> firstLevelTodos = new HashSet<ProjectTODO>();

		for (IJavaProject javaProject : projectsToHandle) {
			try {
				ProjectTODO recursiveTODO = recursiveTODO(javaProject, monitor);
				firstLevelTodos.add(recursiveTODO);
			} catch (CoreException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}

		cleanDuplicatesTodos(firstLevelTodos);

		monitor.beginTask("Export UML Models", totalWork);

		for (ProjectTODO jobsTODO : firstLevelTodos) {
			try {
				jobsTODO.run(monitor);
			} catch (InterruptedException e) {
				throw new CoreException(new Status(Status.CANCEL, CreatorActivator.PLUGIN_ID, e.getMessage()));
			}
		}
	}

	/**
	 * avoid recursive works if unnecessary
	 * 
	 * @param firstTODOs
	 * @throws CoreException
	 */
	private void cleanDuplicatesTodos(Set<ProjectTODO> firstTODOs) throws CoreException {

		Set<ProjectTODO> allTODOs = new LinkedHashSet<ProjectTODO>();
		for (ProjectTODO firstTODO : firstTODOs) {
			allTODOs.addAll(firstTODO.getAllJobsTODO());
		}

		// get a set of unique ProjectTODOs
		Set<ProjectTODO> allUniqueTODOs = new LinkedHashSet<ProjectTODO>();
		for (ProjectTODO oneOfAllTODOs : allTODOs) {
			boolean isUnique = true;
			for (ProjectTODO anUniqueTODO : allUniqueTODOs) {
				if (!anUniqueTODO.equals(oneOfAllTODOs) && anUniqueTODO.isSameTo(oneOfAllTODOs)) {
					isUnique = false;
					break;
				}
			}
			if (isUnique) {
				allUniqueTODOs.add(oneOfAllTODOs);
			}
		}

		// clean
		ProjectTODO[] firstTODOsArray = firstTODOs.toArray(new ProjectTODO[0]);
		for (int i = 0; i < firstTODOsArray.length; i++) {
			boolean toClean = false;
			ProjectTODO firstLevelTodo = firstTODOsArray[i];
			for (ProjectTODO anUniqueTODO : allUniqueTODOs) {
				if (!anUniqueTODO.equals(firstLevelTodo) && anUniqueTODO.isSameTo(firstLevelTodo)) {
					toClean = true;
					break;
				}
			}
			if (toClean) {
				firstTODOs.remove(firstLevelTodo);
			} else {
				firstLevelTodo.avoidDuplicatedTODOs(allUniqueTODOs);
			}
		}
	}

	private ProjectTODO recursiveTODO(IJavaProject javaProject, IProgressMonitor monitor)
			throws CoreException {
		LibVisitor libVisitor = CreatorFactory.createLibVisitor(monitor);
		ProjectVisitor projectVisitor = CreatorFactory.createProjectVisitor(monitor);

		UMLJob exportLibrary = new ExportModel("Export Libraries Model in " + javaProject.getElementName(),
				javaProject, libVisitor);

		UMLJob exportModel = new ExportModel("Export Project Model in " + javaProject.getElementName(),
				javaProject, projectVisitor);

		totalWork += exportLibrary.countMonitorWork() + exportModel.countMonitorWork();

		ProjectTODO result = CoreFactory.createJobsTODO(javaProject, exportModel, exportLibrary);

		IProject[] referencedProjects = javaProject.getProject().getReferencedProjects();
		for (IProject referencedProject : referencedProjects) {
			if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject referencedJDTProject = JavaCore.create(referencedProject);
				ProjectTODO recursiveTODOs = recursiveTODO(referencedJDTProject, monitor);
				result.addSubJobsTODO(recursiveTODOs);
			}
		}
		return result;
	}
}
