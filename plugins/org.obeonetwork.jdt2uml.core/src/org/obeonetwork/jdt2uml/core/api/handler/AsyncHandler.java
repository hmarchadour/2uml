package org.obeonetwork.jdt2uml.core.api.handler;

import org.eclipse.uml2.uml.Classifier;

public interface AsyncHandler {

	boolean isHandleable();

	void handle();

	boolean isHandled();

	Classifier getCurrentClassifier();

}
