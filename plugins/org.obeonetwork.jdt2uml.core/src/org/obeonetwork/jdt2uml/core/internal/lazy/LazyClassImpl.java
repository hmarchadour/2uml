package org.obeonetwork.jdt2uml.core.internal.lazy;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyContainer;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyPackage;

import com.google.common.collect.Maps;

public class LazyClassImpl extends AbtractLazyItem implements LazyClass {

	private LazyPackage lazyPackage;

	private LazyHandler lazyHandler;

	public LazyClassImpl(LazyPackage lazyPackage, LazyHandler lazyClassifier) {
		super();
		this.lazyPackage = lazyPackage;
		this.lazyHandler = lazyClassifier;
	}

	@Override
	public Classifier resolve() {
		Classifier classifier = (Classifier)lazyHandler.resolve();
		if (!isResolved()) {
			resolved = true;
			Package pack = lazyPackage.resolve();
			pack.getPackagedElements().add(classifier);
			Resource eResource = classifier.eResource();
			try {
				eResource.save(Maps.newHashMap());
			} catch (IOException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}

		return classifier;
	}

	@Override
	public LazyContainer getContainer() {
		return lazyPackage;
	}

	@Override
	public LazyHandler getLazyHandler() {
		return lazyHandler;
	}

}
