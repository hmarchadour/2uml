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
package org.obeonetwork.jdt2uml.ui.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.obeonetwork.jdt2uml.creator.api.job.ExportModels;

public class JDTProjectHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!selection.isEmpty() && selection instanceof TreeSelection) {
			final Set<IJavaProject> javaProjects = new HashSet<IJavaProject>();
			Iterator iterator = ((TreeSelection)selection).iterator();
			while (iterator.hasNext()) {
				Object object = (Object)iterator.next();
				if (object instanceof IJavaProject) {
					javaProjects.add((IJavaProject)object);
				}
			}
			if (!javaProjects.isEmpty()) {
				Job job = new Job("Export UML Model") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						IWorkspaceRunnable jdt2uml = new ExportModels(javaProjects);
						try {
							ResourcesPlugin.getWorkspace().run(jdt2uml, monitor);
						} catch (CoreException e) {
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				};

				// Start the Job
				job.schedule();
			}
		}
		return null;
	}
}
