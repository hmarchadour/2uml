package org.obeonetwork.jdt2uml.core.api.job;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;

public interface UMLJob {

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	Model getModel();

	Set<Model> getRelatedProjectResults();

	String getFileName();

	String getTitle();

	IJavaProject getJavaProject();

	URI getSemanticModelURI();

}
