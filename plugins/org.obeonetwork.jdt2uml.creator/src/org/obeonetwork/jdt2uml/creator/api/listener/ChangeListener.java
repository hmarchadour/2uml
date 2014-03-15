package org.obeonetwork.jdt2uml.creator.api.listener;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.creator.api.job.ExportUMLModels;

public class ChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			IJavaElement javaElement = JavaCore.create(event.getResource());
			if (javaElement != null && javaElement instanceof IJavaProject) {
				IJavaProject javaProject = (IJavaProject)javaElement;
				Set<Model> models = Utils.getModel(javaProject);
				if (models.isEmpty()) {
					Set<IJavaProject> javaProjects = new HashSet<IJavaProject>();
					javaProjects.add(javaProject);
					IWorkspaceRunnable jdt2uml = new ExportUMLModels(javaProjects);
					try {
						ResourcesPlugin.getWorkspace().run(jdt2uml, new NullProgressMonitor());
					} catch (CoreException e) {
						CoreActivator.logUnexpectedError(e);
					}
				} else {
					handleDeltaChanges(event.getDelta());
				}
			}
		}
	}

	protected Set<IJavaProject> filterJavaProjects(Set<IJavaProject> allJavaProjects) {
		Set<IJavaProject> filtredJavaProjects = new HashSet<IJavaProject>(allJavaProjects);

		for (IJavaProject javaProject : allJavaProjects) {
			IProject[] referencingProjects = javaProject.getProject().getReferencingProjects();
			for (IProject referencingProject : referencingProjects) {
				try {
					if (referencingProject.hasNature(JavaCore.NATURE_ID)) {
						filtredJavaProjects.remove(JavaCore.create(referencingProject));
					}
				} catch (CoreException e) {
					CoreActivator.logUnexpectedError(e);
				}
			}
		}

		return filtredJavaProjects;
	}

	protected Set<IJavaProject> getJavaProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		Set<IJavaProject> javaProjects = new HashSet<IJavaProject>();
		for (IProject project : projects) {
			try {
				if (project.hasNature(JavaCore.NATURE_ID)) {
					javaProjects.add(JavaCore.create(project));
				}
			} catch (CoreException e) {
				CoreActivator.logUnexpectedError(e);
			}
		}
		return javaProjects;
	}

	private void handle(final IResource resource) {
		IWorkspaceRunnable iWorkspaceRunnable = new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IJavaElement javaElement = JavaCore.create(resource);
				if (javaElement != null && javaElement instanceof IJavaProject) {
					Set<IJavaProject> javaProjects = new HashSet<IJavaProject>();
					javaProjects.add((IJavaProject)javaElement);
					IWorkspaceRunnable jdt2uml = new ExportUMLModels(javaProjects);
					try {
						ResourcesPlugin.getWorkspace().run(jdt2uml, new NullProgressMonitor());
					} catch (CoreException e) {
						CoreActivator.logUnexpectedError(e);
					}
				}
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(iWorkspaceRunnable, new NullProgressMonitor());
		} catch (CoreException e) {
			CoreActivator.logUnexpectedError(e);
		}
	}

	protected void handleAddedResource(final IResource resource) {
		handle(resource);
	}

	protected void handleRemovedResource(IResource resource) {
		handle(resource);
	}

	protected void handleChangedResource(IResource resource) {
		handle(resource);
	}

	protected void handleDeltaChanges(IResourceDelta rootDelta) {

		Set<IJavaProject> javaProjects = getJavaProjects();

		for (IJavaProject javaProject : javaProjects) {
			IResourceDelta resourceDelta = rootDelta.findMember(javaProject.getPath());
			if (resourceDelta != null) {
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) {
						// only interested in changed resources (not added or removed)
						if (delta.getKind() == IResourceDelta.CHANGED) {
							IResource resource = delta.getResource();
							if (delta.getKind() == IResourceDelta.ADDED) {
								handleAddedResource(resource);
							} else if (delta.getKind() == IResourceDelta.REMOVED) {
								handleRemovedResource(resource);
							} else {
								handleChangedResource(resource);
							}
							return true;
						} else {
							System.out.println(delta.getKind());
						}

						return false;
					}
				};
				try {
					resourceDelta.accept(visitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					CoreActivator.logUnexpectedError(e);
				}
			}
		}
	}
}
