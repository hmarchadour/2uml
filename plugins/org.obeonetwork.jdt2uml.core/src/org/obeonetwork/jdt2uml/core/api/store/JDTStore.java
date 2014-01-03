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
package org.obeonetwork.jdt2uml.core.api.store;

import java.util.Set;

import org.eclipse.jdt.core.IType;

public interface JDTStore<T> {

	void add(IType type);

	Set<T> getAllJavaItems();

	Set<T> getAllJavaClasses();

	Set<T> getAllJavaInterfaces();

	Set<T> getAllJavaAnnotations();

	Set<T> getAllJavaEnums();

	boolean exist(T item);

	boolean exist(IType type);
}
