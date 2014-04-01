package org.obeonetwork.jdt2uml.ide.internal;

import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.obeonetwork.jdt2uml.creator.api.job.ExportModels;

public class ProjectBuilder extends IncrementalProjectBuilder {

	public ProjectBuilder() {
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException {

		if (getProject().hasNature(JavaCore.NATURE_ID)) {
			if (kind == IncrementalProjectBuilder.FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
		}
		return null;
	}

	private void fullBuild(IProgressMonitor monitor) {
		IJavaProject javaProject = JavaCore.create(getProject());
		HashSet<IJavaProject> javaProjects = new HashSet<IJavaProject>();
		javaProjects.add(javaProject);
		IWorkspaceRunnable jdt2uml = new ExportModels(javaProjects);
		try {
			ResourcesPlugin.getWorkspace().run(jdt2uml, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
	}
}
