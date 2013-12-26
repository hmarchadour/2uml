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
package org.obeonetwork.jar2uml.core.internal.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class VisitClassDependencies extends Job {

	private ClassStore classesToVisit;

	private ClassStore externalClasses;

	public VisitClassDependencies(ClassStore classesToVisit) {
		super(VisitClassDependencies.class.getSimpleName());
		this.classesToVisit = classesToVisit;
		this.externalClasses = Factory.createClassStore();
	}

	public ClassStore getExternalClasses() {
		return externalClasses;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask("Visit classes", classesToVisit.getAllJavaClasses().size());

		final JavaVisitorHandler<Void> javaRelationHandler = Factory.createJavaRelationHandler(
				classesToVisit, externalClasses);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(javaRelationHandler);

		for (Class<?> javaItem : classesToVisit.getAllJavaClasses()) {
			javaVisitor.visit(javaItem);
			monitor.worked(1);
		}
		return Status.OK_STATUS;
	}

}
