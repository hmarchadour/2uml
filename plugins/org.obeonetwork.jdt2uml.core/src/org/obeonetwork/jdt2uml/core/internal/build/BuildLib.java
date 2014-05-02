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
package org.obeonetwork.jdt2uml.core.internal.build;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.obeonetwork.jdt2uml.core.api.build.Build;
import org.obeonetwork.jdt2uml.core.api.build.BuildDescriptor;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;

import com.google.common.collect.Maps;

public class BuildLib implements Build {

	private LibVisitor libVisitor;

	private BuildDescriptor jobDescriptor;

	public BuildLib(BuildDescriptor jobDescriptor) {
		this.libVisitor = (LibVisitor)jobDescriptor.getVisitor();
		this.jobDescriptor = jobDescriptor;
	}

	@Override
	public BuildDescriptor getDescriptor() {
		return jobDescriptor;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		if (!jobDescriptor.isDone()) {
			jobDescriptor.setDone();

			Resource resource = new ResourceSetImpl().createResource(jobDescriptor.getSemanticModelURI());
			resource.getContents().add(jobDescriptor.getModel());

			monitor.setTaskName(jobDescriptor.getTitle());
			libVisitor.visit(jobDescriptor.getModel(), jobDescriptor.getJavaProject());
			libVisitor.endCallBack();
			try {
				resource.save(Maps.newHashMap());
			} catch (IOException e) {
				throw new InterruptedException();
			}
		}
		return Status.OK_STATUS;
	}
}
