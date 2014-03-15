package org.obeonetwork.jdt2uml.updator.internal.handler;

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
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.updator.api.handler.JDTUpdatorHandler;

public class ProjectUpdatorHandler extends AbstractUpdatorHandler {

	private Component currentComponent;

	private Package currentPackage;

	private Classifier currentClassifier;

	public ProjectUpdatorHandler(IProgressMonitor monitor, Model model) {
		super(monitor, model);
	}

	@Override
	public JDTUpdatorHandler copy(Model model) {
		return new ProjectUpdatorHandler(getMonitor(), model);
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JModelVisitor visitor) {
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
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JModelVisitor visitor) {
		if (!fragmentRoot.isExternal()) {
			super.casePackageFragmentRoot(fragmentRoot, visitor);
		}
	}

	@Override
	public void casePackageFragment(IPackageFragment packageFragment, JModelVisitor visitor) {
		Package oldCurrentPackage = currentPackage;

		if (currentComponent == null) {
			throw new IllegalStateException("currentComponent is null");
		}
		currentPackage = Utils.handlePackage(currentComponent, packageFragment);

		super.casePackageFragment(packageFragment, visitor);

		currentPackage = oldCurrentPackage;
	}

	@Override
	public void caseType(IType type, JModelVisitor visitor) {
		caseType(CoreFactory.toWrappedType(type), visitor);
	}

	@Override
	public void caseType(ITypeWrapper type, JModelVisitor visitor) {

		if (!type.isExternal()) {
			casePre(type.getType(), visitor);

			Classifier oldCurrentClassifier = currentClassifier;
			if (currentClassifier == null) {
				if (type.isAnnotation()) {
					// TODO
				} else if (type.isEnum()) {
					currentClassifier = UMLFactory.eINSTANCE.createEnumeration();
				} else if (type.isInterface()) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else if (type.isClass()) {
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
					for (IType superType : type.resolveType(superclassName)) {
						visitor.visit(superType);
					}
				}
				// handle implemented interfaces
				for (String superInterfaceName : type.getSuperInterfaceNames()) {
					for (IType interfaceType : type.resolveType(superInterfaceName)) {
						visitor.visit(interfaceType);
					}
				}
			} catch (JavaModelException e) {
				CoreActivator.logUnexpectedError(e);
			}
			currentClassifier = oldCurrentClassifier;
			casePost(type.getType(), visitor);
		}
	}

	public String getModelFileName(IJavaProject javaProject) {
		return Utils.getModelFileName(javaProject);
	}
}
