package org.obeonetwork.jdt2uml.core.internal.lazy;

import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyComponent;

public class LazyComponentImpl extends AbtractLazyItem implements LazyComponent {

	protected Model model;

	protected Component component;

	public LazyComponentImpl(Model model, Component component) {
		super();

		this.model = model;
		this.component = component;
	}

	@Override
	public Component resolve() {
		if (!isResolved()) {
			resolved = true;
			model.getPackagedElements().add(component);
		}
		return component;
	}

}
