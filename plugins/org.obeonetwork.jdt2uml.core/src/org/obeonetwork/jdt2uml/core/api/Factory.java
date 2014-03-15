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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.api.handler.JDTHandler;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.handler.creator.LibrariesCreatorHandler;
import org.obeonetwork.jdt2uml.core.internal.handler.creator.ProjectCreatorHandler;
import org.obeonetwork.jdt2uml.core.internal.handler.updator.ProjectUpdatorHandler;
import org.obeonetwork.jdt2uml.core.internal.job.ExportUMLImpl;
import org.obeonetwork.jdt2uml.core.internal.visitor.JDTVisitorImpl;
import org.obeonetwork.jdt2uml.core.internal.wrapper.TypeWrapper;

public final class Factory {

	public static ITypeWrapper toWrappedType(IType type) {
		return new TypeWrapper(type);
	}

	public static UMLJob createExportUML(String title, IJavaProject project, JDTCreatorHandler visitorHandler) {
		return new ExportUMLImpl(title, project, visitorHandler);
	}

	public static JDTCreatorHandler createJDTProjectVisitorHandler(IProgressMonitor monitor) {
		return new ProjectCreatorHandler(monitor);
	}

	public static JDTCreatorHandler createJDTLibrariesVisitorHandler(IProgressMonitor monitor) {
		return new LibrariesCreatorHandler(monitor);
	}

	public static ProjectUpdatorHandler createJDTModelUpdatorHandler(IProgressMonitor monitor, Model model) {
		return new ProjectUpdatorHandler(monitor, model);
	}

	public static JDTVisitor createJDTVisitor(JDTHandler handler) {
		return new JDTVisitorImpl(handler);
	}
}
