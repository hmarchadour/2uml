package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.handler.AsyncHandler;

public abstract class AbstractAsyncHandler implements AsyncHandler {

	protected Classifier currentClassifier;

	protected boolean handled;

	public AbstractAsyncHandler(Classifier currentClassifier) {
		this.currentClassifier = currentClassifier;
		handled = false;
	}

	public Classifier getCurrentClassifier() {
		return currentClassifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean isHandleable();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void handle();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return handled;
	}

}
