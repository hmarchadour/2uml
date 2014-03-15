package org.obeonetwork.jdt2uml.core.internal.handler.creator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.Jdt2UMLActivator;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;

public class LibrariesCreatorHandler extends AbstractCreatorHandler {

	public LibrariesCreatorHandler(IProgressMonitor monitor) {
		super(monitor);
	}

	@Override
	public JDTCreatorHandler copy() {
		return new LibrariesCreatorHandler(getMonitor());
	}

	@Override
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor) {
		if (!fragmentRoot.isExternal()) {
			super.casePackageFragmentRoot(fragmentRoot, visitor);
		}
	}

	@Override
	public void caseType(IType type, JDTVisitor visitor) {
		caseType(Factory.toWrappedType(type), visitor);
	}

	@Override
	public void caseType(ITypeWrapper type, JDTVisitor visitor) {
		casePre(type.getType(), visitor);
		IPackageFragmentRoot root = type.getPackageFragmentRoot();
		if (root.isExternal()) {
			Component currentComponent;
			Classifier currentClassifier;

			PackageableElement component = getModel().getPackagedElement(root.getElementName());
			if (component != null && component instanceof Component) {
				currentComponent = (Component)component;
			} else {
				currentComponent = UMLFactory.eINSTANCE.createComponent();
				currentComponent.setName(root.getElementName());
				if (getModel() == null) {
					throw new IllegalStateException("model is null");
				}
				getModel().getPackagedElements().add(currentComponent);
			}
			Package currentPackage = type.handlePackage(currentComponent);
			if (type.isAnnotation()) {
				// TODO
				currentClassifier = null;
			} else if (type.isEnum()) {
				currentClassifier = UMLFactory.eINSTANCE.createEnumeration();
			} else if (type.isInterface()) {
				currentClassifier = UMLFactory.eINSTANCE.createInterface();
			} else if (type.isClass()) {
				currentClassifier = UMLFactory.eINSTANCE.createClass();
			} else {
				currentClassifier = null;
				throw new IllegalArgumentException("Type " + type.getType().getElementName()
						+ " is not a class, an interface, an enum or an annotation");
			}
			currentClassifier.setName(type.getElementName());
			if (currentPackage != null) {
				if (currentPackage.getPackagedElement(type.getElementName()) == null) {
					currentPackage.getPackagedElements().add(currentClassifier);
				}
			} else {
				if (currentComponent.getPackagedElement(type.getElementName()) == null) {
					currentComponent.getPackagedElements().add(currentClassifier);
				}
			}
		} else {
			getMonitor().worked(Utils.countAllJavaItems(type.getType()));
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
			Jdt2UMLActivator.logUnexpectedError(e);
		}

		// casePost(type, visitor);
	}

	@Override
	public String getNewModelFileName(IJavaProject javaProject) {
		return Utils.getLibrariesFileName(javaProject);
	}

}
