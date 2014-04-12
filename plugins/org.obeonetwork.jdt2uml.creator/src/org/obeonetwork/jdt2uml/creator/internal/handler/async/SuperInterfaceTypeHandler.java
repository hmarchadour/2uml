package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.Set;

import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;

public final class SuperInterfaceTypeHandler extends AbstractAsyncHandler {

	protected org.eclipse.jdt.core.dom.Type superInterfaceType;

	protected Set<LazyHandler> lazyHandlers;

	protected DomTypeResolver typesResolver;

	public SuperInterfaceTypeHandler(BehavioredClassifier currentClassifier,
			org.eclipse.jdt.core.dom.Type superInterfaceType, Set<LazyHandler> lazyHandlers) {
		super(currentClassifier);

		this.superInterfaceType = superInterfaceType;
		this.lazyHandlers = lazyHandlers;
		this.typesResolver = new DomTypeResolver(currentClassifier, superInterfaceType, lazyHandlers);

	}

	public boolean isHandleable() {
		boolean isResolved = typesResolver.isResolved();
		if (!isResolved) {
			isResolved = typesResolver.tryToResolve();
		}
		return isResolved;
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			Classifier rootClassifier = typesResolver.getRootClassifier();
			if (rootClassifier instanceof Interface) {
				Interface superInterface = (Interface)rootClassifier;
				((BehavioredClassifier)currentClassifier).createInterfaceRealization(
						superInterface.getName(), superInterface);
			}
		}
	}
}
