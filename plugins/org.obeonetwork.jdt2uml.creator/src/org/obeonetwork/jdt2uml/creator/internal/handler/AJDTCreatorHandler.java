package org.obeonetwork.jdt2uml.creator.internal.handler;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.handler.BasicJDTHandler;
import org.obeonetwork.jdt2uml.core.api.visitor.JModelVisitor;
import org.obeonetwork.jdt2uml.creator.api.handler.JDTCreatorHandler;

public abstract class AJDTCreatorHandler extends BasicJDTHandler implements JDTCreatorHandler {

	private final Model model;

	public AJDTCreatorHandler(IProgressMonitor monitor) {
		super(monitor);
		this.model = UMLFactory.eINSTANCE.createModel();
	}

	@Override
	public void caseField(IField field, JModelVisitor visitor) {
		casePre(field, visitor);

		Set<String> qualifiedNames = Utils.getQualifiedNames(field);
		for (String qualifiedName : qualifiedNames) {
			Set<IType> retrieveTypes = Utils.retrieveTypes(field.getJavaProject(), qualifiedName);
			for (IType type : retrieveTypes) {
				visitor.visit(type);
			}
		}

		casePost(field, visitor);
	}

	@Override
	public void caseMethod(IMethod method, JModelVisitor visitor) {
		casePre(method, visitor);

		Set<String> qualifiedNames = Utils.getQualifiedNames(method);
		for (String qualifiedName : qualifiedNames) {
			Set<IType> retrieveTypes = Utils.retrieveTypes(method.getJavaProject(), qualifiedName);
			for (IType type : retrieveTypes) {
				visitor.visit(type);
			}
		}

		casePost(method, visitor);
	}

	@Override
	public Model getModel() {
		return model;
	}
}
