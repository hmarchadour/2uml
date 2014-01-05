package org.obeonetwork.jdt2uml.core.internal.handler.creator;

import java.util.Set;

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
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;

public abstract class AbstractCreatorHandler implements JDTCreatorHandler {

	private final Model model;

	private final IProgressMonitor monitor;

	public AbstractCreatorHandler(IProgressMonitor monitor) {
		this.model = UMLFactory.eINSTANCE.createModel();
		this.monitor = monitor;
	}

	protected void casePre(IJavaElement javaElement, JDTVisitor visitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	protected void casePost(IJavaElement javaElement, JDTVisitor visitor) {
		if (javaElement instanceof IParent) {
			try {
				IJavaElement[] subJavaElements = ((IParent)javaElement).getChildren();
				for (IJavaElement subJavaElement : subJavaElements) {
					visitor.visit(subJavaElement);
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		if (monitor != null) {
			monitor.subTask(Utils.getPath(javaElement));
			monitor.worked(1);
		}
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JDTVisitor visitor) {
		casePre(javaProject, visitor);
		// nothing
		casePost(javaProject, visitor);
	}

	@Override
	public void caseJavaModel(IJavaModel javaModel, JDTVisitor visitor) {
		casePre(javaModel, visitor);
		// nothing
		casePost(javaModel, visitor);
	}

	@Override
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor) {
		casePre(fragmentRoot, visitor);
		// nothing
		casePost(fragmentRoot, visitor);
	}

	@Override
	public void casePackageFragment(IPackageFragment packageFragment, JDTVisitor visitor) {
		casePre(packageFragment, visitor);
		// nothing
		casePost(packageFragment, visitor);
	}

	@Override
	public abstract void caseType(IType type, JDTVisitor visitor);

	@Override
	public void caseAnnotation(IAnnotation annotation, JDTVisitor visitor) {
		casePre(annotation, visitor);

		// nothing (already handled during the visit)

		casePost(annotation, visitor);
	}

	@Override
	public void caseClassFile(IClassFile classFile, JDTVisitor visitor) {
		casePre(classFile, visitor);

		IType classType = classFile.getType();
		if (classType != null) {
			visitor.visit(classType);
		}
		casePost(classFile, visitor);
	}

	@Override
	public void caseCompilationUnit(ICompilationUnit compilationUnit, JDTVisitor visitor) {
		casePre(compilationUnit, visitor);

		IType primaryType = compilationUnit.findPrimaryType();
		if (primaryType != null) {
			visitor.visit(primaryType);
		}

		casePost(compilationUnit, visitor);
	}

	@Override
	public void caseField(IField field, JDTVisitor visitor) {
		casePre(field, visitor);

		Set<String> qualifiedNames = Utils.getQualifiedNames(field);
		for (String qualifiedName : qualifiedNames) {
			Set<IType> retrieveTypes = Utils.retrieveTypes(field.getJavaProject(), qualifiedName);
			for (IType type : retrieveTypes) {
				visitor.visit(type);
			}
		}

		casePost(field, visitor);
	}

	@Override
	public void caseInitializer(IInitializer initializer, JDTVisitor visitor) {
		casePre(initializer, visitor);
		// nothing
		casePost(initializer, visitor);
	}

	@Override
	public void caseMethod(IMethod method, JDTVisitor visitor) {
		casePre(method, visitor);

		Set<String> qualifiedNames = Utils.getQualifiedNames(method);
		for (String qualifiedName : qualifiedNames) {
			Set<IType> retrieveTypes = Utils.retrieveTypes(method.getJavaProject(), qualifiedName);
			for (IType type : retrieveTypes) {
				visitor.visit(type);
			}
		}

		casePost(method, visitor);
	}

	@Override
	public void caseImportContainer(IImportContainer importContainer, JDTVisitor visitor) {
		casePre(importContainer, visitor);
		// nothing
		casePost(importContainer, visitor);
	}

	@Override
	public void casePackageDeclaration(IPackageDeclaration packageDeclaration, JDTVisitor visitor) {
		casePre(packageDeclaration, visitor);
		// nothing
		casePost(packageDeclaration, visitor);
	}

	@Override
	public void caseTypeParameter(ITypeParameter typeParameter, JDTVisitor visitor) {
		casePre(typeParameter, visitor);
		// nothing
		casePost(typeParameter, visitor);
	}

	@Override
	public void caseLocalVariable(ILocalVariable localVariable, JDTVisitor visitor) {
		casePre(localVariable, visitor);
		// nothing
		casePost(localVariable, visitor);
	}

	@Override
	public void caseImportDeclaration(IImportDeclaration importDeclaration, JDTVisitor visitor) {
		casePre(importDeclaration, visitor);
		// nothing
		casePost(importDeclaration, visitor);
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public IProgressMonitor getMonitor() {
		return monitor;
	}
}
