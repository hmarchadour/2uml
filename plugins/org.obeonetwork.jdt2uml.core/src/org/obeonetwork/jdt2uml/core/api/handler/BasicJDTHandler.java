package org.obeonetwork.jdt2uml.core.api.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.CoreFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;

public abstract class BasicJDTHandler implements JDTHandler {

	protected final IProgressMonitor monitor;

	private ASTVisitor astVisitor;

	public BasicJDTHandler(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	protected void casePre(IJavaElement javaElement, JModelVisitor visitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	protected void casePost(IJavaElement javaElement, JModelVisitor visitor) {
		if (javaElement instanceof IParent) {
			try {
				IJavaElement[] subJavaElements = ((IParent)javaElement).getChildren();
				if (subJavaElements != null) {
					for (IJavaElement subJavaElement : subJavaElements) {
						visitor.visit(subJavaElement);
					}
				}
			} catch (JavaModelException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}
		if (monitor != null) {
			monitor.subTask(Utils.getPath(javaElement));
			monitor.worked(1);
		}
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JModelVisitor visitor) {
		casePre(javaProject, visitor);
		// nothing
		casePost(javaProject, visitor);
	}

	@Override
	public void caseJavaModel(IJavaModel javaModel, JModelVisitor visitor) {
		casePre(javaModel, visitor);
		// nothing
		casePost(javaModel, visitor);
	}

	@Override
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JModelVisitor visitor) {
		casePre(fragmentRoot, visitor);
		// nothing
		casePost(fragmentRoot, visitor);
	}

	@Override
	public void casePackageFragment(IPackageFragment packageFragment, JModelVisitor visitor) {
		casePre(packageFragment, visitor);
		// nothing
		casePost(packageFragment, visitor);
	}

	@Override
	public void caseType(IType type, JModelVisitor visitor) {
		caseType(CoreFactory.toWrappedType(type), visitor);
	}

	@Override
	public void caseAnnotation(IAnnotation annotation, JModelVisitor visitor) {
		casePre(annotation, visitor);
		// nothing
		casePost(annotation, visitor);
	}

	@Override
	public void caseClassFile(IClassFile classFile, JModelVisitor visitor) {
		casePre(classFile, visitor);
		// nothing
		casePost(classFile, visitor);
	}

	@Override
	public void caseCompilationUnit(ICompilationUnit compilationUnit, JModelVisitor visitor) {
		casePre(compilationUnit, visitor);
		// nothing
		casePost(compilationUnit, visitor);
	}

	@Override
	public void caseField(IField field, JModelVisitor visitor) {
		casePre(field, visitor);
		// nothing
		casePost(field, visitor);
	}

	@Override
	public void caseInitializer(IInitializer initializer, JModelVisitor visitor) {
		casePre(initializer, visitor);
		// nothing
		casePost(initializer, visitor);
	}

	@Override
	public void caseMethod(IMethod method, JModelVisitor visitor) {
		casePre(method, visitor);
		// nothing
		casePost(method, visitor);
	}

	@Override
	public void caseImportContainer(IImportContainer importContainer, JModelVisitor visitor) {
		casePre(importContainer, visitor);
		// nothing
		casePost(importContainer, visitor);
	}

	@Override
	public void casePackageDeclaration(IPackageDeclaration packageDeclaration, JModelVisitor visitor) {
		casePre(packageDeclaration, visitor);
		// nothing
		casePost(packageDeclaration, visitor);
	}

	@Override
	public void caseTypeParameter(ITypeParameter typeParameter, JModelVisitor visitor) {
		casePre(typeParameter, visitor);
		// nothing
		casePost(typeParameter, visitor);
	}

	@Override
	public void caseLocalVariable(ILocalVariable localVariable, JModelVisitor visitor) {
		casePre(localVariable, visitor);
		// nothing
		casePost(localVariable, visitor);
	}

	@Override
	public void caseImportDeclaration(IImportDeclaration importDeclaration, JModelVisitor visitor) {
		casePre(importDeclaration, visitor);
		// nothing
		casePost(importDeclaration, visitor);
	}

	@Override
	public IProgressMonitor getMonitor() {
		return monitor;
	}
}
