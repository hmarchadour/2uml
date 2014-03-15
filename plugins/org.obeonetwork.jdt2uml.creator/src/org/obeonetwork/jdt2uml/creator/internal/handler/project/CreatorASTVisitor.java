package org.obeonetwork.jdt2uml.creator.internal.handler.project;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Package;
import org.obeonetwork.jdt2uml.core.internal.visitor.BasicASTVisitor;

public class CreatorASTVisitor extends BasicASTVisitor {

	private Component currentComponent;

	private Package currentPackage;

	public CreatorASTVisitor(Component currentComponent) {
		this.currentComponent = currentComponent;
	}

	public CreatorASTVisitor(Package currentPackage) {
		this.currentPackage = currentPackage;
	}

	@Override
	public boolean visit(MemberRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

}
