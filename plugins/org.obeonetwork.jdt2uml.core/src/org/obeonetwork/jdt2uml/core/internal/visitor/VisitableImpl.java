package org.obeonetwork.jdt2uml.core.internal.visitor;

import org.eclipse.jdt.core.IJavaElement;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitable;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitor;

public class VisitableImpl implements Visitable {

	private IJavaElement javaElement;

	public VisitableImpl(IJavaElement javaElement) {
		this.javaElement = javaElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(javaElement);
	}
}
