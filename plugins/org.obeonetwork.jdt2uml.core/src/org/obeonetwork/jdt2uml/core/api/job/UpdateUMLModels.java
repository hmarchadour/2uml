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
package org.obeonetwork.jdt2uml.core.api.job;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.JDTUpdatorHandler;
import org.obeonetwork.jdt2uml.core.internal.job.UpdateUMLImpl;

public class UpdateUMLModels extends Job {

	private final IJavaElement element;

	public UpdateUMLModels(IJavaElement element) {
		super("Update UML Models");
		this.element = element;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			IJavaProject javaProject = element.getJavaProject();
			URI resourceURI = URI.createPlatformResourceURI(
					Utils.createModelPath(javaProject, Utils.getModelFileName(javaProject)), true);
			Resource resource = new ResourceSetImpl().getResource(resourceURI, true);
			if (resource != null) {
				List<EObject> contents = resource.getContents();
				for (EObject eObject : contents) {
					if (eObject instanceof Model) {
						Model model = (Model)eObject;
						JDTUpdatorHandler projectHandler = Factory.createJDTModelUpdatorHandler(monitor,
								model);

						UMLJob updateProject = new UpdateUMLImpl("Update Libraries Model in "
								+ element.getJavaProject().getElementName(), element.getJavaProject(),
								projectHandler);
						try {
							int totalWork = updateProject.countMonitorWork();

							monitor.beginTask("Export UML Models", totalWork);

							updateProject.run(monitor);
						} catch (JavaModelException e) {
							return Status.CANCEL_STATUS;
						}
					}
				}
			}

		} catch (InterruptedException e) {
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}
}
