package org.obeonetwork.jdt2uml.creator.internal.handler.async;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;
import org.obeonetwork.jdt2uml.core.api.DomTypeResolver;

public final class FieldDeclarationHandler extends AbstractAsyncHandler {

	protected FieldDeclaration fieldDeclaration;

	protected DomTypeResolver typesResolver;

	protected String fieldName;

	public FieldDeclarationHandler(Classifier currentClassifier, FieldDeclaration fieldDeclaration) {
		super(currentClassifier);

		this.fieldDeclaration = fieldDeclaration;
		this.typesResolver = new DomTypeResolver(currentClassifier, fieldDeclaration.getType());

		// hack to get the field name
		Object o = fieldDeclaration.fragments().get(0);
		if (o instanceof VariableDeclarationFragment) {
			this.fieldName = ((VariableDeclarationFragment)o).getName().toString();
		} else {
			this.fieldName = o.toString();
		}
	}

	public boolean isHandleable() {
		boolean isResolved = typesResolver.isResolved();
		if (!isResolved) {
			isResolved = typesResolver.tryToResolve();
		}
		return isResolved;
	}

	public void handle() {

		if (isHandleable()) {
			String name = fieldName;
			Type umlType = typesResolver.getRootClassifier();

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
			} else {
				throw new IllegalStateException("Should not appended");
			}

			attribute.setVisibility(VisibilityKind.PACKAGE_LITERAL); // Default
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
