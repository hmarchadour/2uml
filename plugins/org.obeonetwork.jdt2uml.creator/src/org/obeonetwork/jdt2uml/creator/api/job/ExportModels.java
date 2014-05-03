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
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.build.BuildDescriptor;
import org.obeonetwork.jdt2uml.core.api.build.BuildTodo;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.ProjectVisitor;
import org.obeonetwork.jdt2uml.core.internal.build.BuildDescriptorImpl;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.api.CreatorFactory;

public class ExportModels implements IWorkspaceRunnable {

	private final Set<IJavaProject> projectsToHandle;

	private int totalWork;

	public ExportModels(Set<IJavaProject> projects) {
		this.projectsToHandle = projects;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		totalWork = 0;

		Set<BuildTodo> firstLevelTodos = new HashSet<BuildTodo>();

		for (IJavaProject javaProject : projectsToHandle) {
			try {
				BuildTodo recursiveTODO = recursiveTODO(javaProject, monitor);
				firstLevelTodos.add(recursiveTODO);
			} catch (CoreException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}

		cleanDuplicatesTodos(firstLevelTodos);

		monitor.beginTask("Export UML Models", totalWork);

		for (BuildTodo jobsTODO : firstLevelTodos) {
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
	private void cleanDuplicatesTodos(Set<BuildTodo> firstTODOs) throws CoreException {

		Set<BuildTodo> allTODOs = new LinkedHashSet<BuildTodo>();
		for (BuildTodo firstTODO : firstTODOs) {
			allTODOs.addAll(firstTODO.getAllBuildTodos());
		}

		// get a set of unique ProjectTODOs
		Set<BuildTodo> allUniqueTODOs = new LinkedHashSet<BuildTodo>();
		for (BuildTodo oneOfAllTODOs : allTODOs) {
			boolean isUnique = true;
			for (BuildTodo anUniqueTODO : allUniqueTODOs) {
				if (!anUniqueTODO.equals(oneOfAllTODOs) && anUniqueTODO.isSameTo(oneOfAllTODOs)) {
					isUnique = false;
					break;
				}
			}
			if (isUnique) {
				totalWork += Utils.countJavaItems(oneOfAllTODOs.getProject());
				allUniqueTODOs.add(oneOfAllTODOs);
			}
		}

		// clean
		BuildTodo[] firstTODOsArray = firstTODOs.toArray(new BuildTodo[0]);
		for (int i = 0; i < firstTODOsArray.length; i++) {
			boolean toClean = false;
			BuildTodo firstLevelTodo = firstTODOsArray[i];
			for (BuildTodo anUniqueTODO : allUniqueTODOs) {
				if (!anUniqueTODO.equals(firstLevelTodo) && anUniqueTODO.isSameTo(firstLevelTodo)) {
					toClean = true;
					break;
				}
			}
			if (toClean) {
				firstTODOs.remove(firstLevelTodo);
			} else {
				firstLevelTodo.avoidDuplicatedBuilds(allUniqueTODOs);
			}
		}
	}

	private BuildTodo recursiveTODO(IJavaProject javaProject, IProgressMonitor monitor)
			throws CoreException {
		LibVisitor libVisitor = CreatorFactory.createLibVisitor(monitor);
		ProjectVisitor projectVisitor = CreatorFactory.createProjectVisitor(monitor);

		BuildDescriptor libraryDescriptor = new BuildDescriptorImpl("Export Libraries Model in "
				+ javaProject.getElementName(), javaProject, libVisitor);
		BuildDescriptor projectDescriptor = new BuildDescriptorImpl("Export Project Model in "
				+ javaProject.getElementName(), javaProject, projectVisitor);

		BuildTodo result = CoreFactory.createBuildTodo(javaProject, projectDescriptor, libraryDescriptor);

		IProject[] referencedProjects = javaProject.getProject().getReferencedProjects();
		for (IProject referencedProject : referencedProjects) {
			if (referencedProject.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject referencedJDTProject = JavaCore.create(referencedProject);
				BuildTodo recursiveTODOs = recursiveTODO(referencedJDTProject, monitor);
				result.addSubBuildTodos(recursiveTODOs);
			}
		}
		return result;
	}
}
