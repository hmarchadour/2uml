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

public interface JDTVisitorHandler {

	void caseJavaProject(IJavaProject javaProject, JDTVisitor visitor);

	void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor);

	void casePackageFragment(IPackageFragment packageFragment, JDTVisitor visitor);

	void caseCompilationUnit(ICompilationUnit compilationUnit, JDTVisitor visitor);

	void caseClassFile(IClassFile classFile, JDTVisitor visitor);

	void caseField(IField field, JDTVisitor visitor);

	void caseMethod(IMethod method, JDTVisitor visitor);

	void caseInitializer(IInitializer initializer, JDTVisitor visitor);

	void caseImportDeclaration(IImportDeclaration importDeclaration, JDTVisitor visitor);

	void caseImportContainer(IImportContainer importContainer, JDTVisitor visitor);

	void casePackageDeclaration(IPackageDeclaration packageDeclaration, JDTVisitor visitor);

	void caseTypeParameter(ITypeParameter typeParameter, JDTVisitor visitor);

	void caseLocalVariable(ILocalVariable localVariable, JDTVisitor visitor);

	void caseType(IType type, JDTVisitor visitor);

	void caseAnnotation(IAnnotation annotation, JDTVisitor visitor);

	void caseJavaModel(IJavaModel javaModel, JDTVisitor visitor);

	Model getModel();

	IProgressMonitor getMonitor();

}
