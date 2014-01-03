package org.obeonetwork.jdt2uml.core.internal.visitor;

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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

public class JDTVisitorImpl implements JDTVisitor {

	private final JDTVisitorHandler<?> handler;

	public JDTVisitorImpl(JDTVisitorHandler<?> handler) {
		this.handler = handler;
	}

	@Override
	public void visit(IJavaElement javaElement) {
		if (javaElement != null) {
			try {
				if (javaElement instanceof IJavaProject) {
					visitJavaProject((IJavaProject)javaElement);
				} else if (javaElement instanceof IJavaModel) {
					visitJavaModel((IJavaModel)javaElement);
				} else if (javaElement instanceof IPackageFragmentRoot) {
					visitPackageFragmentRoot((IPackageFragmentRoot)javaElement);
				} else if (javaElement instanceof IPackageFragment) {
					visitFragmentRoot((IPackageFragment)javaElement);
				} else if (javaElement instanceof IClassFile) {
					visitClassFile((IClassFile)javaElement);
				} else if (javaElement instanceof ICompilationUnit) {
					visitCompilationUnit((ICompilationUnit)javaElement);
				} else if (javaElement instanceof IMember) {
					visitMember((IMember)javaElement);
				} else if (javaElement instanceof IAnnotation) {
					visitAnnotation((IAnnotation)javaElement);
				} else if (javaElement instanceof IImportContainer) {
					visitImportContainer((IImportContainer)javaElement);
				} else if (javaElement instanceof IImportDeclaration) {
					visitImportDeclaration((IImportDeclaration)javaElement);
				} else if (javaElement instanceof IPackageDeclaration) {
					visitPackageDeclaration((IPackageDeclaration)javaElement);
				} else if (javaElement instanceof ITypeParameter) {
					visitTypeParameter((ITypeParameter)javaElement);
				} else if (javaElement instanceof ILocalVariable) {
					visitLocalVariable((ILocalVariable)javaElement);
				} else {
					System.out.println("Not handled !" + javaElement);
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	protected void visitJavaProject(IJavaProject javaElement) throws JavaModelException {
		handler.caseJavaProject(javaElement, this);
	}

	private void visitJavaModel(IJavaModel javaElement) {
		handler.caseJavaModel(javaElement, this);
	}

	protected void visitPackageFragmentRoot(IPackageFragmentRoot javaElement) throws JavaModelException {
		handler.casePackageFragmentRoot(javaElement, this);
	}

	protected void visitFragmentRoot(IPackageFragment packageFragment) throws JavaModelException {
		handler.casePackageFragment(packageFragment, this);
	}

	protected void visitImportDeclaration(IImportDeclaration javaElement) {
		handler.caseImportDeclaration(javaElement, this);
	}

	protected void visitImportContainer(IImportContainer javaElement) {
		handler.caseImportContainer(javaElement, this);
	}

	private void visitPackageDeclaration(IPackageDeclaration javaElement) {
		handler.casePackageDeclaration(javaElement, this);
	}

	private void visitAnnotation(IAnnotation javaElement) {
		handler.caseAnnotation(javaElement, this);
	}

	private void visitTypeParameter(ITypeParameter javaElement) {
		handler.caseTypeParameter(javaElement, this);
	}

	private void visitLocalVariable(ILocalVariable javaElement) {
		handler.caseLocalVariable(javaElement, this);
	}

	protected void visitClassFile(IClassFile javaElement) throws JavaModelException {
		handler.caseClassFile(javaElement, this);
	}

	protected void visitCompilationUnit(ICompilationUnit javaElement) throws JavaModelException {
		handler.caseCompilationUnit(javaElement, this);
	}

	protected void visitMember(IMember javaElement) throws JavaModelException {
		if (javaElement instanceof IField) {
			handler.caseField((IField)javaElement, this);
		} else if (javaElement instanceof IMethod) {
			handler.caseMethod((IMethod)javaElement, this);
		} else if (javaElement instanceof IInitializer) {
			handler.caseInitializer((IInitializer)javaElement, this);
		} else if (javaElement instanceof IType) {
			handler.caseType((IType)javaElement, this);
		}
	}
}
