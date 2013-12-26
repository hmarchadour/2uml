package org.obeonetwork.jdt2uml.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.obeonetwork.jdt2uml.core.tests.integ.AllIntegTests;
import org.obeonetwork.jdt2uml.core.tests.unit.AllUnitTests;

@RunWith(Suite.class)
@SuiteClasses({AllUnitTests.class, AllIntegTests.class})
public class AllTests {

}
