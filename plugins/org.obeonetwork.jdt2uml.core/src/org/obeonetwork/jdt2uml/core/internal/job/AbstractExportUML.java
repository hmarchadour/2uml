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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.Utils;

public abstract class AbstractExportUML implements ExportUML {

	private final String title;

	private final IJavaProject javaProject;

	private final String fileName;

	private Set<Model> relatedProjectResults;

	private Model currentResult;

	private Set<ExportUML> subExportsToDo;

	public AbstractExportUML(String title, IJavaProject project, String fileName) {
		this.title = title;
		this.javaProject = project;
		this.fileName = fileName;
		relatedProjectResults = new HashSet<Model>();
		subExportsToDo = new HashSet<ExportUML>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCurrentResult(Model currentResult) {
		this.currentResult = currentResult;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model getCurrentResult() {
		return currentResult;
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

	public void recursiveCallOnRelatedProjects(IProgressMonitor monitor) throws InterruptedException {
		for (ExportUML subExportToDo : getSubExportsToDo()) {
			subExportToDo.run(monitor);
			getRelatedProjectResults().add(subExportToDo.getCurrentResult());
		}
	}

	public int countMonitorWork() throws JavaModelException {
		int totalWork = Utils.countAllJavaItems(getJavaProject());
		for (ExportUML subExportToDo : getSubExportsToDo()) {
			totalWork += subExportToDo.countMonitorWork();
		}
		return totalWork;
	}

	public Set<ExportUML> getSubExportsToDo() {
		return subExportsToDo;
	}
}
