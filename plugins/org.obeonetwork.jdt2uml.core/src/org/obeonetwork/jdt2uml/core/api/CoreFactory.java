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
package org.obeonetwork.jdt2uml.core.api;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.obeonetwork.jdt2uml.core.api.job.ProjectTODO;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitable;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.ProjectTODOImpl;
import org.obeonetwork.jdt2uml.core.internal.wrapper.TypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.wrapper.VisitableImpl;

public final class CoreFactory {

	public static ITypeWrapper toWrappedType(IType type) {
		return new TypeWrapper(type);
	}

	public static Visitable toVisitable(IJavaElement javaElement) {
		return new VisitableImpl(javaElement);
	}

	public static ProjectTODO createJobsTODO(IJavaProject javaProject, UMLJob projectJob, UMLJob libJob) {
		return new ProjectTODOImpl(javaProject, projectJob, libJob);
	}

}
