package org.obeonetwork.jar2uml.core.internal.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class VisitClassDependencies extends Job {

	private ClassStore classesToVisit;

	private ClassStore externalClasses;

	public VisitClassDependencies(ClassStore classesToVisit) {
		super(VisitClassDependencies.class.getSimpleName());
		this.classesToVisit = classesToVisit;
		this.externalClasses = Factory.createClassStore();
	}

	public ClassStore getExternalClasses() {
		return externalClasses;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		monitor.beginTask("Visit classes", classesToVisit.getAllJavaClasses().size());

		final JavaVisitorHandler<Void> javaRelationHandler = Factory.createJavaRelationHandler(
				classesToVisit, externalClasses);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(javaRelationHandler);

		for (Class<?> javaItem : classesToVisit.getAllJavaClasses()) {
			javaVisitor.visit(javaItem);
			monitor.worked(1);
		}
		return Status.OK_STATUS;
	}

}
