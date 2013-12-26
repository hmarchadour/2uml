/*******************************************************************************
 * Copyright (c) 2014 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.tests.integ;

import org.junit.Before;
import org.junit.Test;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.tests.api.TestUtils;

public class VisitClassAndInterfaceScenario {

	protected ClassStore internal;

	protected ClassStore external;

	private JavaVisitor javaVisitor;

	@Before
	public void setUp() throws Exception {
		internal = Factory.createClassStore();
		external = Factory.createClassStore();
		javaVisitor = Factory.createJavaVisitor(Factory.createJavaRelationHandler(internal, external));
	}

	private void test(Class<?> classUnderTest, Class<?>[] internalClassesToFind,
			Class<?>[] externalClassesToFind) {
		for (Class<?> internalClassToFind : internalClassesToFind) {
			internal.add(internalClassToFind);
		}
		javaVisitor.visit(classUnderTest);
		TestUtils.checkStores(internal, external, internalClassesToFind, externalClassesToFind);
	}

	class MyObject {

	}

	class A {
		MyObject myObject;
	}

	@Test
	public void caseA() {
		test(A.class, new Class<?>[] {A.class},
				new Class<?>[] {this.getClass(), Object.class, MyObject.class});
	}

	/******************************************************************/

	class B {
		MyObject getMyObject() {
			return null;
		}
	}

	@Test
	public void caseB() {
		test(B.class, new Class<?>[] {B.class},
				new Class<?>[] {this.getClass(), Object.class, MyObject.class});
	}

	/******************************************************************/

	class C {
		C(MyObject myObject) {
		}
	}

	@Test
	public void caseC() {
		test(C.class, new Class<?>[] {C.class},
				new Class<?>[] {this.getClass(), Object.class, MyObject.class});
	}

	/******************************************************************/

	class D extends DSuper {
	}

	class DSuper {
	}

	@Test
	public void caseD() {
		test(D.class, new Class<?>[] {D.class}, new Class<?>[] {this.getClass(), Object.class, DSuper.class});
	}

	/******************************************************************/

	class E implements IE {
	}

	interface IE {
	}

	@Test
	public void caseE() {
		test(E.class, new Class<?>[] {E.class}, new Class<?>[] {this.getClass(), Object.class, IE.class});
	}

	/******************************************************************/

	class F extends FSuper {
	}

	class FSuper implements IFSuper {
	}

	interface IFSuper extends IIFSuper {
	}

	interface IIFSuper {
	}

	@Test
	public void caseF() {
		test(F.class, new Class<?>[] {F.class}, new Class<?>[] {this.getClass(), Object.class, FSuper.class,
				IFSuper.class, IIFSuper.class});
	}

	/******************************************************************/

	class G extends GSuper {
	}

	interface G1 {
	}

	interface G2 {
	}

	interface G3 {
	}

	class GSuper implements IGSuper {
		G1 g1;

		@Override
		public G3 getG3() {
			return null;
		}

		@Override
		public void setG2(G2 g2) {
		}
	}

	interface IGSuper extends IIGSuper {
		void setG2(G2 g2);
	}

	interface IIGSuper {
		G3 getG3();
	}

	@Test
	public void caseG() {
		test(G.class, new Class<?>[] {G.class}, new Class<?>[] {this.getClass(), Object.class, GSuper.class,
				IGSuper.class, IIGSuper.class, G1.class, G2.class, G3.class});
	}
}
