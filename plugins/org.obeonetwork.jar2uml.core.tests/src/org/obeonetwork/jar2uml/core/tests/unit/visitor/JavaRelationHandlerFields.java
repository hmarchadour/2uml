package org.obeonetwork.jar2uml.core.tests.unit.visitor;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import demo.ArrayFields;

@RunWith(Parameterized.class)
public class JavaRelationHandlerFields {

	protected final Field fieldToUse;
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

	/**
	 * @param clazzToUse
	 * @param internalToFind
	 * @param externalToFind
	 */
	public JavaRelationHandlerFields(Field fieldToUse, Class<?>[] internalToFind, Class<?>[] externalToFind) {
		this.fieldToUse = fieldToUse;
		this.internalToFind = internalToFind;
		this.externalToFind = externalToFind;
	}

	@Parameters
	public static Collection<Object[]> params() {
		HashSet<Object[]> params = new HashSet<Object[]>();
		Field[] declaredFields = ArrayFields.class.getDeclaredFields();
		for (Field field : declaredFields) {
			params.add(new Object[] { field, new Class<?>[] { field.getDeclaringClass() }, new Class<?>[] {} });
		}
		return params;
	}

	@Test
	public void caseField() {
		internal.add(createMock(File.class), fieldToUse.getDeclaringClass());
		javaRelationHandler.caseClass(fieldToUse.getDeclaringClass());
		javaRelationHandler.caseField(fieldToUse);
		assertEquals(internalToFind.length, internal.getAllJavaItems().size());
		assertEquals(externalToFind.length, external.getAllJavaItems().size());
	}

}
