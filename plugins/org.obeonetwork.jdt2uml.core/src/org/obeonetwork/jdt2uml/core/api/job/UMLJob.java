package org.obeonetwork.jdt2uml.core.api.job;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;

public interface UMLJob {

	void recursiveCallOnRelatedProjects(IProgressMonitor monitor) throws InterruptedException;

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	Model getCurrentResult();

	Set<Model> getRelatedProjectResults();

	String getFileName();

	String getTitle();

	IJavaProject getJavaProject();

	int countMonitorWork() throws JavaModelException;

	Set<UMLJob> getSubExportsToDo();

	URI getSemanticModelURI();

}
