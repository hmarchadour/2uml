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

import org.eclipse.jdt.core.IType;
import org.obeonetwork.jdt2uml.core.api.handler.JDTHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.visitor.JModelVisitorImpl;
import org.obeonetwork.jdt2uml.core.internal.wrapper.TypeWrapper;

public final class CoreFactory {

	public static ITypeWrapper toWrappedType(IType type) {
		return new TypeWrapper(type);
	}

	public static JModelVisitor createJDTVisitor(JDTHandler handler) {
		return new JModelVisitorImpl(handler);
	}
}
