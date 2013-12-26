package org.obeonetwork.jdt2uml.core.internal.visitor;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

public class JDTVisitorHandlerImpl implements JDTVisitorHandler<String> {

	private JDTStore<String> internalStore;

	private JDTStore<String> externalStore;

	private IProgressMonitor monitor;

	public JDTVisitorHandlerImpl(IProgressMonitor monitor) {
		internalStore = Factory.createIdentStore();
		externalStore = Factory.createIdentStore();
		this.monitor = monitor;
	}

	protected void casePre(IJavaElement elem) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	protected void casePost(IJavaElement elem) {

	}

	protected void addType(IType type) {
		String qualifiedName = type.getFullyQualifiedName();
		if (!internalStore.exist(qualifiedName) && !externalStore.exist(qualifiedName)) {
			System.out.println(qualifiedName);
			if (Utils.isExternal(type)) {
				externalStore.add(type);
			} else {
				internalStore.add(type);
			}
			try {
				IType[] types = type.getTypes();
				for (IType subType : types) {
					addType(subType);
				}
				String superclassName = type.getSuperclassName();
				for (IType iType : Utils.getType(type, superclassName)) {
					addType(iType);
				}
				String[] superInterfaceNames = type.getSuperInterfaceNames();
				for (String superInterfaceName : superInterfaceNames) {
					for (IType iType : Utils.getType(type, superInterfaceName)) {
						addType(iType);
					}
				}
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void caseJavaProject(IJavaProject javaProject, JDTVisitor visitor) {
		casePre(javaProject);
		// nothing
		casePost(javaProject);
	}

	@Override
	public void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor) {
		casePre(fragmentRoot);
		// nothing
		casePost(fragmentRoot);
	}

	@Override
	public void casePackageFragment(IPackageFragment packageFragment, JDTVisitor visitor) {
		casePre(packageFragment);
		monitor.subTask(packageFragment.getElementName());
		monitor.worked(1);
		casePost(packageFragment);
	}

	@Override
	public void caseClassFile(IClassFile classFile, JDTVisitor visitor) {
		casePre(classFile);
		monitor.subTask(classFile.getElementName());
		addType(classFile.getType());
		casePost(classFile);
	}

	@Override
	public void caseCompilationUnit(ICompilationUnit compilationUnit, JDTVisitor visitor) {
		casePre(compilationUnit);
		monitor.subTask(compilationUnit.getElementName());
		addType(compilationUnit.findPrimaryType());
		casePost(compilationUnit);
	}

	@Override
	public void caseField(IField field, JDTVisitor visitor) {
		casePre(field);
		Set<String> qualifiedNames = Utils.getQualifiedNames(field);
		for (String qualifiedName : qualifiedNames) {
			if (!internalStore.exist(qualifiedName) && !externalStore.exist(qualifiedName)) {
				Set<IType> retrieveTypes = Utils.retrieveTypes(field.getJavaProject(), qualifiedName);
				for (IType type : retrieveTypes) {
					addType(type);
				}
			}
		}
		casePost(field);
	}

	@Override
	public void caseInitializer(IInitializer initializer, JDTVisitor visitor) {
		casePre(initializer);
		// nothing
	}

	@Override
	public void caseMethod(IMethod method, JDTVisitor visitor) {
		casePre(method);
		Set<String> qualifiedNames = Utils.getQualifiedNames(method);
		for (String qualifiedName : qualifiedNames) {
			if (!internalStore.exist(qualifiedName) && !externalStore.exist(qualifiedName)) {
				Set<IType> retrieveTypes = Utils.retrieveTypes(method.getJavaProject(), qualifiedName);
				for (IType type : retrieveTypes) {
					addType(type);
				}
			}
		}
		casePost(method);
	}

	@Override
	public JDTStore<String> getInternal() {
		return internalStore;
	}

	@Override
	public JDTStore<String> getExternal() {
		return externalStore;
	}

}
