package org.obeonetwork.jdt2uml.core.api.resolver;

import java.util.Map;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.uml2.uml.Classifier;

public interface ResolverResult {

	boolean isResolved();

	void setAsResolved();

	Map<Type, Classifier> getResolverMap();

	Classifier getRootClassifier();

	Type getRootType();

}
