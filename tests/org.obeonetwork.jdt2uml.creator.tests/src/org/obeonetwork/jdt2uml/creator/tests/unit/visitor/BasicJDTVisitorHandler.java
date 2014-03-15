package org.obeonetwork.jdt2uml.creator.tests.unit.visitor;

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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.creator.api.CreatorFactory;
import org.obeonetwork.jdt2uml.creator.api.handler.JDTCreatorHandler;

public class BasicJDTVisitorHandler {

	private JDTCreatorHandler visitorHandler;

	private JModelVisitor mockedVisitor;

	@Before
	public void setUp() throws Exception {
		visitorHandler = CreatorFactory.createJDTProjectVisitorHandler(null);
		mockedVisitor = EasyMock.createMock(JModelVisitor.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void caseJavaProject() {
		IJavaProject mockedJavaProject = EasyMock.createMock(IJavaProject.class);
		try {
			EasyMock.expect(mockedJavaProject.getElementName()).andReturn("JavaProject");
			EasyMock.expect(mockedJavaProject.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		EasyMock.replay(mockedJavaProject, mockedVisitor);
		visitorHandler.caseJavaProject(mockedJavaProject, mockedVisitor);
		EasyMock.verify(mockedJavaProject, mockedVisitor);
	}

	@Test
	public void caseJavaModel() {
		IJavaModel mockedJavaModel = EasyMock.createMock(IJavaModel.class);
		try {
			EasyMock.expect(mockedJavaModel.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		EasyMock.replay(mockedJavaModel, mockedVisitor);
		visitorHandler.caseJavaModel(mockedJavaModel, mockedVisitor);
		EasyMock.verify(mockedJavaModel, mockedVisitor);

	}

	@Test
	public void caseAnnotation() {
		IAnnotation mockedAnnotation = EasyMock.createMock(IAnnotation.class);
		EasyMock.replay(mockedAnnotation, mockedVisitor);
		visitorHandler.caseAnnotation(mockedAnnotation, mockedVisitor);
		EasyMock.verify(mockedAnnotation, mockedVisitor);

	}

	@Test
	public void caseClassFile() {
		IClassFile mockedClassFile = EasyMock.createMock(IClassFile.class);
		try {
			EasyMock.expect(mockedClassFile.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		EasyMock.expect(mockedClassFile.getType()).andReturn(null);

		EasyMock.replay(mockedClassFile, mockedVisitor);
		visitorHandler.caseClassFile(mockedClassFile, mockedVisitor);
		EasyMock.verify(mockedClassFile, mockedVisitor);

	}

	@Test
	public void caseCompilationUnit() {
		ICompilationUnit mockedCompilationUnit = EasyMock.createMock(ICompilationUnit.class);
		try {
			EasyMock.expect(mockedCompilationUnit.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		EasyMock.expect(mockedCompilationUnit.findPrimaryType()).andReturn(null);
		EasyMock.replay(mockedCompilationUnit, mockedVisitor);
		visitorHandler.caseCompilationUnit(mockedCompilationUnit, mockedVisitor);
		EasyMock.verify(mockedCompilationUnit, mockedVisitor);

	}

	@Test
	public void caseField() {
		IField mockedField = EasyMock.createMock(IField.class);
		IType mDeclaringType = EasyMock.createMock(IType.class);
		IJavaProject mJavaProject = EasyMock.createMock(IJavaProject.class);
		IType returnType = EasyMock.createMock(IType.class);
		try {
			EasyMock.expect(mockedField.getTypeSignature()).andReturn("QObject;");

			EasyMock.expect(mockedField.getDeclaringType()).andReturn(mDeclaringType);
			EasyMock.expect(mDeclaringType.resolveType("Object")).andReturn(
					new String[][] {new String[] {"java", "lang", "Object"}});
			EasyMock.expect(mockedField.getJavaProject()).andReturn(mJavaProject);
			EasyMock.expect(mJavaProject.findType("java.lang.Object")).andReturn(returnType);
			EasyMock.expect(mockedField.getChildren()).andReturn(new IJavaElement[0]);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		mockedVisitor.visit(returnType);

		EasyMock.replay(returnType, mJavaProject, mDeclaringType, mockedField, mockedVisitor);
		visitorHandler.caseField(mockedField, mockedVisitor);
		EasyMock.verify(returnType, mJavaProject, mDeclaringType, mockedField, mockedVisitor);

	}

	@Test
	public void caseInitializer() {
		IInitializer mockedInitializer = EasyMock.createMock(IInitializer.class);
		try {
			EasyMock.expect(mockedInitializer.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		EasyMock.replay(mockedInitializer, mockedVisitor);
		visitorHandler.caseInitializer(mockedInitializer, mockedVisitor);
		EasyMock.verify(mockedInitializer, mockedVisitor);

	}

	@Test
	public void caseMethod() {
		IMethod mMethod = EasyMock.createMock(IMethod.class);
		IType mDeclaringType = EasyMock.createMock(IType.class);
		IJavaProject mJavaProject = EasyMock.createMock(IJavaProject.class);
		IType returnType = EasyMock.createMock(IType.class);

		try {
			EasyMock.expect(mMethod.getDeclaringType()).andReturn(mDeclaringType);
			EasyMock.expect(mDeclaringType.resolveType("Object")).andReturn(
					new String[][] {new String[] {"java", "lang", "Object"}});
			EasyMock.expect(mMethod.getReturnType()).andReturn("QObject;");
			EasyMock.expect(mMethod.getParameterTypes()).andReturn(new String[0]);
			EasyMock.expect(mMethod.getJavaProject()).andReturn(mJavaProject);
			EasyMock.expect(mMethod.getChildren()).andReturn(new IJavaElement[0]);

			EasyMock.expect(mJavaProject.findType("java.lang.Object")).andReturn(returnType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		mockedVisitor.visit(returnType);

		EasyMock.replay(mJavaProject, mDeclaringType, mMethod, mockedVisitor);
		visitorHandler.caseMethod(mMethod, mockedVisitor);
		EasyMock.verify(mDeclaringType, mMethod, mockedVisitor);

	}

	@Test
	public void caseImportContainer() {
		IImportContainer mockedImportContainer = EasyMock.createMock(IImportContainer.class);
		try {
			EasyMock.expect(mockedImportContainer.getChildren()).andReturn(new IJavaElement[0]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		EasyMock.replay(mockedImportContainer, mockedVisitor);
		visitorHandler.caseImportContainer(mockedImportContainer, mockedVisitor);
		EasyMock.verify(mockedImportContainer, mockedVisitor);

	}

	@Test
	public void casePackageDeclaration() {
		IPackageDeclaration mockedPackageDeclaration = EasyMock.createMock(IPackageDeclaration.class);
		EasyMock.replay(mockedPackageDeclaration, mockedVisitor);
		visitorHandler.casePackageDeclaration(mockedPackageDeclaration, mockedVisitor);
		EasyMock.verify(mockedPackageDeclaration, mockedVisitor);

	}

	@Test
	public void caseTypeParameter() {
		ITypeParameter mockedTypeParameter = EasyMock.createMock(ITypeParameter.class);
		EasyMock.replay(mockedTypeParameter, mockedVisitor);
		visitorHandler.caseTypeParameter(mockedTypeParameter, mockedVisitor);
		EasyMock.verify(mockedTypeParameter, mockedVisitor);

	}

	@Test
	public void caseLocalVariable() {
		ILocalVariable mockedLocalVariable = EasyMock.createMock(ILocalVariable.class);
		EasyMock.replay(mockedLocalVariable, mockedVisitor);
		visitorHandler.caseLocalVariable(mockedLocalVariable, mockedVisitor);
		EasyMock.verify(mockedLocalVariable, mockedVisitor);

	}

	@Test
	public void caseImportDeclaration() {
		IImportDeclaration mockedImportDeclaration = EasyMock.createMock(IImportDeclaration.class);
		EasyMock.replay(mockedImportDeclaration, mockedVisitor);
		visitorHandler.caseImportDeclaration(mockedImportDeclaration, mockedVisitor);
		EasyMock.verify(mockedImportDeclaration, mockedVisitor);

	}

}
