package org.obeonetwork.jdt2uml.core.api.handler;

import org.eclipse.uml2.uml.NamedElement;

public interface LazyHandler {

	boolean canHandle(String key);

	NamedElement resolve();

}
