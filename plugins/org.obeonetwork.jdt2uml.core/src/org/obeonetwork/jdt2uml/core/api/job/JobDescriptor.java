package org.obeonetwork.jdt2uml.core.api.job;

import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public interface JobDescriptor {

	boolean isDone();

	void setDone();

	Model getModel();

	Visitor getVisitor();

	Set<Model> getRelatedProjectResults();

	String getFileName();

	String getTitle();

	IJavaProject getJavaProject();

	URI getSemanticModelURI();

}
