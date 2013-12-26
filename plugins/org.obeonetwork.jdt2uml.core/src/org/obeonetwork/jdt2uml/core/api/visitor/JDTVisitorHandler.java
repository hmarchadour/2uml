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

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;

public interface JDTVisitorHandler<T> {

	void caseJavaProject(IJavaProject javaProject, JDTVisitor visitor);

	void casePackageFragmentRoot(IPackageFragmentRoot fragmentRoot, JDTVisitor visitor);

	void casePackageFragment(IPackageFragment packageFragment, JDTVisitor visitor);

	void caseCompilationUnit(ICompilationUnit compilationUnit, JDTVisitor visitor);

	void caseClassFile(IClassFile classFile, JDTVisitor visitor);

	void caseField(IField field, JDTVisitor visitor);

	void caseMethod(IMethod method, JDTVisitor visitor);

	void caseInitializer(IInitializer initializer, JDTVisitor visitor);

	JDTStore<T> getInternal();

	JDTStore<T> getExternal();

}
