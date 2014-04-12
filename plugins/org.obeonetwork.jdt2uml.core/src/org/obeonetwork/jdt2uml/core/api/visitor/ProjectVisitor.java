package org.obeonetwork.jdt2uml.core.api.visitor;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;

public interface ProjectVisitor extends CreatorVisitor {
	void visit(Set<LazyHandler> lazyHandlers, Model model, IJavaProject javaProject);
}
