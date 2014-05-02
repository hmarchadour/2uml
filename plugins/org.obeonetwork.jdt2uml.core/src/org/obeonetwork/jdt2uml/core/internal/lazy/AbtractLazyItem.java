package org.obeonetwork.jdt2uml.core.internal.lazy;

import org.obeonetwork.jdt2uml.core.api.lazy.LazyItem;

public abstract class AbtractLazyItem implements LazyItem {

	protected boolean resolved;

	public AbtractLazyItem() {
		resolved = false;
	}

	@Override
	public boolean isResolved() {
		return resolved;
	}

}
