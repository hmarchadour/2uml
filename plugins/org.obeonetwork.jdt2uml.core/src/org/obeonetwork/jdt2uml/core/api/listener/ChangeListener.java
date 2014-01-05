package org.obeonetwork.jdt2uml.core.api.listener;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.obeonetwork.jdt2uml.core.api.job.ExportUMLModels;

public class ChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_BUILD) {
			System.out.println("POST_BUILD changed!");
			Set<IJavaProject> allJavaProjects = getJavaProjects();
			Set<IJavaProject> filtredJavaProjects = filterJavaProjects(allJavaProjects);
			for (IJavaProject filtredJavaProject : filtredJavaProjects) {
				Job jdt2uml = new ExportUMLModels(filtredJavaProject);
				jdt2uml.schedule();
			}
		} else if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			handleDeltaChanges(event.getDelta());
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
					e.printStackTrace();
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
				e.printStackTrace();
			}
		}
		return javaProjects;
	}

	protected void handleAddedResource(IResource resource) {
		IJavaElement javaElement = JavaCore.create(resource);
		System.out.println(javaElement);
	}

	protected void handleRemovedResource(IResource resource) {
		IJavaElement javaElement = JavaCore.create(resource);
		System.out.println(javaElement);
	}

	protected void handleChangedResource(IResource resource) {
		IJavaElement javaElement = JavaCore.create(resource);
		System.out.println(javaElement);

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
						}

						return false;
					}
				};
				try {
					resourceDelta.accept(visitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
