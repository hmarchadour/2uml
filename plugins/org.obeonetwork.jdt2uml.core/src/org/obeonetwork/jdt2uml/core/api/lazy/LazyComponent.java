package org.obeonetwork.jdt2uml.core.api.lazy;

import org.eclipse.uml2.uml.Component;

public interface LazyComponent extends LazyContainer {

	Component resolve();
}
