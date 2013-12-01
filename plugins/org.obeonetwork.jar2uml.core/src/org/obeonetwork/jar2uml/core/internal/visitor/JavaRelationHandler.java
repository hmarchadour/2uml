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

import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class JavaRelationHandler implements JavaVisitorHandler<Void> {

	private final ClassStore internal;

	private final ClassStore external;

	private Class<?> context;

	public JavaRelationHandler(ClassStore internal, ClassStore external) {
		this.internal = internal;
		this.external = external;
	}

	@Override
	public void caseClass(Class<?> aClass) {
		context = aClass;
	}

	@Override
	public void casePrimitive(Class<?> aClass) {
		context = aClass;
	}

	@Override
	public void caseSuperClass(Class<?> aSuperClass) {
		if (isExternal(aSuperClass)) {
			external.addClass(internal.retrieveFile(context).get(), aSuperClass);
		}
	}

	@Override
	public void caseImplementedInterface(Class<?> anImplInterface) {
		if (isExternal(anImplInterface)) {
			external.addInterface(internal.retrieveFile(context).get(), anImplInterface);
		}
	}

	@Override
	public void caseInterface(Class<?> anInterface) {
		context = anInterface;
	}

	@Override
	public void caseAnnotation(Class<?> anAnnotation) {
		context = anAnnotation;
	}

	@Override
	public void caseEnum(Class<?> anEnum) {
		context = anEnum;
	}

	@Override
	public void caseConstructor(Constructor<?> constructor) {
		// TODO
	}

	@Override
	public void caseField(Field aField) {
		if (!Utils.isPrimitive(aField) && isExternal(aField.getType())) {
			external.add(internal.retrieveFile(context).get(), aField.getType());
		}
	}

	@Override
	public void caseMethod(Method method) {
		Class<?> returnType = Utils.findMethodReturn(method);
		if (returnType != null && isExternal(returnType)) {
			external.add(internal.retrieveFile(context).get(), returnType);
		}
		for (Class<?> parameterType : Utils.findMethodParams(method)) {
			if (isExternal(parameterType)) {
				external.add(internal.retrieveFile(context).get(), parameterType);
			}
		}
	}

	@Override
	public Void getResult() {
		return null;
	}

	private boolean isExternal(Class<?> clazz) {
		if (clazz.isArray()) {
			return isExternal(clazz.getComponentType());
		} else {
			return !internal.getAllJavaItems().contains(clazz);
		}
	}

}