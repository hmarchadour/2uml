package org.obeonetwork.jdt2uml.core.api.visitor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.Utils;

public abstract class AbstractVisitor implements Visitor {

	public final static int RECURSIVE_DEFAULT_LIMIT = 100;

	private final int recursiveLimit;

	private final Set<IJavaElement> visited;

	private final Set<IJavaElement> stack;

	private final IProgressMonitor monitor;

	public AbstractVisitor(IProgressMonitor monitor) {
		this.recursiveLimit = RECURSIVE_DEFAULT_LIMIT;
		visited = new HashSet<IJavaElement>();
		stack = new HashSet<IJavaElement>();
		this.monitor = monitor;
	}

	@Override
	public IProgressMonitor getMonitor() {
		return monitor;
	}

	@Override
	public void preVisit(IJavaElement javaElement) {
		if (stack.size() > recursiveLimit) {
			StringBuilder error = new StringBuilder("Max recursive level");
			for (IJavaElement item : stack) {
				error.append("\n" + item.getElementName());
			}
			throw new IllegalStateException("Max recursive level\n" + error.toString());
		}
		visited.add(javaElement);
		stack.add(javaElement);
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	@Override
	public void postVisit(IJavaElement javaElement) {
		if (monitor != null) {
			monitor.subTask(Utils.getPath(javaElement));
		}
		stack.remove(javaElement);
	}

	@Override
	public void visit(IJavaElement javaElement) {
		if (javaElement != null && !visited.contains(javaElement)) {
			if (javaElement instanceof IJavaProject) {
				visit((IJavaProject)javaElement);
			} else if (javaElement instanceof IJavaModel) {
				visit((IJavaModel)javaElement);
			} else if (javaElement instanceof IPackageFragmentRoot) {
				visit((IPackageFragmentRoot)javaElement);
			} else if (javaElement instanceof IPackageFragment) {
				visit((IPackageFragment)javaElement);
			} else if (javaElement instanceof IClassFile) {
				visit((IClassFile)javaElement);
			} else if (javaElement instanceof ICompilationUnit) {
				visit((ICompilationUnit)javaElement);
			} else if (javaElement instanceof IMember) {
				visit((IMember)javaElement);
			} else if (javaElement instanceof IAnnotation) {
				visit((IAnnotation)javaElement);
			} else if (javaElement instanceof IImportContainer) {
				visit((IImportContainer)javaElement);
			} else if (javaElement instanceof IImportDeclaration) {
				visit((IImportDeclaration)javaElement);
			} else if (javaElement instanceof IPackageDeclaration) {
				visit((IPackageDeclaration)javaElement);
			} else if (javaElement instanceof ITypeParameter) {
				visit((ITypeParameter)javaElement);
			} else if (javaElement instanceof ILocalVariable) {
				visit((ILocalVariable)javaElement);
			} else {
				CoreActivator.log(IStatus.ERROR, "Not handled !" + javaElement);
			}
		}
	}

	@Override
	public void visit(IJavaProject javaProject) {
		preVisit(javaProject);
		// nothing
		postVisit(javaProject);
	}

	@Override
	public void visit(IJavaModel javaModel) {
		preVisit(javaModel);
		// nothing
		postVisit(javaModel);
	}

	@Override
	public void visit(IPackageFragmentRoot packageFragmentRoot) {
		preVisit(packageFragmentRoot);
		// nothing
		postVisit(packageFragmentRoot);
	}

	@Override
	public void visit(IPackageFragment packageFragment) {
		preVisit(packageFragment);
		// nothing
		postVisit(packageFragment);
	}

	@Override
	public void visit(IImportDeclaration importDeclaration) {
		preVisit(importDeclaration);
		// nothing
		postVisit(importDeclaration);
	}

	@Override
	public void visit(IImportContainer importContainer) {
		preVisit(importContainer);
		// nothing
		postVisit(importContainer);
	}

	@Override
	public void visit(IPackageDeclaration packageDeclaration) {
		preVisit(packageDeclaration);
		// nothing
		postVisit(packageDeclaration);
	}

	@Override
	public void visit(IAnnotation annotation) {
		preVisit(annotation);
		// nothing
		postVisit(annotation);
	}

	@Override
	public void visit(ITypeParameter typeParameter) {
		preVisit(typeParameter);
		// nothing
		postVisit(typeParameter);
	}

	@Override
	public void visit(ILocalVariable localVariable) {
		preVisit(localVariable);
		// nothing
		postVisit(localVariable);
	}

	@Override
	public void visit(IClassFile classFile) {
		preVisit(classFile);
		// nothing
		postVisit(classFile);
	}

	@Override
	public void visit(ICompilationUnit compilationUnit) {
		preVisit(compilationUnit);
		// nothing
		postVisit(compilationUnit);
	}

	@Override
	public void visit(IMember member) {
		if (member instanceof IField) {
			visit((IField)member);
		} else if (member instanceof IMethod) {
			visit((IMethod)member);
		} else if (member instanceof IInitializer) {
			visit((IInitializer)member);
		} else if (member instanceof IType) {
			visit((IType)member);
		} else {
			throw new IllegalStateException("Not handled membre type:" + member.getClass());
		}
	}

	@Override
	public void visit(IField field) {
		preVisit(field);
		// nothing
		postVisit(field);
	}

	@Override
	public void visit(IMethod method) {
		preVisit(method);
		// nothing
		postVisit(method);
	}

	@Override
	public void visit(IInitializer initializer) {
		preVisit(initializer);
		// nothing
		postVisit(initializer);
	}

	@Override
	public void visit(IType type) {
		preVisit(type);
		// nothing
		postVisit(type);
	}
}
