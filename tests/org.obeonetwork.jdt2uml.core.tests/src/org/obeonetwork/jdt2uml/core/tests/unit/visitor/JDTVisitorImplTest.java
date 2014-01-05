package org.obeonetwork.jdt2uml.core.tests.unit.visitor;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.obeonetwork.jdt2uml.core.api.Factory;
import org.obeonetwork.jdt2uml.core.api.handler.JDTCreatorHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.JDTVisitor;

@RunWith(Parameterized.class)
public class JDTVisitorImplTest {

	private JDTCreatorHandler mockedHandler;

	private JDTVisitor jdtVisitor;

	private String testCaseName;

	private Class<?> javaItemClass;

	@Before
	public void setUp() throws Exception {
		mockedHandler = EasyMock.createMock(JDTCreatorHandler.class);
		jdtVisitor = Factory.createJDTVisitor(mockedHandler);
	}

	public JDTVisitorImplTest(String testCaseName, Class<?> javaItemClass) {
		this.testCaseName = testCaseName;
		this.javaItemClass = javaItemClass;
	}

	@Parameters(name = "#{index} {0}")
	public static Collection<Object[]> params() {

		HashSet<Object[]> params = new HashSet<Object[]>();

		Class<?>[] javaItemClasses = new Class<?>[] {ICompilationUnit.class, IAnnotation.class,
				IImportContainer.class, IImportDeclaration.class, IPackageFragment.class,
				IPackageFragmentRoot.class, ITypeParameter.class, IClassFile.class, IJavaModel.class,
				IJavaProject.class, ILocalVariable.class, IField.class, IInitializer.class, IMethod.class,
				IType.class, IPackageDeclaration.class};

		for (Class<?> javaItemClass : javaItemClasses) {
			String testCaseName = javaItemClass.getSimpleName();
			params.add(new Object[] {testCaseName, javaItemClass});
		}

		return params;
	}

	/*
	 * We expect an AssertionError on the EasyMock.verify(mockedHandler). In deed, we have not mocked the
	 * handler call
	 */
	@Test(expected = AssertionError.class)
	public void failOnVerify() {
		IJavaElement mockedJavaElem = (IJavaElement)EasyMock.createMock(javaItemClass);
		EasyMock.replay(mockedHandler);
		jdtVisitor.visit(mockedJavaElem);
		EasyMock.verify(mockedHandler);
	}

	@Test
	public void visitMock() {
		IJavaElement mockedJavaElem = (IJavaElement)EasyMock.createMock(javaItemClass);

		if (mockedJavaElem instanceof IJavaProject) {
			mockedHandler.caseJavaProject((IJavaProject)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IJavaModel) {
			mockedHandler.caseJavaModel((IJavaModel)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IPackageFragmentRoot) {
			mockedHandler.casePackageFragmentRoot((IPackageFragmentRoot)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IPackageFragment) {
			mockedHandler.casePackageFragment((IPackageFragment)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IClassFile) {
			mockedHandler.caseClassFile((IClassFile)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof ICompilationUnit) {
			mockedHandler.caseCompilationUnit((ICompilationUnit)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IField) {
			mockedHandler.caseField((IField)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IMethod) {
			mockedHandler.caseMethod((IMethod)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IInitializer) {
			mockedHandler.caseInitializer((IInitializer)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IType) {
			mockedHandler.caseType((IType)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IAnnotation) {
			mockedHandler.caseAnnotation((IAnnotation)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IImportContainer) {
			mockedHandler.caseImportContainer((IImportContainer)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IImportDeclaration) {
			mockedHandler.caseImportDeclaration((IImportDeclaration)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof IPackageDeclaration) {
			mockedHandler.casePackageDeclaration((IPackageDeclaration)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof ITypeParameter) {
			mockedHandler.caseTypeParameter((ITypeParameter)mockedJavaElem, jdtVisitor);
		} else if (mockedJavaElem instanceof ILocalVariable) {
			mockedHandler.caseLocalVariable((ILocalVariable)mockedJavaElem, jdtVisitor);
		} else {
			fail();
		}
		EasyMock.replay(mockedHandler, mockedJavaElem);
		jdtVisitor.visit(mockedJavaElem);
		EasyMock.verify(mockedHandler);
	}
}
