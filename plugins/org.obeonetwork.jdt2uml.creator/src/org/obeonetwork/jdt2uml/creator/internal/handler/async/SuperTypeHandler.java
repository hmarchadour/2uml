package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.Set;

import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;

public final class SuperTypeHandler extends AbstractAsyncHandler {

	protected org.eclipse.jdt.core.dom.Type superType;

	protected Set<LazyHandler> lazyHandlers;

	protected DomTypeResolver typesResolver;

	public SuperTypeHandler(Classifier currentClassifier, org.eclipse.jdt.core.dom.Type superType,
			Set<LazyHandler> lazyHandlers) {
		super(currentClassifier);

		this.superType = superType;
		this.lazyHandlers = lazyHandlers;
		this.typesResolver = new DomTypeResolver(currentClassifier, superType, lazyHandlers);

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
			currentClassifier.createGeneralization(rootClassifier);
		}
	}
}
