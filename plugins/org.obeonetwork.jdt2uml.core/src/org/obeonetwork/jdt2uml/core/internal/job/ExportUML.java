package org.obeonetwork.jdt2uml.core.internal.job;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Model;

public interface ExportUML {

	void recursiveCallOnRelatedProjects(IProgressMonitor monitor) throws InterruptedException;

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	void setCurrentResult(Model currentResult);

	Model getCurrentResult();

	Set<Model> getRelatedProjectResults();

	String getFileName();

	String getTitle();

	IJavaProject getJavaProject();

	int countMonitorWork() throws JavaModelException;

	Set<ExportUML> getSubExportsToDo();

}
