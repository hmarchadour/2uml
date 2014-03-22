package org.obeonetwork.jdt2uml.creator.internal.handler.lib;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.AbstractVisitor;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.api.CreatorVisitor;
import org.obeonetwork.jdt2uml.creator.api.LibVisitor;

public class LibVisitorImpl extends AbstractVisitor implements LibVisitor {

	private final Model model;

	private Component currentComponent;

	private Package currentPackage;

	public LibVisitorImpl(IProgressMonitor monitor) {
		super(monitor);
		this.model = UMLFactory.eINSTANCE.createModel();
	}

	@Override
	public CreatorVisitor newInstance() {
		return new LibVisitorImpl(getMonitor());
	}

	@Override
	public String getNewModelFileName(IJavaProject javaProject) {
		return Utils.getLibrariesFileName(javaProject);
	}

	@Override
	public void visit(IJavaProject javaProject) {
		preVisit(javaProject);

		try {
			for (IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
				if (packageFragmentRoot.isExternal()) {
					CoreFactory.toVisitable(packageFragmentRoot).accept(this);
				}
			}
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}

		postVisit(javaProject);
	}

	@Override
	public void visit(IPackageFragmentRoot packageFragmentRoot) {
		preVisit(packageFragmentRoot);

		if (currentComponent != null) {
			throw new IllegalStateException(
					"Visit a packageFragmentRoot in another packageFragmentRoot should not appended");
		}
		currentComponent = UMLFactory.eINSTANCE.createComponent();
		currentComponent.setName(packageFragmentRoot.getElementName());
		model.getPackagedElements().add(currentComponent);

		try {
			for (IJavaElement element : packageFragmentRoot.getChildren()) {
				CoreFactory.toVisitable(element).accept(this);
			}
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}
		currentComponent = null;

		postVisit(packageFragmentRoot);
	}

	@Override
	public void visit(IPackageFragment packageFragment) {
		preVisit(packageFragment);

		if (currentComponent == null) {
			throw new IllegalStateException(
					"Visit a packageFragment without a parent packageFragmentRoot should not appended");
		}

		Package prevPackage = currentPackage;
		currentPackage = Utils.handlePackage(currentComponent, packageFragment);
		try {
			for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
				CoreFactory.toVisitable(compilationUnit).accept(this);
			}
			for (IClassFile classFile : packageFragment.getClassFiles()) {
				CoreFactory.toVisitable(classFile).accept(this);
			}
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}
		currentPackage = prevPackage;

		postVisit(packageFragment);
	}

	@Override
	public void visit(IClassFile classFile) {
		preVisit(classFile);

		Classifier currentClassifier;
		try {
			if (classFile.isInterface()) {
				currentClassifier = UMLFactory.eINSTANCE.createInterface();
			} else {
				currentClassifier = UMLFactory.eINSTANCE.createClass();
			}
			currentClassifier.setName(classFile.getElementName());
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

		postVisit(classFile);
	}

	@Override
	public Model getModel() {
		return model;
	}

}
