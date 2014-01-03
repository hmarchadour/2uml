package org.obeonetwork.jdt2uml.core.tests.unit.visitor;

import java.util.Collection;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitorHandler;

@RunWith(Parameterized.class)
public class TypeJDTVisitorHandler {

	private static String FULL_QUALIFIED_NAME = "java.lang.Object";

	private JDTVisitorHandler visitorHandler;

	private JDTVisitor mockedVisitor;

	private String testCaseName;

	private Boolean isExternal;

	@Before
	public void setUp() throws Exception {
		visitorHandler = Factory.createJDTProjectVisitorHandler(null, null);
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

	private void initMocks(IType mockedType, IPackageFragment packageFragment,
			IPackageFragmentRoot packageFragmentRoot) throws JavaModelException {
		EasyMock.expect(mockedType.getFullyQualifiedName()).andReturn(FULL_QUALIFIED_NAME);
		EasyMock.expect(mockedType.getFullyQualifiedName()).andReturn(FULL_QUALIFIED_NAME);
		EasyMock.expect(mockedType.getFullyQualifiedName()).andReturn(FULL_QUALIFIED_NAME);
		EasyMock.expect(mockedType.getTypes()).andReturn(new IType[0]);
		EasyMock.expect(mockedType.getSuperclassName()).andReturn(null);
		EasyMock.expect(mockedType.getSuperInterfaceNames()).andReturn(new String[0]);
		EasyMock.expect(mockedType.getPackageFragment()).andReturn(packageFragment);
		EasyMock.expect(packageFragment.getParent()).andReturn(packageFragmentRoot);
		EasyMock.expect(packageFragmentRoot.isExternal()).andReturn(isExternal);
		EasyMock.expect(mockedType.getChildren()).andReturn(new IJavaElement[0]);
	}

	@Test
	public void caseAnnotationType() {
		IType mockedType = EasyMock.createMock(IType.class);
		IPackageFragment packageFragment = EasyMock.createMock(IPackageFragment.class);
		IPackageFragmentRoot packageFragmentRoot = EasyMock.createMock(IPackageFragmentRoot.class);

		try {
			initMocks(mockedType, packageFragment, packageFragmentRoot);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(true);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseEnumType() {
		IType mockedType = EasyMock.createMock(IType.class);
		IPackageFragment packageFragment = EasyMock.createMock(IPackageFragment.class);
		IPackageFragmentRoot packageFragmentRoot = EasyMock.createMock(IPackageFragmentRoot.class);

		try {
			initMocks(mockedType, packageFragment, packageFragmentRoot);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false);
			EasyMock.expect(mockedType.isEnum()).andReturn(true);
			EasyMock.expect(mockedType.isEnum()).andReturn(true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseInterfaceType() {
		IType mockedType = EasyMock.createMock(IType.class);
		IPackageFragment packageFragment = EasyMock.createMock(IPackageFragment.class);
		IPackageFragmentRoot packageFragmentRoot = EasyMock.createMock(IPackageFragmentRoot.class);

		try {
			initMocks(mockedType, packageFragment, packageFragmentRoot);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false);
			EasyMock.expect(mockedType.isEnum()).andReturn(false);
			EasyMock.expect(mockedType.isInterface()).andReturn(true);
			EasyMock.expect(mockedType.isInterface()).andReturn(true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}

	@Test
	public void caseClassType() {
		IType mockedType = EasyMock.createMock(IType.class);
		IPackageFragment packageFragment = EasyMock.createMock(IPackageFragment.class);
		IPackageFragmentRoot packageFragmentRoot = EasyMock.createMock(IPackageFragmentRoot.class);

		try {
			initMocks(mockedType, packageFragment, packageFragmentRoot);
			EasyMock.expect(mockedType.isAnnotation()).andReturn(false);
			EasyMock.expect(mockedType.isEnum()).andReturn(false);
			EasyMock.expect(mockedType.isInterface()).andReturn(false);
			EasyMock.expect(mockedType.isClass()).andReturn(true);
			EasyMock.expect(mockedType.isClass()).andReturn(true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);
		visitorHandler.caseType(mockedType, mockedVisitor);
		EasyMock.verify(packageFragment, packageFragmentRoot, mockedType, mockedVisitor);

		if (isExternal) {
		} else {
		}
	}
}
