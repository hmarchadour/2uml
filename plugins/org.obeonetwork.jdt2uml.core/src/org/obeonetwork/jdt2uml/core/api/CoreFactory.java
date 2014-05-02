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

import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.core.api.job.JobDescriptor;
import org.obeonetwork.jdt2uml.core.api.job.ProjectTODO;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyComponent;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyContainer;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyPackage;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.visitor.Visitable;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.ProjectTODOImpl;
import org.obeonetwork.jdt2uml.core.internal.lazy.LazyClassImpl;
import org.obeonetwork.jdt2uml.core.internal.lazy.LazyComponentImpl;
import org.obeonetwork.jdt2uml.core.internal.lazy.LazyPackageImpl;
import org.obeonetwork.jdt2uml.core.internal.resolver.ResolverImpl;
import org.obeonetwork.jdt2uml.core.internal.wrapper.TypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.wrapper.VisitableImpl;

public final class CoreFactory {

	public static ITypeWrapper toWrappedType(IType type) {
		return new TypeWrapper(type);
	}

	public static Visitable toVisitable(IJavaElement javaElement) {
		return new VisitableImpl(javaElement);
	}

	public static ProjectTODO createJobsTODO(IJavaProject javaProject, JobDescriptor projectDescriptor,
			JobDescriptor libDescriptor) {
		return new ProjectTODOImpl(javaProject, projectDescriptor, libDescriptor);
	}

	public static LazyClass createLazyClass(LazyPackage lazyPackage, LazyHandler lazyClassifier) {
		return new LazyClassImpl(lazyPackage, lazyClassifier);
	}

	public static LazyPackage createLazyPackage(LazyContainer lazyContainer, Package pack) {
		return new LazyPackageImpl(lazyContainer, pack);
	}

	public static LazyComponent createLazyComponent(Model model, Component component) {
		return new LazyComponentImpl(model, component);
	}

	public static Resolver createResolver(Namespace context, Set<LazyClass> lazyClasses) {
		return new ResolverImpl(context, lazyClasses);
	}
}
