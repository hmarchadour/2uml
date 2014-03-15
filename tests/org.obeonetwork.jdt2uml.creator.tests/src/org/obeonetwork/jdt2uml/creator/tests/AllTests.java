package org.obeonetwork.jdt2uml.creator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.obeonetwork.jdt2uml.creator.tests.integ.AllIntegTests;
import org.obeonetwork.jdt2uml.creator.tests.unit.AllUnitTests;

@RunWith(Suite.class)
@SuiteClasses({AllUnitTests.class, AllIntegTests.class})
public class AllTests {

}
