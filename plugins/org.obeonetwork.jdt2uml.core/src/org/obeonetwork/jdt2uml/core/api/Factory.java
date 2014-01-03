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
package org.obeonetwork.jdt2uml.core.api;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;
import org.obeonetwork.jdt2uml.core.internal.store.JDTIdentStoreImpl;
import org.obeonetwork.jdt2uml.core.internal.visitor.JDTVisitorImpl;
import org.obeonetwork.jdt2uml.core.internal.visitor.LibrariesVisitorHandler;
import org.obeonetwork.jdt2uml.core.internal.visitor.ProjectVisitorHandler;

public final class Factory {
	public static JDTVisitorHandler createJDTProjectVisitorHandler(Model model, IProgressMonitor monitor) {
		return new ProjectVisitorHandler(model, monitor);
	}

	public static JDTVisitorHandler createJDTLibrariesVisitorHandler(Model model, IProgressMonitor monitor) {
		return new LibrariesVisitorHandler(model, monitor);
	}

	public static JDTVisitor createJDTVisitor(JDTVisitorHandler handler) {
		return new JDTVisitorImpl(handler);
	}

	public static JDTStore<String> createIdentStore() {
		return new JDTIdentStoreImpl();
	}
}
