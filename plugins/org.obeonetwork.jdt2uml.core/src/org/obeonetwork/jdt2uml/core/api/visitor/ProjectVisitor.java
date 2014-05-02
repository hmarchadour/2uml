package org.obeonetwork.jdt2uml.core.api.visitor;

import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;

public interface ProjectVisitor extends CreatorVisitor {
	void visit(Set<LazyClass> lazyClasses, Model model, IJavaProject javaProject);
}
