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
package org.obeonetwork.jdt2uml.core.internal.job;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.job.JobDescriptor;
import org.obeonetwork.jdt2uml.core.api.visitor.CreatorVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public class ExportModelDescriptor implements JobDescriptor {

	protected final String title;

	protected final IJavaProject javaProject;

	protected final String fileName;

	protected final CreatorVisitor visitor;

	protected final Set<Model> relatedProjectResults;

	protected Model model;

	protected Resource resource;

	protected boolean done;

	public ExportModelDescriptor(String title, IJavaProject project, CreatorVisitor visitor) {
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

	@Override
	public Visitor getVisitor() {
		return visitor;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone() {
		this.done = true;
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
}
