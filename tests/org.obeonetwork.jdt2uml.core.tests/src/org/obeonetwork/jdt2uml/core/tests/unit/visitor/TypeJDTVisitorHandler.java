package org.obeonetwork.jdt2uml.core.tests.unit.visitor;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.UMLFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;
import org.obeonetwork.jdt2uml.core.internal.handler.creator.ProjectCreatorHandler;

@SuppressWarnings("restriction")
@RunWith(Parameterized.class)
public class TypeJDTVisitorHandler {

	private static String NAME = "Object";

	private static String PACKAGE_NAME = "java.lang";

	private static String FULL_QUALIFIED_NAME = "java.lang.Object";

	private ProjectCreatorHandler visitorHandler;

	private JDTVisitor mockedVisitor;

	private String testCaseName;

	private Boolean isExternal;

	@Before
	public void setUp() throws Exception {
		visitorHandler = (ProjectCreatorHandler)Factory.createJDTProjectVisitorHandler(null);
		mockedVisitor = EasyMock.createMock(JDTVisitor.class);
	}

	public TypeJDTVisitorHandler(String testCaseName, Boolean isExternal) {
		this.testCaseName = testCaseName;
		this.isExternal = isExternal;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {

		HashSet<Object[]> params = new HashSet<Object[]>();

		params.add(new Object[] {"Internal", Boolean.FALSE});
		params.add(new Object[] {"External", Boolean.TRUE});

		return params;
	}

	private void initMocks(ITypeWrapper mockedType) throws JavaModelException {
		EasyMock.expect(mockedType.getType()).andReturn(EasyMock.createMock(IType.class)).anyTimes();
		EasyMock.expect(mockedType.isExternal()).andReturn(isExternal).anyTimes();
		EasyMock.expect(mockedType.getElementName()).andReturn(NAME).anyTimes();
		EasyMock.expect(mockedType.getSuperclassName()).andReturn(null).anyTimes();
		EasyMock.expect(mockedType.getSuperInterfaceNames()).andReturn(new HashSet<String>()).anyTimes();
		EasyMock.expect(mockedType.getTypes()).andReturn(new HashSet<IType>()).anyTimes();
	}

	@Test
	public void caseAnnotationType() {
		fail("TODO");
		ITypeWrapper mockedType = EasyMock.createMock(ITypeWrapper.class);
		visitorHandler.setCurrentComponent(UMLFactory.eINSTANCE.createComponent());
		visitorHandler.setCurrentPackage(UMLFactory.eINSTANCE.createPackage());
		try {
			initMocks(mockedType);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(true).anyTimes();
			EasyMock.expect(mockedType.isClass()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isInterface()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isEnum()).andReturn(false).anyTimes();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseEnumType() {
		ITypeWrapper mockedType = EasyMock.createMock(ITypeWrapper.class);
		visitorHandler.setCurrentComponent(UMLFactory.eINSTANCE.createComponent());
		visitorHandler.setCurrentPackage(UMLFactory.eINSTANCE.createPackage());
		try {
			initMocks(mockedType);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isClass()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isInterface()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isEnum()).andReturn(true).anyTimes();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseInterfaceType() {
		ITypeWrapper mockedType = EasyMock.createMock(ITypeWrapper.class);
		visitorHandler.setCurrentComponent(UMLFactory.eINSTANCE.createComponent());
		visitorHandler.setCurrentPackage(UMLFactory.eINSTANCE.createPackage());
		try {
			initMocks(mockedType);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isClass()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isInterface()).andReturn(true).anyTimes();
			EasyMock.expect(mockedType.isEnum()).andReturn(false).anyTimes();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseClassType() {
		ITypeWrapper mockedType = EasyMock.createMock(ITypeWrapper.class);
		visitorHandler.setCurrentComponent(UMLFactory.eINSTANCE.createComponent());
		visitorHandler.setCurrentPackage(UMLFactory.eINSTANCE.createPackage());
		try {
			initMocks(mockedType);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isClass()).andReturn(true).anyTimes();
			EasyMock.expect(mockedType.isInterface()).andReturn(false).anyTimes();
			EasyMock.expect(mockedType.isEnum()).andReturn(false).anyTimes();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}
}
