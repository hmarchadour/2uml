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
package org.obeonetwork.jdt2uml.creator.api;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.creator.internal.handler.lib.LibVisitorImpl;
import org.obeonetwork.jdt2uml.creator.internal.handler.project.ProjectVisitorImpl;
import org.obeonetwork.jdt2uml.creator.internal.job.ExportUMLModelsImpl;

public final class CreatorFactory {

	public static UMLJob createExportUML(String title, IJavaProject project, CreatorVisitor visitor) {
		return new ExportUMLModelsImpl(title, project, visitor);
	}

	public static LibVisitor createLibVisitor(IProgressMonitor monitor) {
		return new LibVisitorImpl(monitor);
	}

	public static ProjectVisitor createProjectVisitor(IProgressMonitor monitor) {
		return new ProjectVisitorImpl(monitor);
	}

}
