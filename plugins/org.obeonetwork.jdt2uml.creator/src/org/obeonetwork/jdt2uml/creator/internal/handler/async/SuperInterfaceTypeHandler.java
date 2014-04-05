package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;

public final class SuperInterfaceTypeHandler extends AbstractAsyncHandler {

	protected org.eclipse.jdt.core.dom.Type superInterfaceType;

	protected DomTypeResolver typesResolver;

	public SuperInterfaceTypeHandler(BehavioredClassifier currentClassifier,
			org.eclipse.jdt.core.dom.Type superInterfaceType) {
		super(currentClassifier);

		this.superInterfaceType = superInterfaceType;
		this.typesResolver = new DomTypeResolver(currentClassifier, superInterfaceType);

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
			if (rootClassifier instanceof Interface) {
				Interface superInterface = (Interface)rootClassifier;
				((BehavioredClassifier)currentClassifier).createInterfaceRealization(
						superInterface.getName(), superInterface);
			}
		}
	}
}
