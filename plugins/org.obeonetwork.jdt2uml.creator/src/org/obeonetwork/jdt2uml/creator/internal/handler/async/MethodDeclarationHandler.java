package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public final class MethodDeclarationHandler extends AbstractAsyncHandler {

	protected MethodDeclaration methodDeclaration;

	protected Resolver typesResolver;

	protected ResolverResult latestReturnResult;

	protected Map<SingleVariableDeclaration, ResolverResult> latestArgResults;

	public MethodDeclarationHandler(Classifier currentClassifier, MethodDeclaration methodDeclaration,
			Resolver typesResolver) {
		super(currentClassifier);

		this.methodDeclaration = methodDeclaration;
		this.typesResolver = typesResolver;
		this.latestArgResults = new LinkedHashMap<SingleVariableDeclaration, ResolverResult>();

		@SuppressWarnings("rawtypes")
		List parameters = methodDeclaration.parameters();
		for (Object object : parameters) {
			if (object instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration)object;
				this.latestArgResults.put(variableDeclaration, null);
			}
		}
	}

	public boolean isHandleable() {
		boolean isHandleable = true;

		org.eclipse.jdt.core.dom.Type returnType = methodDeclaration.getReturnType2();
		if (returnType != null) {
			if (latestReturnResult == null || !latestReturnResult.isResolved()) {
				latestReturnResult = typesResolver.resolve(returnType);
			}
			isHandleable = latestReturnResult.isResolved();
		}

		if (isHandleable) {
			for (SingleVariableDeclaration arg : latestArgResults.keySet()) {
				ResolverResult latestArgResult = latestArgResults.get(arg);
				if (latestArgResult == null || !latestArgResult.isResolved()) {
					latestArgResult = typesResolver.resolve(arg.getType());
					latestArgResults.put(arg, latestArgResult);
					if (!latestArgResult.isResolved()) {
						isHandleable = false;
						break;
					}
				}
			}
		}
		return isHandleable;
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {

			Operation operation;
			if (currentClassifier instanceof Interface) {
				operation = ((Interface)currentClassifier).createOwnedOperation(methodDeclaration.getName()
						.getIdentifier(), null, null);
			} else if (currentClassifier instanceof org.eclipse.uml2.uml.Class) {
				operation = ((org.eclipse.uml2.uml.Class)currentClassifier).createOwnedOperation(
						methodDeclaration.getName().getIdentifier(), null, null);
			} else if (currentClassifier instanceof Enumeration) {
				operation = ((Enumeration)currentClassifier).createOwnedOperation(methodDeclaration.getName()
						.getIdentifier(), null, null);
			} else {
				throw new IllegalStateException("Should not appended");
			}

			if (latestReturnResult == null) {
				// Constructor
			} else {
				Type umlType = latestReturnResult.getRootClassifier();
				if (umlType != null) {
					Parameter returnParam = operation.createReturnResult("return", umlType);
					// Todo handle Generic types
				} // else void
			}
			for (SingleVariableDeclaration arg : latestArgResults.keySet()) {
				ResolverResult latestArgResult = latestArgResults.get(arg);

				Type umlArgType = latestArgResult.getRootClassifier();

				if (umlArgType == null) {
					throw new IllegalStateException("Should not appended");
				}
				Parameter parameter = operation.createOwnedParameter(arg.getName().getIdentifier(),
						umlArgType);
				// Generic types part
				if (umlArgType instanceof Classifier
						&& ((Classifier)umlArgType).getOwnedTemplateSignature() != null) {
					// Todo handle Generic types
				}

			}

			if (currentClassifier instanceof Interface) {
				operation.setVisibility(VisibilityKind.PUBLIC_LITERAL); // Default
			} else {
				operation.setVisibility(VisibilityKind.PACKAGE_LITERAL); // Default
			}

			@SuppressWarnings("rawtypes")
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
