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
package org.obeonetwork.jar2uml.core.tests.unit.store;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite regroup all unit tests about stores used in the core plugins.
 */
@RunWith(Suite.class)
@SuiteClasses({JarStoreCommon.class, JarStoreAnnotation.class, JarStoreClass.class,
		JarStoreEnum.class, JarStoreInterface.class})
public class AllUnitStoreTests {

}
