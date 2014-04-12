package org.obeonetwork.jdt2uml.core.api.job;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;

public interface ProjectTODO {

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	IJavaProject getProject();

	JobDescriptor getLibDescriptor();

	JobDescriptor getProjectDescriptor();

	Set<JobDescriptor> getDepLibJobs();

	Set<JobDescriptor> getDepProjectJobs();

	void avoidDuplicatedTODOs(Set<ProjectTODO> toRemove);

	Set<ProjectTODO> getSubJobsTODO();

	Set<ProjectTODO> getAllJobsTODO();

	void addSubJobsTODO(ProjectTODO toAdd);

	boolean isSameTo(ProjectTODO toCompare);
}
