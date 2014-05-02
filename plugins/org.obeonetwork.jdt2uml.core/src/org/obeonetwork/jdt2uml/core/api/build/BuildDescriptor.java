package org.obeonetwork.jdt2uml.core.api.build;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public interface BuildDescriptor {

	boolean isDone();

	void setDone();

	Model getModel();

	Visitor getVisitor();

	String getFileName();

	String getTitle();

	IJavaProject getJavaProject();

	URI getSemanticModelURI();

}
