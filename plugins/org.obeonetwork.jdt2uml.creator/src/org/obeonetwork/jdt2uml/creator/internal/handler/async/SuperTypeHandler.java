package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public final class SuperTypeHandler extends AbstractAsyncHandler {

	protected Type superType;

	protected Resolver typesResolver;

	protected ResolverResult latestResult;

	public SuperTypeHandler(Classifier currentClassifier, Type superType, Resolver typesResolver) {
		super(currentClassifier);

		this.superType = superType;
		this.typesResolver = typesResolver;

	}

	public boolean isHandleable() {
		if (latestResult == null || !latestResult.isResolved()) {
			latestResult = typesResolver.resolve(superType);
		}
		return latestResult.isResolved();
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			Classifier rootClassifier = latestResult.getRootClassifier();
			currentClassifier.createGeneralization(rootClassifier);
		}
	}
}
