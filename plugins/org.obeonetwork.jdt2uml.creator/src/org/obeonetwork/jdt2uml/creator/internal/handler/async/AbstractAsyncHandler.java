package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.uml2.uml.Classifier;

public abstract class AbstractAsyncHandler implements AsyncHandler {

	protected Classifier currentClassifier;

	protected boolean handled;

	public AbstractAsyncHandler(Classifier currentClassifier) {
		this.currentClassifier = currentClassifier;
		handled = false;
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
