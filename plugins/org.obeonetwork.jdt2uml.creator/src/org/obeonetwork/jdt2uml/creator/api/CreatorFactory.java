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
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.ProjectVisitor;
import org.obeonetwork.jdt2uml.creator.internal.visitor.lib.LibVisitorImpl;
import org.obeonetwork.jdt2uml.creator.internal.visitor.project.ProjectVisitorImpl;

public final class CreatorFactory {

	public static LibVisitor createLibVisitor(IProgressMonitor monitor) {
		return new LibVisitorImpl(monitor);
	}

	public static ProjectVisitor createProjectVisitor(IProgressMonitor monitor) {
		return new ProjectVisitorImpl(monitor);
	}

}
