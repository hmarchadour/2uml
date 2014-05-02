package org.obeonetwork.jdt2uml.core.api.build;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;

public interface BuildTodo {

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	IJavaProject getProject();

	BuildDescriptor getLibDescriptor();

	BuildDescriptor getProjectDescriptor();

	Set<BuildDescriptor> getDepLibJobs();

	Set<BuildDescriptor> getDepProjectJobs();

	void avoidDuplicatedBuilds(Set<BuildTodo> toRemove);

	Set<BuildTodo> getSubBuildTodos();

	Set<BuildTodo> getAllBuildTodos();

	void addSubBuilds(BuildTodo toAdd);

	boolean isSameTo(BuildTodo toCompare);
}
