package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.ClassifierTemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;

public final class ExternalClassifierHandler extends AbstractAsyncHandler {

	protected IClassFile classFile;

	public ExternalClassifierHandler(IClassFile classFile) {
		super(null);
		this.classFile = classFile;
	}

	public boolean isHandleable() {
		return true;
	}

	@SuppressWarnings("restriction")
	public void handle() {

		if (isHandleable() && !isHandled()) {
			try {
				if (classFile.isInterface()) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else {
					currentClassifier = UMLFactory.eINSTANCE.createClass();
				}
				BinaryType binaryType = (BinaryType)classFile.getType();
				ITypeParameter[] typeParameters = binaryType.getTypeParameters();
				if (typeParameters.length > 0) {
					TemplateSignature templateSignature = currentClassifier.createOwnedTemplateSignature();
					for (ITypeParameter iTypeParameter : typeParameters) {
						Classifier newGenericClass = UMLFactory.eINSTANCE.createClass();
						newGenericClass.setName(iTypeParameter.getElementName());

						ClassifierTemplateParameter classifierTemplateParameter = UMLFactory.eINSTANCE
								.createClassifierTemplateParameter();
						classifierTemplateParameter.setOwnedDefault(newGenericClass);
						classifierTemplateParameter.setParameteredElement(newGenericClass);
						classifierTemplateParameter.setSignature(templateSignature);
					}
				}
				currentClassifier.setName(classFile.getType().getElementName());
			} catch (JavaModelException e) {
				CreatorActivator.logUnexpectedError(e);
			}
			handled = true;
		}
	}
}
