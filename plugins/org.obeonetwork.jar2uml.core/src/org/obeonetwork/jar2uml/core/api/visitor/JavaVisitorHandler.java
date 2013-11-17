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
package org.obeonetwork.jar2uml.core.api.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface JavaVisitorHandler<T> {

	void caseClass(Class<?> aClass);

	void casePrimitive(Class<?> aClass);

	void caseSuperClass(Class<?> aSuperClass);

	void caseImplementedInterface(Class<?> anImplInterface);

	void caseInterface(Class<?> anInterface);

	void caseAnnotation(Class<?> anAnnotation);

	void caseEnum(Class<?> anEnum);

	void caseField(Field aField);

	void caseMethod(Method method);

	T getResult();
}
