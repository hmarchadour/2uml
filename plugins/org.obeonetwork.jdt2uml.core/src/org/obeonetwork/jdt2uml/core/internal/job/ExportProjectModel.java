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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.core.api.job.JobDescriptor;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.core.api.visitor.ProjectVisitor;

import com.google.common.collect.Maps;

public class ExportProjectModel implements UMLJob {

	private ProjectVisitor projectVisitor;

	private JobDescriptor jobDescriptor;

	private Set<LazyHandler> lazyHandlers;

	public ExportProjectModel(Set<LazyHandler> lazyHandlers, JobDescriptor jobDescriptor) {
		this.projectVisitor = (ProjectVisitor)jobDescriptor.getVisitor();
		this.lazyHandlers = lazyHandlers;
		this.jobDescriptor = jobDescriptor;
	}

	@Override
	public JobDescriptor getJobDescriptor() {
		return jobDescriptor;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		if (!jobDescriptor.isDone()) {
			jobDescriptor.setDone();

			Resource resource = new ResourceSetImpl().createResource(jobDescriptor.getSemanticModelURI());
			resource.getContents().add(jobDescriptor.getModel());

			monitor.setTaskName(jobDescriptor.getTitle());
			projectVisitor.visit(lazyHandlers, jobDescriptor.getModel(), jobDescriptor.getJavaProject());
			boolean relaunchHandlers = projectVisitor.relaunchMissingHandlers();
			if (!relaunchHandlers) {
				CoreActivator.log(IStatus.ERROR, "At least of one handler could not be launch.");
			}
			try {
				resource.save(Maps.newHashMap());
			} catch (IOException e) {
				throw new InterruptedException();
			}
		}
		return Status.OK_STATUS;
	}
}
