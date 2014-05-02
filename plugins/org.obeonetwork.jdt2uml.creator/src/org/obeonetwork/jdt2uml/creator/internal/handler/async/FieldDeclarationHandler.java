package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public final class FieldDeclarationHandler extends AbstractAsyncHandler {

	protected FieldDeclaration fieldDeclaration;

	protected Resolver typesResolver;

	protected String fieldName;

	protected ResolverResult latestResult;

	public FieldDeclarationHandler(Classifier currentClassifier, FieldDeclaration fieldDeclaration,
			Resolver typesResolver) {
		super(currentClassifier);

		this.fieldDeclaration = fieldDeclaration;
		this.typesResolver = typesResolver;

		// hack to get the field name
		Object o = fieldDeclaration.fragments().get(0);
		if (o instanceof VariableDeclarationFragment) {
			this.fieldName = ((VariableDeclarationFragment)o).getName().toString();
		} else {
			this.fieldName = o.toString();
		}
	}

	public boolean isHandleable() {
		if (latestResult == null || !latestResult.isResolved()) {
			latestResult = typesResolver.resolve(fieldDeclaration.getType());
		}
		return latestResult.isResolved();
	}

	public void handle() {

		if (isHandleable() && !isHandled()) {
			String name = fieldName;
			Type umlType = latestResult.getRootClassifier();

			if (umlType == null) {
				throw new IllegalStateException("Should not appended");
			}

			int lower = 0;
			int upper = 1;
			if (fieldDeclaration.getType().isArrayType()) {
				upper = -1;
			}

			Property attribute;

			if (currentClassifier instanceof Interface) {
				attribute = ((Interface)currentClassifier).createOwnedAttribute(name, umlType, lower, upper);
			} else if (currentClassifier instanceof org.eclipse.uml2.uml.Class) {
				attribute = ((org.eclipse.uml2.uml.Class)currentClassifier).createOwnedAttribute(name,
						umlType, lower, upper);
			} else if (currentClassifier instanceof Enumeration) {
				attribute = ((Enumeration)currentClassifier)
						.createOwnedAttribute(name, umlType, lower, upper);
			} else {
				throw new IllegalStateException("Should not appended");
			}

			if (currentClassifier instanceof Interface) {
				attribute.setVisibility(VisibilityKind.PUBLIC_LITERAL); // Default
			} else {
				attribute.setVisibility(VisibilityKind.PACKAGE_LITERAL); // Default
			}

			if (currentClassifier.getOwnedTemplateSignature() != null) {
				// Todo handle Generic types
			}

			@SuppressWarnings("rawtypes")
			List modifiers = fieldDeclaration.modifiers();
			for (Object object : modifiers) {
				if (object instanceof Modifier) {
					Modifier modifier = (Modifier)object;
					if (modifier.isStatic()) {
						attribute.setIsStatic(true);
					}

					if (modifier.isFinal()) {
						// TODO
					}

					if (modifier.isPrivate()) {
						attribute.setVisibility(VisibilityKind.PRIVATE_LITERAL);
					} else if (modifier.isProtected()) {
						attribute.setVisibility(VisibilityKind.PROTECTED_LITERAL);
					} else if (modifier.isPublic()) {
						attribute.setVisibility(VisibilityKind.PUBLIC_LITERAL);
					}
				}
			}
			handled = true;
		}
	}
}
