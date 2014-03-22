package org.obeonetwork.jdt2uml.creator.internal.handler.project;

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
import org.obeonetwork.jdt2uml.core.api.visitor.AbstractVisitor;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.api.CreatorVisitor;
import org.obeonetwork.jdt2uml.creator.api.ProjectVisitor;

public class ProjectVisitorImpl extends AbstractVisitor implements ProjectVisitor {

	private final Model model;

	private Component currentComponent;

	private Package currentPackage;

	public ProjectVisitorImpl(IProgressMonitor monitor) {
		super(monitor);
		this.model = UMLFactory.eINSTANCE.createModel();
	}

	@Override
	public CreatorVisitor newInstance() {
		return new ProjectVisitorImpl(getMonitor());
	}

	@Override
	public String getNewModelFileName(IJavaProject javaProject) {
		return Utils.getModelFileName(javaProject);
	}

	@Override
	public void visit(IJavaProject javaProject) {
		preVisit(javaProject);

		try {
			for (IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
				if (!packageFragmentRoot.isExternal()) {
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
		} catch (JavaModelException e) {
			CreatorActivator.logUnexpectedError(e);
		}
		currentPackage = prevPackage;

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

		ast.accept(new CreatorASTVisitor(currentPackage));

		postVisit(compilationUnit);
	}

	@Override
	public Model getModel() {
		return model;
	}

}
