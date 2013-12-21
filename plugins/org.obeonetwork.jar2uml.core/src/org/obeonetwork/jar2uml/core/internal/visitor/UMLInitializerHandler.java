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
package org.obeonetwork.jar2uml.core.internal.visitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.ModelStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class UMLInitializerHandler implements JavaVisitorHandler<Set<Element>> {

	private final Component parent;

	private final ModelStore modelStore;

	private final Set<Element> createdUMLElements;

	public UMLInitializerHandler(Component parent, ModelStore modelStore) {
		this.parent = parent;
		this.modelStore = modelStore;
		createdUMLElements = new HashSet<Element>();
	}

	@Override
	public Set<Element> getResult() {
		return createdUMLElements;
	}

	@Override
	public void caseClass(Class<?> aClass) {
		org.eclipse.uml2.uml.Package pack = Utils.handlePackage(parent, aClass.getPackage());
		org.eclipse.uml2.uml.Class createClass = pack.createOwnedClass(aClass.getSimpleName(),
				Modifier.isAbstract(aClass.getModifiers()));
		createdUMLElements.add(createClass);
		modelStore.add(aClass, createClass);
	}

	@Override
	public void caseSuperClass(Class<?> aSuperClass) {
		// Nothing
	}

	@Override
	public void caseImplementedInterface(Class<?> anImplInterface) {
		// Nothing
	}

	@Override
	public void caseInterface(Class<?> anInterface) {
		org.eclipse.uml2.uml.Package pack = Utils.handlePackage(parent, anInterface.getPackage());
		Interface createInterface = pack.createOwnedInterface(anInterface.getSimpleName());
		createdUMLElements.add(createInterface);
		modelStore.add(anInterface, createInterface);
	}

	@Override
	public void caseAnnotation(Class<?> anAnnotation) {
		// TODO handle annotation

	}

	@Override
	public void caseEnum(Class<?> anEnum) {
		org.eclipse.uml2.uml.Package pack = Utils.handlePackage(parent, anEnum.getPackage());
		Enumeration createEnumeration = pack.createOwnedEnumeration(anEnum.getSimpleName());
		createdUMLElements.add(createEnumeration);
		modelStore.add(anEnum, createEnumeration);
	}

	@Override
	public void caseConstructor(Constructor<?> constructor) {
		// TODO
	}

	@Override
	public void caseField(Field aField) {
		// Nothing
	}

	@Override
	public void caseMethod(Method method) {
		// Nothing
	}
}
