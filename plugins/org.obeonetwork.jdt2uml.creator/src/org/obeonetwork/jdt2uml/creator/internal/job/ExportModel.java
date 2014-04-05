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
package org.obeonetwork.jdt2uml.creator.internal.job;

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
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.creator.api.CreatorVisitor;

import com.google.common.collect.Maps;

public class ExportModel implements UMLJob {

	private final String title;

	private final IJavaProject javaProject;

	private final String fileName;

	private final CreatorVisitor visitor;

	private final Set<Model> relatedProjectResults;

	private Model model;

	private Resource resource;

	private boolean done;

	public ExportModel(String title, IJavaProject project, CreatorVisitor visitor) {
		this.title = title;
		this.javaProject = project;
		this.fileName = visitor.getNewModelFileName(javaProject);
		this.visitor = visitor;

		this.model = UMLFactory.eINSTANCE.createModel();
		this.resource = new ResourceSetImpl().createResource(getSemanticModelURI());
		this.resource.getContents().add(model);
		this.model.setName(getFileName());

		this.relatedProjectResults = new HashSet<Model>();
		this.done = false;
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
	public Model getModel() {
		return model;
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
	public String getFileName() {
		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		if (!done) {
			done = true;
			monitor.setTaskName(getTitle());
			visitor.visit(model, getJavaProject());

			try {
				this.resource.save(Maps.newHashMap());
			} catch (IOException e) {
				throw new InterruptedException();
			}
		}
		return Status.OK_STATUS;
	}
}
