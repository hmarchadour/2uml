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
package org.obeonetwork.jdt2uml.updator.internal.job;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.updator.api.handler.JDTUpdatorHandler;

import com.google.common.collect.Maps;

public class UpdateUMLImpl implements UMLJob {

	private final String title;

	private final IJavaProject javaProject;

	private final String fileName;

	private final JDTUpdatorHandler updatorHandler;

	private final Set<Model> relatedProjectResults;

	private final Set<UMLJob> subExportsToDo;

	public UpdateUMLImpl(String title, IJavaProject project, JDTUpdatorHandler visitorHandler) {
		this.title = title;
		this.javaProject = project;
		this.fileName = visitorHandler.getModelFileName(project);
		this.updatorHandler = visitorHandler;
		relatedProjectResults = new HashSet<Model>();
		subExportsToDo = new HashSet<UMLJob>();

	}

	public String getTitle() {
		return title;
	}

	public URI getSemanticModelURI() {

		final URI semanticModelURI = URI.createPlatformResourceURI(
				Utils.createModelPath(getJavaProject(), getFileName()), true);
		return semanticModelURI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model getCurrentResult() {
		return updatorHandler.getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Model> getRelatedProjectResults() {
		return relatedProjectResults;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void recursiveCallOnRelatedProjects(IProgressMonitor monitor) throws InterruptedException {
		for (UMLJob subExportToDo : getSubExportsToDo()) {
			subExportToDo.run(monitor);
			getRelatedProjectResults().add(subExportToDo.getCurrentResult());
		}
	}

	public int countMonitorWork() throws JavaModelException {
		int totalWork = Utils.countAllJavaItems(getJavaProject());
		for (UMLJob subExportToDo : getSubExportsToDo()) {
			totalWork += subExportToDo.countMonitorWork();
		}
		return totalWork;
	}

	public Set<UMLJob> getSubExportsToDo() {
		return subExportsToDo;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {

		recursiveCallOnRelatedProjects(monitor);

		Resource resource = new ResourceSetImpl().createResource(getSemanticModelURI());
		Model model = updatorHandler.getModel();
		resource.getContents().add(model);
		model.setName(getFileName());
		Utils.importUMLResource(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		for (UMLJob subExportToDo : subExportsToDo) {
			Utils.importUMLResource(model, subExportToDo.getSemanticModelURI());
		}
		monitor.setTaskName(getTitle());
		CoreFactory.createJDTVisitor(updatorHandler).visit(getJavaProject());

		try {
			resource.save(Maps.newHashMap());
		} catch (IOException e) {
			throw new InterruptedException();
		}
		return Status.OK_STATUS;
	}

	@Override
	public String getFileName() {
		return fileName;
	}
}
