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
import java.util.Arrays;
import java.util.List;

import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class JavaRelationHandler implements JavaVisitorHandler<Void> {

	private final ClassStore internal;

	private final ClassStore external;

	public JavaRelationHandler(ClassStore internal, ClassStore external) {
		this.internal = internal;
		this.external = external;
	}

	@Override
	public void casePrimitive(Class<?> aClass) {
		// Nothing
	}

	@Override
	public void caseClass(Class<?> aClass) {
		// Nothing
	}

	@Override
	public void caseSuperClass(Class<?> aSuperClass) {
		handleClass(aSuperClass);
	}

	@Override
	public void caseImplementedInterface(Class<?> anImplInterface) {
		handleClass(anImplInterface);
	}

	@Override
	public void caseInterface(Class<?> anInterface) {
		// Nothing
	}

	@Override
	public void caseAnnotation(Class<?> anAnnotation) {
		// Nothing
	}

	@Override
	public void caseEnum(Class<?> anEnum) {
		// Nothing
	}

	@Override
	public void caseConstructor(Constructor<?> constructor) {
		handleClasses(Arrays.asList(constructor.getParameterTypes()));
	}

	@Override
	public void caseField(Field aField) {
		if (!Utils.isPrimitive(aField)) {
			handleClass(aField.getType());
		}
	}

	@Override
	public void caseMethod(Method method) {
		Class<?> returnType = Utils.findMethodReturn(method);
		handleClass(returnType);
		handleClasses(Utils.findMethodParams(method));
	}

	@Override
	public Void getResult() {
		return null;
	}

	private void handleClasses(List<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			handleClass(clazz);
		}
	}

	private void handleClass(Class<?> clazz) {
		if (clazz != null && isExternal(clazz)) {
			external.add(clazz);
		}
	}

	private boolean isExternal(Class<?> clazz) {
		if (clazz.isArray()) {
			return isExternal(clazz.getComponentType());
		} else {
			return !internal.getAllJavaItems().contains(clazz);
		}
	}

}
