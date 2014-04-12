package org.obeonetwork.jdt2uml.core.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.job.JobDescriptor;
import org.obeonetwork.jdt2uml.core.api.job.ProjectTODO;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.core.internal.job.ExportLibModel;
import org.obeonetwork.jdt2uml.core.internal.job.ExportProjectModel;

public class ProjectTODOImpl implements ProjectTODO {

	protected IJavaProject javaProject;

	protected JobDescriptor projectDescriptor;

	protected JobDescriptor libDescriptor;

	protected Set<ProjectTODO> subJobsTODOs;

	public ProjectTODOImpl(IJavaProject javaProject, JobDescriptor projectDescriptor,
			JobDescriptor libDescriptor) {
		this.javaProject = javaProject;
		this.projectDescriptor = projectDescriptor;
		this.libDescriptor = libDescriptor;

		subJobsTODOs = new HashSet<ProjectTODO>();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			subJobsTODO.run(monitor);
		}

		Utils.importUMLResource(libDescriptor.getModel(), UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);
		for (JobDescriptor depProjectJob : getDepProjectJobs()) {
			if (!projectDescriptor.equals(depProjectJob)) {
				Utils.importUMLResource(libDescriptor.getModel(), depProjectJob.getSemanticModelURI());
			}
		}

		UMLJob exportLibrary = new ExportLibModel(libDescriptor);
		exportLibrary.run(monitor);

		Utils.importUMLResource(projectDescriptor.getModel(), libDescriptor.getSemanticModelURI());

		LibVisitor libVisitor = (LibVisitor)libDescriptor.getVisitor();
		UMLJob exportModel = new ExportProjectModel(libVisitor.getLazyHandlers(), projectDescriptor);
		exportModel.run(monitor);

		return new Status(IStatus.OK, CoreActivator.PLUGIN_ID, null);
	}

	@Override
	public IJavaProject getProject() {
		return javaProject;
	}

	@Override
	public JobDescriptor getLibDescriptor() {
		return libDescriptor;
	}

	@Override
	public JobDescriptor getProjectDescriptor() {
		return projectDescriptor;
	}

	@Override
	public Set<ProjectTODO> getSubJobsTODO() {
		return subJobsTODOs;
	}

	@Override
	public void avoidDuplicatedTODOs(Set<ProjectTODO> toReplace) {
		ProjectTODO[] subJobsTODOsArray = subJobsTODOs.toArray(new ProjectTODO[0]);

		for (int i = 0; i < subJobsTODOsArray.length; i++) {
			ProjectTODO subJobsTODO = subJobsTODOsArray[i];
			for (ProjectTODO oneToReplace : toReplace) {
				if (subJobsTODO.isSameTo(oneToReplace)) {
					subJobsTODOs.remove(subJobsTODO);
					subJobsTODOs.add(oneToReplace);
				}
			}
		}
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			subJobsTODO.avoidDuplicatedTODOs(toReplace);
		}
	}

	@Override
	public Set<ProjectTODO> getAllJobsTODO() {
		Set<ProjectTODO> allSubJobsTODO = new HashSet<ProjectTODO>();
		allSubJobsTODO.add(this);
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			allSubJobsTODO.addAll(subJobsTODO.getAllJobsTODO());
		}
		return allSubJobsTODO;
	}

	@Override
	public void addSubJobsTODO(ProjectTODO jobsTODO) {
		subJobsTODOs.add(jobsTODO);
	}

	@Override
	public Set<JobDescriptor> getDepLibJobs() {
		Set<JobDescriptor> depLibJobs = new HashSet<JobDescriptor>();
		depLibJobs.add(libDescriptor);
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			depLibJobs.addAll(subJobsTODO.getDepLibJobs());
		}
		return depLibJobs;
	}

	@Override
	public Set<JobDescriptor> getDepProjectJobs() {
		Set<JobDescriptor> depProjectJobs = new HashSet<JobDescriptor>();
		depProjectJobs.add(projectDescriptor);
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			depProjectJobs.addAll(subJobsTODO.getDepProjectJobs());
		}
		return depProjectJobs;
	}

	@Override
	public boolean isSameTo(ProjectTODO toCompare) {
		return this.getProject().equals(toCompare.getProject());
	}
}
