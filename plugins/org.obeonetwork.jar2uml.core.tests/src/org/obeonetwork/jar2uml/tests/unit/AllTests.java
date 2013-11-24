package org.obeonetwork.jar2uml.tests.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.obeonetwork.jar2uml.tests.unit.store.AllStoreTests;
import org.obeonetwork.jar2uml.tests.unit.visitor.AllVisitorTests;

@RunWith(Suite.class)
@SuiteClasses({AllStoreTests.class, AllVisitorTests.class})
public class AllTests {

}
