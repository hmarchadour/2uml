package org.obeonetwork.jdt2uml.creator.internal.visitor.project;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.handler.AsyncHandler;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.FieldDeclarationHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.MethodDeclarationHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.SuperInterfaceTypeHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.SuperTypeHandler;

public class CompilationUnitASTVisitor extends ASTVisitor {

	private Package currentPackage;

	private Classifier currentClassifier;

	private Set<AsyncHandler> handlersToRelaunch;

	private Set<LazyHandler> lazyHandlers;

	public CompilationUnitASTVisitor(Package currentPackage, Set<LazyHandler> lazyHandlers) {
		this.currentPackage = currentPackage;
		this.lazyHandlers = lazyHandlers;
		this.handlersToRelaunch = new LinkedHashSet<AsyncHandler>();
	}

	public Set<AsyncHandler> getHandlers() {
		return handlersToRelaunch;
	}

	protected void tryTo(AsyncHandler handler) {
		if (handler.isHandleable()) {
			handler.handle();
		} else {
			handlersToRelaunch.add(handler);
		}
	}

	@Override
	public boolean visit(MemberRef memberRef) {
		if (currentClassifier == null) {
			CreatorActivator.log(IStatus.WARNING,
					"Visit a memberRef whithout currentClassifier should not appended");
		}
		return super.visit(memberRef);
	}

	@Override
	public boolean visit(FieldDeclaration fieldDeclaration) {
		if (currentClassifier == null) {
			CreatorActivator.log(IStatus.WARNING,
					"Visit a fieldDeclaration whithout currentClassifier should not appended");
		} else {
			AsyncHandler handler = new FieldDeclarationHandler(currentClassifier, fieldDeclaration,
					lazyHandlers);
			tryTo(handler);
		}
		return super.visit(fieldDeclaration);
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		if (currentClassifier == null) {
			CreatorActivator.log(IStatus.WARNING,
					"Visit a methodDeclaration whithout currentClassifier should not appended");
		} else {
			AsyncHandler handler = new MethodDeclarationHandler(currentClassifier, methodDeclaration,
					lazyHandlers);
			tryTo(handler);
		}
		return super.visit(methodDeclaration);
	}

	@Override
	public boolean visit(EnumDeclaration enumDeclaration) {

		if (enumDeclaration.isPackageMemberTypeDeclaration()) {
			if (currentPackage != null) {
				String identifier = enumDeclaration.getName().getIdentifier();
				if (currentPackage.getPackagedElement(identifier) == null) {
					Enumeration enumeration = currentPackage.createOwnedEnumeration(identifier);
					currentClassifier = enumeration;
					@SuppressWarnings("rawtypes")
					List enumConstants = enumDeclaration.enumConstants();
					for (Object object : enumConstants) {
						if (object instanceof EnumConstantDeclaration) {
							EnumConstantDeclaration enumConstantDeclaration = (EnumConstantDeclaration)object;
							enumeration.createOwnedLiteral(enumConstantDeclaration.getName().getIdentifier());
						}
					}
				}
			}
		}
		return super.visit(enumDeclaration);
	}

	@Override
	public boolean visit(TypeDeclaration typeDeclaration) {
		if (!typeDeclaration.isLocalTypeDeclaration()) {
			if (!typeDeclaration.isMemberTypeDeclaration()) {
				if (typeDeclaration.isInterface()) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else {
					currentClassifier = UMLFactory.eINSTANCE.createClass();
				}
				currentClassifier.setName(typeDeclaration.getName().getIdentifier());
				if (currentPackage != null) {
					if (currentPackage.getPackagedElement(currentClassifier.getName()) == null) {
						currentPackage.getPackagedElements().add(currentClassifier);
					}
				}

				Type superclassType = typeDeclaration.getSuperclassType();
				if (superclassType != null) {
					AsyncHandler handler = new SuperTypeHandler(currentClassifier, superclassType,
							lazyHandlers);
					tryTo(handler);
				}

				if (currentClassifier instanceof BehavioredClassifier) {
					@SuppressWarnings("rawtypes")
					List superInterfaceTypes = typeDeclaration.superInterfaceTypes();
					for (Object object : superInterfaceTypes) {
						if (object != null && object instanceof Type) {
							Type superInterfaceType = (Type)object;
							AsyncHandler handler = new SuperInterfaceTypeHandler(
									(BehavioredClassifier)currentClassifier, superInterfaceType, lazyHandlers);
							tryTo(handler);
						}
					}
				}
			}
		}
		return super.visit(typeDeclaration);
	}

}
