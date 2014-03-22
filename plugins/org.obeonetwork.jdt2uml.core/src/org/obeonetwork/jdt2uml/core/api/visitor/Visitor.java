/*******************************************************************************
 * Copyright (c) 2014 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jdt2uml.core.api.visitor;

import org.eclipse.core.runtime.IProgressMonitor;
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

public interface Visitor {

	IProgressMonitor getMonitor();

	Visitor newInstance();

	void visit(IJavaElement javaElement);

	void visit(IType type);

	void visit(IInitializer initializer);

	void visit(IMethod method);

	void visit(IField field);

	void visit(IMember member);

	void visit(ICompilationUnit compilationUnit);

	void visit(IClassFile classFile);

	void visit(ILocalVariable localVariable);

	void visit(ITypeParameter typeParameter);

	void visit(IAnnotation annotation);

	void visit(IPackageDeclaration packageDeclaration);

	void visit(IImportContainer importContainer);

	void visit(IImportDeclaration importDeclaration);

	void visit(IPackageFragment packageFragment);

	void visit(IPackageFragmentRoot packageFragmentRoot);

	void visit(IJavaModel javaModel);

	void visit(IJavaProject javaProject);

	void postVisit(IJavaElement javaElement);

	void preVisit(IJavaElement javaElement);

}
