/*******************************************************************************
 * Copyright (c) 2013 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.api;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.obeonetwork.jar2uml.core.internal.Jars2UML;

public class ConverterJob extends Job {

	private final IProject project;

	private final Set<File> jarFiles;

	public ConverterJob(IProject project, Set<File> jarFiles) {
		super(ConverterJob.class.getSimpleName());
		this.project = project;
		this.jarFiles = jarFiles;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return new Jars2UML(project, jarFiles).run(monitor);
	}

}
