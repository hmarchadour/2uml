package org.obeonetwork.jdt2uml.core.api.lazy;

public interface LazyPackage extends LazyContainer {

	LazyContainer getParent();

	org.eclipse.uml2.uml.Package resolve();
}
