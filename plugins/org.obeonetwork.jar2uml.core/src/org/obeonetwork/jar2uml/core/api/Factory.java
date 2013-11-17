/*******************************************************************************
 * Copyright (c) 2013 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.api;

import java.util.Set;

import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;
import org.obeonetwork.jar2uml.core.internal.store.ClassStore;
import org.obeonetwork.jar2uml.core.internal.store.ModelStore;
import org.obeonetwork.jar2uml.core.internal.visitor.DefaultJavaVisitor;
import org.obeonetwork.jar2uml.core.internal.visitor.JavaRelationHandler;
import org.obeonetwork.jar2uml.core.internal.visitor.UMLInitializerHandler;
import org.obeonetwork.jar2uml.core.internal.visitor.UMLRelationHandler;

public final class Factory {

	public static JavaVisitorHandler<Set<Element>> createInitializerHandler(Component parent,
			ModelStore modelStore) {
		return new UMLInitializerHandler(parent, modelStore);
	}

	public static JavaVisitorHandler<Void> createUMLRelationHandler(ModelStore modelStore) {
		return new UMLRelationHandler(modelStore);
	}

	public static JavaVisitorHandler<Void> createJavaRelationHandler(ClassStore internal, ClassStore external) {
		return new JavaRelationHandler(internal, external);
	}

	public static JavaVisitor createJavaVisitor(JavaVisitorHandler<?> handler) {
		return new DefaultJavaVisitor(handler);
	}
}
