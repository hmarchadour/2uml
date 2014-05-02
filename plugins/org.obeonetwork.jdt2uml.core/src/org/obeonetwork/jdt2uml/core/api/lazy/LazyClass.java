package org.obeonetwork.jdt2uml.core.api.lazy;

import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;

public interface LazyClass extends LazyItem {

	@Override
	public Classifier resolve();

	LazyContainer getContainer();

	LazyHandler getLazyHandler();
}
