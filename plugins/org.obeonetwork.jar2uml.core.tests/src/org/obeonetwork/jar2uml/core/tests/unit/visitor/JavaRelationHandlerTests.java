package org.obeonetwork.jar2uml.core.tests.unit.visitor;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;

import demo.ArrayFields;

@RunWith(Parameterized.class)
public class JavaRelationHandlerTests {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private Class<?> clazz;

	public JavaRelationHandlerTests(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		params.add(new Object[] {ArrayFields.class});
		return params;
	}

	@Test
	public void test() {
		Factory.createJavaRelationHandler(null, null);
		fail("Not yet implemented");
	}

}
