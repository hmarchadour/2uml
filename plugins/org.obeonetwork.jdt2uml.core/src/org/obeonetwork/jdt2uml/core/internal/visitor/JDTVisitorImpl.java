package org.obeonetwork.jdt2uml.core.internal.visitor;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
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
		try {
			if (javaElement instanceof IJavaProject) {
				visitJavaProject((IJavaProject)javaElement);
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
			} else if (javaElement instanceof IImportContainer) {
				visitImportContainer((IImportContainer)javaElement);
			} else if (javaElement instanceof IImportDeclaration) {
				visitImportDeclaration((IImportDeclaration)javaElement);
			} else if (javaElement instanceof IPackageDeclaration) {
				visitPackageDeclaration((IPackageDeclaration)javaElement);
			} else {
				System.out.println("Not handled !" + javaElement);
			}

			if (javaElement instanceof IParent) {
				for (IJavaElement subJavaElement : ((IParent)javaElement).getChildren()) {
					visit(subJavaElement);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	protected void visitJavaProject(IJavaProject javaProject) throws JavaModelException {
		handler.caseJavaProject(javaProject, this);
	}

	protected void visitPackageFragmentRoot(IPackageFragmentRoot packageFragmentRoot)
			throws JavaModelException {
		handler.casePackageFragmentRoot(packageFragmentRoot, this);
	}

	protected void visitFragmentRoot(IPackageFragment packageFragment) throws JavaModelException {
		handler.casePackageFragment(packageFragment, this);
	}

	protected void visitImportDeclaration(IImportDeclaration javaElement) {
		// TODO Auto-generated method stub
	}

	protected void visitImportContainer(IImportContainer javaElement) {
		// TODO Auto-generated method stub
	}

	private void visitPackageDeclaration(IPackageDeclaration javaElement) {
		// TODO Auto-generated method stub

	}

	protected void visitClassFile(IClassFile classFile) throws JavaModelException {
		handler.caseClassFile(classFile, this);
	}

	protected void visitCompilationUnit(ICompilationUnit compilationUnit) throws JavaModelException {
		handler.caseCompilationUnit(compilationUnit, this);
	}

	protected void visitMember(IMember member) throws JavaModelException {
		if (member instanceof IField) {
			handler.caseField((IField)member, this);
		} else if (member instanceof IMethod) {
			handler.caseMethod((IMethod)member, this);
		} else if (member instanceof IInitializer) {
			handler.caseInitializer((IInitializer)member, this);
		}
	}
}
