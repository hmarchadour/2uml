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
import org.obeonetwork.jdt2uml.core.api.store.JDTStore;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;
import org.obeonetwork.jdt2uml.core.internal.store.JDTIdentStoreImpl;
import org.obeonetwork.jdt2uml.core.internal.visitor.JDTVisitorHandlerImpl;
import org.obeonetwork.jdt2uml.core.internal.visitor.JDTVisitorImpl;

public final class Factory {
	public static JDTVisitorHandler<String> createJDTVisitorHandler(IProgressMonitor monitor) {
		return new JDTVisitorHandlerImpl(monitor);
	}

	public static JDTVisitor createJDTVisitor(JDTVisitorHandler<?> handler) {
		return new JDTVisitorImpl(handler);
	}

	public static JDTStore<String> createIdentStore() {
		return new JDTIdentStoreImpl();
	}
}
