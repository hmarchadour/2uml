package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;

public final class SuperTypeHandler extends AbstractAsyncHandler {

	protected org.eclipse.jdt.core.dom.Type superType;

	protected DomTypeResolver typesResolver;

	public SuperTypeHandler(Classifier currentClassifier, org.eclipse.jdt.core.dom.Type superType) {
		super(currentClassifier);

		this.superType = superType;
		this.typesResolver = new DomTypeResolver(currentClassifier, superType);

	}

	public boolean isHandleable() {
		boolean isResolved = typesResolver.isResolved();
		if (!isResolved) {
			isResolved = typesResolver.tryToResolve();
		}
		return isResolved;
	}

	public void handle() {

		if (isHandleable()) {
			Classifier rootClassifier = typesResolver.getRootClassifier();
			currentClassifier.createGeneralization(rootClassifier);
		}
	}
}
