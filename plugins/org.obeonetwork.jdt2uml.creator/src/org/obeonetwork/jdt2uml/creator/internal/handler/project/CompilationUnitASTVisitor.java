package org.obeonetwork.jdt2uml.creator.internal.handler.project;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.creator.CreatorActivator;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.AsyncHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.FieldDeclarationHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.MethodDeclarationHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.SuperInterfaceTypeHandler;
import org.obeonetwork.jdt2uml.creator.internal.handler.async.SuperTypeHandler;

public class CompilationUnitASTVisitor extends ASTVisitor {

	private Package currentPackage;

	private Classifier currentClassifier;

	private Set<AsyncHandler> handlersToRelaunch;

	public CompilationUnitASTVisitor(Package currentPackage) {
		this.currentPackage = currentPackage;
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
			AsyncHandler handler = new FieldDeclarationHandler(currentClassifier, fieldDeclaration);
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
			AsyncHandler handler = new MethodDeclarationHandler(currentClassifier, methodDeclaration);
			tryTo(handler);
		}
		return super.visit(methodDeclaration);
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
					AsyncHandler handler = new SuperTypeHandler(currentClassifier, superclassType);
					tryTo(handler);
				}

				if (currentClassifier instanceof BehavioredClassifier) {
					List superInterfaceTypes = typeDeclaration.superInterfaceTypes();
					for (Object object : superInterfaceTypes) {
						if (object != null && object instanceof Type) {
							Type superInterfaceType = (Type)object;
							AsyncHandler handler = new SuperInterfaceTypeHandler(
									(BehavioredClassifier)currentClassifier, superInterfaceType);
							tryTo(handler);
						}
					}
				}
			}
		}
		return super.visit(typeDeclaration);
	}

}
