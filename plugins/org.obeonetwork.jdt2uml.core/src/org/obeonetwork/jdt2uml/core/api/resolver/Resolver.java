package org.obeonetwork.jdt2uml.core.api.resolver;

import org.eclipse.jdt.core.dom.Type;

public interface Resolver {

	ResolverResult resolve(Type rootType);

}
