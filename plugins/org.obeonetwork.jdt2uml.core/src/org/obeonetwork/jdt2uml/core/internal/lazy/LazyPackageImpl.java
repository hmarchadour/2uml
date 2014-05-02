package org.obeonetwork.jdt2uml.core.internal.lazy;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyContainer;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyPackage;

public class LazyPackageImpl extends AbtractLazyItem implements LazyPackage {

	protected LazyContainer lazyContainer;

	protected Package pack;

	protected Collection<LazyClass> lazyClasses;

	public LazyPackageImpl(LazyContainer lazyContainer, Package pack) {
		super();
		this.lazyContainer = lazyContainer;
		this.pack = pack;
		this.lazyClasses = new LinkedHashSet<LazyClass>();
	}

	@Override
	public LazyContainer getParent() {
		return lazyContainer;
	}

	@Override
	public Package resolve() {
		if (!isResolved()) {
			resolved = true;
			PackageableElement container = (PackageableElement)lazyContainer.resolve();
			if (container instanceof Component) {
				Component component = (Component)container;
				component.getPackagedElements().add(pack);
			} else {
				Package packParent = (Package)container;
				packParent.getPackagedElements().add(pack);
			}
		}
		return pack;
	}

}
