package org.obeonetwork.jdt2uml.core.internal.visitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;

public class ProjectVisitorHandler extends AbstractVisitorHandler {

	private Component currentComponent;

	private Package currentPackage;

	private Classifier currentClassifier;

	public ProjectVisitorHandler(Model model, IProgressMonitor monitor) {
		super(model, monitor);
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JDTVisitor visitor) {
		Component oldCurrentComponent = currentComponent;
		currentComponent = UMLFactory.eINSTANCE.createComponent();
		currentComponent.setName(javaProject.getElementName());
		if (getModel() == null) {
			throw new IllegalStateException("model is null");
		}
		getModel().getPackagedElements().add(currentComponent);

		super.caseJavaProject(javaProject, visitor);

		currentComponent = oldCurrentComponent;
	}

	@Override
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor) {
		if (!fragmentRoot.isExternal()) {
			super.casePackageFragmentRoot(fragmentRoot, visitor);
		}
	}

	@Override
	public void casePackageFragment(IPackageFragment packageFragment, JDTVisitor visitor) {
		Package oldCurrentPackage = currentPackage;

		if (currentComponent == null) {
			throw new IllegalStateException("currentComponent is null");
		}
		currentPackage = Utils.handlePackage(currentComponent, packageFragment);

		super.casePackageFragment(packageFragment, visitor);

		currentPackage = oldCurrentPackage;
	}

	@Override
	public void caseType(IType type, JDTVisitor visitor) {

		if (!Utils.isExternal(type)) {
			casePre(type, visitor);

			Classifier oldCurrentClassifier = currentClassifier;
			if (currentClassifier == null) {
				if (Utils.isAnnotation(type)) {
					// TODO
				} else if (Utils.isEnum(type)) {
					currentClassifier = UMLFactory.eINSTANCE.createEnumeration();
				} else if (Utils.isInterface(type)) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else if (Utils.isClass(type)) {
					currentClassifier = UMLFactory.eINSTANCE.createClass();
				} else {
					throw new IllegalArgumentException("Type " + type.getElementName()
							+ " is not a class, an interface, an enum or an annotation");
				}
				currentClassifier.setName(type.getElementName());
				if (currentPackage != null) {
					if (currentPackage.getPackagedElement(type.getElementName()) == null) {
						currentPackage.getPackagedElements().add(currentClassifier);
					}
				} else if (currentComponent != null) {
					if (currentComponent.getPackagedElement(type.getElementName()) == null) {
						currentComponent.getPackagedElements().add(currentClassifier);
					}
				} else {
					throw new IllegalStateException("currentPackage and currentComponent are null");
				}

			} else {
				// internal classifier
			}
			// handle all types related to the current
			try {
				// handle member types
				for (IType memberType : type.getTypes()) {
					visitor.visit(memberType);
				}
				// handle super types
				String superclassName = type.getSuperclassName();
				if (superclassName != null && !superclassName.isEmpty()) {
					for (IType superType : Utils.getType(type, superclassName)) {
						visitor.visit(superType);
					}
				}
				// handle implemented interfaces
				for (String superInterfaceName : type.getSuperInterfaceNames()) {
					for (IType interfaceType : Utils.getType(type, superInterfaceName)) {
						visitor.visit(interfaceType);
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			currentClassifier = oldCurrentClassifier;
			casePost(type, visitor);
		}
	}
}
