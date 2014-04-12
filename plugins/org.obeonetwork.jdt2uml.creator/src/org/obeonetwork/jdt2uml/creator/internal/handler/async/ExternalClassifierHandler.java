package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;

public final class ExternalClassifierHandler extends AbstractAsyncHandler {

	protected Package currentPackage;

	protected IClassFile classFile;

	public ExternalClassifierHandler(Package currentPackage, IClassFile classFile) {
		super(null);
		this.currentPackage = currentPackage;
		this.classFile = classFile;

	}

	public boolean isHandleable() {
		return true;
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			try {
				if (classFile.isInterface()) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else {
					currentClassifier = UMLFactory.eINSTANCE.createClass();
				}
				currentClassifier.setName(classFile.getType().getElementName());
				if (currentPackage != null) {
					if (currentPackage.getPackagedElement(currentClassifier.getName()) == null) {
						currentPackage.getPackagedElements().add(currentClassifier);
					}
				} else {
					throw new IllegalStateException("Visit a classFile without package should not appended");
				}
			} catch (JavaModelException e) {
				CreatorActivator.logUnexpectedError(e);
			}
			handled = true;
		}
	}
}
