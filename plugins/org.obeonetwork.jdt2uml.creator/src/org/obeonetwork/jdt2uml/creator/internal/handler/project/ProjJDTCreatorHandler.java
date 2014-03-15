package org.obeonetwork.jdt2uml.creator.internal.handler.project;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.creator.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.AJDTCreatorHandler;

public class ProjJDTCreatorHandler extends AJDTCreatorHandler {

	private Component currentComponent;

	private Package currentPackage;

	private Classifier currentClassifier;

	public ProjJDTCreatorHandler(IProgressMonitor monitor) {
		super(monitor);
	}

	@Override
	public JDTCreatorHandler copy() {
		return new ProjJDTCreatorHandler(getMonitor());
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JModelVisitor visitor) {
		if (getModel() == null) {
			throw new IllegalStateException("model is null");
		}
		Component oldCurrentComponent = currentComponent;
		currentComponent = UMLFactory.eINSTANCE.createComponent();
		currentComponent.setName(javaProject.getElementName());
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

		currentPackage = Utils.handlePackage(currentComponent, packageFragment);

		super.casePackageFragment(packageFragment, visitor);

		currentPackage = oldCurrentPackage;
	}

	@Override
	public void caseCompilationUnit(ICompilationUnit compilationUnit, JModelVisitor visitor) {
		super.caseCompilationUnit(compilationUnit, visitor);

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);
		CompilationUnit ast = (CompilationUnit)parser.createAST(new NullProgressMonitor());

		ast.accept(new CreatorASTVisitor(currentPackage));
	}

	@Override
	public void caseType(ITypeWrapper type, JModelVisitor visitor) {

		// Old method
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

	@Override
	public String getNewModelFileName(IJavaProject javaProject) {
		return Utils.getModelFileName(javaProject);
	}

	public Classifier getCurrentClassifier() {
		return currentClassifier;
	}

	public Component getCurrentComponent() {
		return currentComponent;
	}

	public Package getCurrentPackage() {
		return currentPackage;
	}

	@Deprecated
	public void setCurrentClassifier(Classifier currentClassifier) {
		this.currentClassifier = currentClassifier;
	}

	@Deprecated
	public void setCurrentComponent(Component currentComponent) {
		this.currentComponent = currentComponent;
	}

	@Deprecated
	public void setCurrentPackage(Package currentPackage) {
		this.currentPackage = currentPackage;
	}
}
