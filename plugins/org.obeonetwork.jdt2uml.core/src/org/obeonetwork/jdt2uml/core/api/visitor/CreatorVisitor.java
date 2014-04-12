package org.obeonetwork.jdt2uml.core.api.visitor;

import org.eclipse.jdt.core.IJavaProject;

public interface CreatorVisitor extends Visitor {

	String getNewModelFileName(IJavaProject javaProject);

	@Override
	CreatorVisitor newInstance();

	boolean relaunchMissingHandlers();
}
