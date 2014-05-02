package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public final class SuperInterfaceTypeHandler extends AbstractAsyncHandler {

	protected Type superInterfaceType;

	protected Resolver typesResolver;

	protected ResolverResult latestResult;

	public SuperInterfaceTypeHandler(BehavioredClassifier currentClassifier, Type superInterfaceType,
			Resolver typesResolver) {
		super(currentClassifier);

		this.superInterfaceType = superInterfaceType;
		this.typesResolver = typesResolver;

	}

	public boolean isHandleable() {
		if (latestResult == null || !latestResult.isResolved()) {
			latestResult = typesResolver.resolve(superInterfaceType);
		}
		return latestResult.isResolved();
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			Classifier rootClassifier = latestResult.getRootClassifier();
			if (rootClassifier instanceof Interface) {
				Interface superInterface = (Interface)rootClassifier;
				((BehavioredClassifier)currentClassifier).createInterfaceRealization(
						superInterface.getName(), superInterface);
			}
		}
	}
}
