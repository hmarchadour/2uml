package org.obeonetwork.jdt2uml.creator.internal.handler.lazy;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.uml2.uml.NamedElement;
import org.obeonetwork.jdt2uml.core.api.handler.AsyncHandler;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.ExternalClassifierHandler;

public final class LazyExternalClassifierHandler implements LazyHandler {

	protected IClassFile classFile;

	protected AsyncHandler asyncHandler;

	public LazyExternalClassifierHandler(IClassFile classFile) {
		this.classFile = classFile;
		this.asyncHandler = new ExternalClassifierHandler(classFile);
	}

	public boolean canHandle(String qualifiedName) {
		IType type = classFile.getType();
		boolean result = qualifiedName != null && type.getElementName().length() > 0
				&& qualifiedName.endsWith(type.getElementName());
		if (result) {
			result = qualifiedName.equals(type.getFullyQualifiedName());
		}
		return result;
	}

	@Override
	public NamedElement resolve() {
		handle();
		return asyncHandler.getCurrentClassifier();
	}

	public boolean isHandleable() {
		return asyncHandler.isHandleable();
	}

	public void handle() {
		// if not handled handle
		asyncHandler.handle();
	}
}
