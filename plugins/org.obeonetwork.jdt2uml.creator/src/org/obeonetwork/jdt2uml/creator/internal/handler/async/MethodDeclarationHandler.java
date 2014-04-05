package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;

public final class MethodDeclarationHandler extends AbstractAsyncHandler {

	protected MethodDeclaration methodDeclaration;

	protected DomTypeResolver returnTypesResolver;

	protected List<DomTypeResolver> argsTypesResolvers;

	protected List<SingleVariableDeclaration> args;

	public MethodDeclarationHandler(Classifier currentClassifier, MethodDeclaration methodDeclaration) {
		super(currentClassifier);
		this.methodDeclaration = methodDeclaration;
		org.eclipse.jdt.core.dom.Type returnType = methodDeclaration.getReturnType2();
		if (returnType != null) {
			this.returnTypesResolver = new DomTypeResolver(currentClassifier, returnType);
		}
		this.argsTypesResolvers = new ArrayList<DomTypeResolver>();
		this.args = new ArrayList<SingleVariableDeclaration>();

		List parameters = methodDeclaration.parameters();
		for (Object object : parameters) {
			if (object instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration)object;
				this.args.add(variableDeclaration);
				org.eclipse.jdt.core.dom.Type argType = variableDeclaration.getType();
				if (argType == null) {
					throw new IllegalStateException("Should not appended");
				}
				argsTypesResolvers.add(new DomTypeResolver(currentClassifier, argType));
			}
		}
	}

	public boolean isHandleable() {
		boolean isResolved = true;
		if (returnTypesResolver != null) {
			isResolved = returnTypesResolver.isResolved();
			if (!isResolved) {
				isResolved = returnTypesResolver.tryToResolve();
			}
		}
		if (isResolved) {
			for (DomTypeResolver typesResolver : argsTypesResolvers) {
				boolean resolved = typesResolver.isResolved();
				if (!resolved) {
					resolved = typesResolver.tryToResolve();
				}
				isResolved &= resolved;
			}
		}
		return isResolved;
	}

	public void handle() {

		if (isHandleable()) {

			Operation operation;
			if (currentClassifier instanceof Interface) {
				operation = ((Interface)currentClassifier).createOwnedOperation(methodDeclaration.getName()
						.getIdentifier(), null, null);
			} else if (currentClassifier instanceof org.eclipse.uml2.uml.Class) {
				operation = ((org.eclipse.uml2.uml.Class)currentClassifier).createOwnedOperation(
						methodDeclaration.getName().getIdentifier(), null, null);
			} else {
				throw new IllegalStateException("Should not appended");
			}

			if (returnTypesResolver == null) {
				// Constructor
			} else {
				Type umlType = returnTypesResolver.getRootClassifier();
				if (umlType != null) {
					operation.createReturnResult("return", umlType);
				} // else void
			}
			for (int i = 0; i < args.size(); i++) {
				SingleVariableDeclaration arg = args.get(i);
				DomTypeResolver typesResolver = argsTypesResolvers.get(i);

				Type umlArgType = typesResolver.getRootClassifier();
				if (umlArgType == null) {
					throw new IllegalStateException("Should not appended");
				}

				operation.createOwnedParameter(arg.getName().getIdentifier(), umlArgType);
			}

			operation.setVisibility(VisibilityKind.PACKAGE_LITERAL); // Default
			List modifiers = methodDeclaration.modifiers();
			for (Object object : modifiers) {
				if (object instanceof Modifier) {
					Modifier modifier = (Modifier)object;
					if (modifier.isStatic()) {
						operation.setIsStatic(true);
					}

					if (modifier.isPrivate()) {
						operation.setVisibility(VisibilityKind.PRIVATE_LITERAL);
					} else if (modifier.isProtected()) {
						operation.setVisibility(VisibilityKind.PROTECTED_LITERAL);
					} else if (modifier.isPublic()) {
						operation.setVisibility(VisibilityKind.PUBLIC_LITERAL);
					}
				}
			}
			handled = true;
		}
	}
}
