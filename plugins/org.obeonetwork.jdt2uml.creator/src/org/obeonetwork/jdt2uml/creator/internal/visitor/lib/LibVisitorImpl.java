package org.obeonetwork.jdt2uml.creator.internal.visitor.lib;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyComponent;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyPackage;
import org.obeonetwork.jdt2uml.core.api.visitor.AbstractVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.CreatorVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.internal.handler.lazy.LazyExternalClassifierHandler;

public class LibVisitorImpl extends AbstractVisitor implements LibVisitor {

	private Model model;

	private LazyComponent lazyComponent;

	private LazyPackage lazyPackage;

	private Set<LazyClass> lazyClasses;

	public LibVisitorImpl(IProgressMonitor monitor) {
		super(monitor);
		this.model = null;
		this.lazyClasses = new LinkedHashSet<LazyClass>();
	}

	@Override
	public void endCallBack() {
		// Nothing
	}

	public Set<LazyClass> getLazyClasses() {
		return lazyClasses;
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
	public void visit(Model model, IJavaProject javaProject) {
		this.model = model;
		visit(javaProject);
		this.model = null;
	}

	@Override
	public void preVisit(IJavaElement javaElement) {
		if (this.model == null) {
			throw new IllegalStateException("Model cannot be null");
		}
		super.preVisit(javaElement);
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

		if (lazyComponent != null) {
			throw new IllegalStateException(
					"Visit a packageFragmentRoot in another packageFragmentRoot should not appended");
		}
		PackageableElement searchInImportedLibs = model.getImportedMember(packageFragmentRoot
				.getElementName());
		if (searchInImportedLibs == null || !(searchInImportedLibs instanceof Component)) {
			Component currentComponent = UMLFactory.eINSTANCE.createComponent();
			currentComponent.setName(packageFragmentRoot.getElementName());
			lazyComponent = CoreFactory.createLazyComponent(model, currentComponent);
			try {
				for (IJavaElement element : packageFragmentRoot.getChildren()) {
					CoreFactory.toVisitable(element).accept(this);
				}
			} catch (JavaModelException e) {
				CreatorActivator.logUnexpectedError(e);
			}
		}
		lazyComponent = null;

		if (getMonitor() != null) {
			// getMonitor().worked(1);
		}

		postVisit(packageFragmentRoot);
	}

	@Override
	public void visit(IPackageFragment packageFragment) {
		preVisit(packageFragment);

		if (lazyComponent == null) {
			throw new IllegalStateException(
					"Visit a packageFragment without a parent packageFragmentRoot should not appended");
		}

		LazyPackage prevLazyPackage = this.lazyPackage;

		Package createPackage = UMLFactory.eINSTANCE.createPackage();
		createPackage.setName(packageFragment.getElementName());

		if (prevLazyPackage == null) {
			this.lazyPackage = CoreFactory.createLazyPackage(lazyComponent, createPackage);
		} else {
			this.lazyPackage = CoreFactory.createLazyPackage(prevLazyPackage, createPackage);
		}

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
		this.lazyPackage = prevLazyPackage;

		if (getMonitor() != null) {
			// getMonitor().worked(1);
		}

		postVisit(packageFragment);
	}

	@Override
	public void visit(IClassFile classFile) {
		preVisit(classFile);

		if (lazyPackage != null) {
			LazyHandler lazyClassifier = new LazyExternalClassifierHandler(classFile);
			LazyClass lazyClass = CoreFactory.createLazyClass(lazyPackage, lazyClassifier);

			lazyClasses.add(lazyClass);
		} else {
			throw new IllegalStateException("Visit a classFile without package should not appended");
		}

		postVisit(classFile);
	}

}
