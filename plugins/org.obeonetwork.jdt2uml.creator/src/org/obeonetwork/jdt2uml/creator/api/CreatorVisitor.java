package org.obeonetwork.jdt2uml.creator.api;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public interface CreatorVisitor extends Visitor {

	String getNewModelFileName(IJavaProject javaProject);

	@Override
	public CreatorVisitor newInstance();

	public void visit(Model model, IJavaProject javaProject);
}
