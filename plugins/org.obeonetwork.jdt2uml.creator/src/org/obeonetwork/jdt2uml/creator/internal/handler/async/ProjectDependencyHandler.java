package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.uml2.uml.Component;
import org.obeonetwork.jdt2uml.core.api.Utils;

public final class ProjectDependencyHandler extends AbstractAsyncHandler {

	protected String fieldName;

	protected Component currentComponent;

	protected Component depComponent;

	protected IPackageFragmentRoot packageFragmentRoot;

	public ProjectDependencyHandler(Component currentComponent, IPackageFragmentRoot packageFragmentRoot) {
		super(currentComponent);
		this.currentComponent = currentComponent;
		this.packageFragmentRoot = packageFragmentRoot;

	}

	public boolean isHandleable() {
		depComponent = Utils.searchComponentInModels(currentComponent, packageFragmentRoot.getElementName());
		return depComponent != null;
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			currentComponent.createDependency(depComponent);
			handled = true;
		}
	}
}
