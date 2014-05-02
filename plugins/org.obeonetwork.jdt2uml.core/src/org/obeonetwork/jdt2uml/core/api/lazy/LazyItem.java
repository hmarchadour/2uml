package org.obeonetwork.jdt2uml.core.api.lazy;

import org.eclipse.uml2.uml.Element;

public interface LazyItem {

	boolean isResolved();

	Element resolve();
}
