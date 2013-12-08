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
package org.obeonetwork.jar2uml.core.tests.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.obeonetwork.jar2uml.core.tests.unit.store.AllStoreTests;
import org.obeonetwork.jar2uml.core.tests.unit.visitor.AllVisitorTests;

@RunWith(Suite.class)
@SuiteClasses({AllStoreTests.class, AllVisitorTests.class})
public class AllTests {

}
