package org.obeonetwork.jdt2uml.updator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.obeonetwork.jdt2uml.updator.tests.integ.AllIntegTests;
import org.obeonetwork.jdt2uml.updator.tests.unit.AllUnitTests;

@RunWith(Suite.class)
@SuiteClasses({AllUnitTests.class, AllIntegTests.class})
public class AllTests {

}
