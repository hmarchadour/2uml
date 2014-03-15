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
package org.obeonetwork.jdt2uml.core.api.handler;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;

public interface JDTHandler {

	void caseJavaProject(IJavaProject javaProject, JModelVisitor visitor);

	void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JModelVisitor visitor);

	void casePackageFragment(IPackageFragment packageFragment, JModelVisitor visitor);

	void caseCompilationUnit(ICompilationUnit compilationUnit, JModelVisitor visitor);

	void caseClassFile(IClassFile classFile, JModelVisitor visitor);

	void caseField(IField field, JModelVisitor visitor);

	void caseMethod(IMethod method, JModelVisitor visitor);

	void caseInitializer(IInitializer initializer, JModelVisitor visitor);

	void caseImportDeclaration(IImportDeclaration importDeclaration, JModelVisitor visitor);

	void caseImportContainer(IImportContainer importContainer, JModelVisitor visitor);

	void casePackageDeclaration(IPackageDeclaration packageDeclaration, JModelVisitor visitor);

	void caseTypeParameter(ITypeParameter typeParameter, JModelVisitor visitor);

	void caseLocalVariable(ILocalVariable localVariable, JModelVisitor visitor);

	void caseType(IType type, JModelVisitor visitor);

	void caseType(ITypeWrapper type, JModelVisitor visitor);

	void caseAnnotation(IAnnotation annotation, JModelVisitor visitor);

	void caseJavaModel(IJavaModel javaModel, JModelVisitor visitor);

	Model getModel();

	IProgressMonitor getMonitor();
}
