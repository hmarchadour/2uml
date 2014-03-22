package org.obeonetwork.jdt2uml.creator.internal.handler.project;

import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.internal.visitor.BasicASTVisitor;

public class CreatorASTVisitor extends BasicASTVisitor {

	private Package currentPackage;

	public CreatorASTVisitor(Package currentPackage) {
		this.currentPackage = currentPackage;
	}

	@Override
	public boolean visit(MemberRef node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (!node.isLocalTypeDeclaration()) {
			if (!node.isMemberTypeDeclaration()) {
				Classifier currentClassifier;
				if (node.isInterface()) {
					currentClassifier = UMLFactory.eINSTANCE.createInterface();
				} else {
					currentClassifier = UMLFactory.eINSTANCE.createClass();
				}
				currentClassifier.setName(node.getName().getIdentifier());
				if (currentPackage != null) {
					if (currentPackage.getPackagedElement(currentClassifier.getName()) == null) {
						currentPackage.getPackagedElements().add(currentClassifier);
					}
				}
			}
		}
		return super.visit(node);
	}

}
