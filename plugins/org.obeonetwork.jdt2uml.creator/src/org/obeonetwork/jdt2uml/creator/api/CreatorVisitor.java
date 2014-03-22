package org.obeonetwork.jdt2uml.creator.api;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public interface CreatorVisitor extends Visitor {

	String getNewModelFileName(IJavaProject javaProject);

	Model getModel();

	@Override
	public CreatorVisitor newInstance();
}
