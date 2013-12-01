package org.obeonetwork.jar2uml.core.tests.unit.visitor;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import demo.ArrayFields;

@RunWith(Parameterized.class)
public abstract class JavaRelationHandlerHelper {

	protected final Class<?> clazz;
	protected final Class<?>[] internalToFind;
	protected final Class<?>[] externalToFind;
	protected ClassStore internal;
	protected ClassStore external;
	protected JavaVisitorHandler<Void> javaRelationHandler;

	@Before
	public void setUp() throws Exception {
		internal = Factory.createClassStore();
		external = Factory.createClassStore();
		javaRelationHandler = Factory.createJavaRelationHandler(internal, external);
	}

	public JavaRelationHandlerHelper(Class<?> clazzToUse, Class<?>[] internalToFind, Class<?>[] externalToFind) {
		this.clazz = clazzToUse;
		this.internalToFind = internalToFind;
		this.externalToFind = externalToFind;
	}

	@Parameters
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		params.add(new Object[] { ArrayFields.class, new Class<?>[] {}, new Class<?>[] {} });
		return params;
	}

}
