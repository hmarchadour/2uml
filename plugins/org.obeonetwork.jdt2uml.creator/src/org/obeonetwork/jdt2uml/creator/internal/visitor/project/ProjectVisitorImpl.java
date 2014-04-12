package org.obeonetwork.jdt2uml.creator.internal.visitor.project;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.AsyncHandler;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.AbstractVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.CreatorVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.ProjectVisitor;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.ProjectDependencyHandler;

public class ProjectVisitorImpl extends AbstractVisitor implements ProjectVisitor {

	private Model model;

	private Component currentComponent;

	private Package currentPackage;

	private Set<AsyncHandler> handlersToRelaunch;

	private Set<LazyHandler> lazyHandlers;

	public ProjectVisitorImpl(IProgressMonitor monitor) {
		super(monitor);
		this.lazyHandlers = new HashSet<LazyHandler>();
		this.model = null;
		this.handlersToRelaunch = new LinkedHashSet<AsyncHandler>();
	}

	@Override
	public boolean relaunchMissingHandlers() {
		Iterator<AsyncHandler> it = handlersToRelaunch.iterator();
		while (it.hasNext()) {
			AsyncHandler handler = it.next();
			if (handler.isHandled()) {
				throw new IllegalStateException("Should not appended");
			}
			if (handler.isHandleable()) {
				handler.handle();
			}
		}
		return handlersToRelaunch.isEmpty();
	}

	@Override
	public CreatorVisitor newInstance() {
		return new ProjectVisitorImpl(getMonitor());
	}

	@Override
	public String getNewModelFileName(IJavaProject javaProject) {
		return Utils.getModelFileName(javaProject);
	}

	public void visit(Set<LazyHandler> lazyHandlers, Model model, IJavaProject javaProject) {
		this.lazyHandlers = lazyHandlers;
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
		if (currentComponent != null) {
			throw new IllegalStateException(
					"Visit a packageFragmentRoot in another packageFragmentRoot should not appended");
		}
		currentComponent = UMLFactory.eINSTANCE.createComponent();
		currentComponent.setName(javaProject.getElementName());
		model.getPackagedElements().add(currentComponent);

		try {
			for (IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
				if (!packageFragmentRoot.isExternal()
						&& javaProject.equals(packageFragmentRoot.getJavaProject())) {
					CoreFactory.toVisitable(packageFragmentRoot).accept(this);
				} else {
					AsyncHandler handler = new ProjectDependencyHandler(currentComponent, packageFragmentRoot);
					if (handler.isHandleable()) {
						handler.handle();
					}
					if (!handler.isHandled()) {
						handlersToRelaunch.add(handler);
					}
				}
			}
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}

		currentComponent = null;

		postVisit(javaProject);
	}

	@Override
	public void visit(IPackageFragmentRoot packageFragmentRoot) {
		preVisit(packageFragmentRoot);

		try {
			for (IJavaElement element : packageFragmentRoot.getChildren()) {
				CoreFactory.toVisitable(element).accept(this);
			}
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}

		if (getMonitor() != null) {
			getMonitor().worked(1);
		}

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
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}
		currentPackage = prevPackage;

		if (getMonitor() != null) {
			getMonitor().worked(1);
		}

		postVisit(packageFragment);
	}

	@Override
	public void visit(ICompilationUnit compilationUnit) {
		preVisit(compilationUnit);

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);
		CompilationUnit ast = (CompilationUnit)parser.createAST(getMonitor());

		CompilationUnitASTVisitor visitor = new CompilationUnitASTVisitor(currentPackage, lazyHandlers);
		ast.accept(visitor);
		handlersToRelaunch.addAll(visitor.getHandlers());

		postVisit(compilationUnit);
	}
}
