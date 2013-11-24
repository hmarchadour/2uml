package org.obeonetwork.jar2uml.tests.unit.store;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ClassStoreAddCommon.class, ClassStoreAddAnnotation.class, ClassStoreAddClass.class,
		ClassStoreAddEnum.class, ClassStoreAddInterface.class})
public class AllStoreTests {

}
