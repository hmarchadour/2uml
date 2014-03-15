package org.obeonetwork.jdt2uml.ide.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.creator.api.job.ExportUMLModels;

public class JdtCompilationParticipant extends CompilationParticipant {

	public JdtCompilationParticipant() {

	}

	@Override
	public int aboutToBuild(IJavaProject project) {
		System.out.println("aboutToBuild of " + project.getElementName());
		return super.aboutToBuild(project);
	}

	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		// TODO Auto-generated method stub
		super.buildStarting(files, isBatch);
	}

	@Override
	public void cleanStarting(IJavaProject project) {
		System.out.println("clean of " + project.getElementName());
		clean(project);
		super.cleanStarting(project);
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public void reconcile(ReconcileContext context) {
		IJavaProject project = context.getDelta().getElement().getJavaProject();
		System.out.println("reconcile of " + project.getElementName());
		super.reconcile(context);
		clean(project);
		build(project);
	}

	@Override
	public void buildFinished(IJavaProject project) {
		System.out.println("buildFinished of " + project.getElementName());
		super.buildFinished(project);

		build(project);
	}

	private void clean(IJavaProject project) {
		IFolder uml = project.getProject().getFolder("/target/uml");
		if (uml.exists() && uml.isAccessible()) {
			try {
				uml.delete(false, new NullProgressMonitor());
			} catch (CoreException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}
	}

	private void build(IJavaProject project) {
		Set<IJavaProject> javaProjects = new HashSet<IJavaProject>();
		javaProjects.add(project);
		IWorkspaceRunnable jdt2uml = new ExportUMLModels(javaProjects);
		try {
			ResourcesPlugin.getWorkspace().run(jdt2uml, new NullProgressMonitor());
		} catch (CoreException e) {
			CoreActivator.logUnexpectedError(e);
		}
	}
}
