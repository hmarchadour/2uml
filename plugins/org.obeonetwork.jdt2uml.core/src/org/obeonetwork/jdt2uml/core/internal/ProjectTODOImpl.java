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
import org.obeonetwork.jdt2uml.core.api.job.ProjectTODO;
import org.obeonetwork.jdt2uml.core.api.job.UMLJob;

public class ProjectTODOImpl implements ProjectTODO {

	protected IJavaProject javaProject;

	protected UMLJob libJob;

	protected UMLJob projectJob;

	@Deprecated
	protected Set<UMLJob> depLibJobs;

	@Deprecated
	protected Set<UMLJob> depProjectJobs;

	protected Set<ProjectTODO> subJobsTODOs;

	public ProjectTODOImpl(IJavaProject javaProject, UMLJob projectJob, UMLJob libJob) {
		this.javaProject = javaProject;
		this.projectJob = projectJob;
		this.libJob = libJob;
		depLibJobs = null;
		depProjectJobs = null;
		subJobsTODOs = new HashSet<ProjectTODO>();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		for (ProjectTODO subJobsTODO : subJobsTODOs) {
			subJobsTODO.run(monitor);
		}

		Utils.importUMLResource(libJob.getModel(), UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);
		for (UMLJob depProjectJob : getDepProjectJobs()) {
			if (!projectJob.equals(depProjectJob)) {
				Utils.importUMLResource(libJob.getModel(), depProjectJob.getSemanticModelURI());
			}
		}
		libJob.run(monitor);

		Utils.importUMLResource(projectJob.getModel(), libJob.getSemanticModelURI());
		projectJob.run(monitor);

		return new Status(IStatus.OK, CoreActivator.PLUGIN_ID, null);
	}

	@Override
	public IJavaProject getProject() {
		return javaProject;
	}

	@Override
	public UMLJob getLibJob() {
		return libJob;
	}

	@Override
	public UMLJob getProjectJob() {
		return projectJob;
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
	public Set<UMLJob> getDepLibJobs() {
		if (depLibJobs == null) {
			depLibJobs = new HashSet<UMLJob>();
			depLibJobs.add(libJob);
			for (ProjectTODO subJobsTODO : subJobsTODOs) {
				depLibJobs.addAll(subJobsTODO.getDepLibJobs());
			}
		}
		return depLibJobs;
	}

	@Override
	public Set<UMLJob> getDepProjectJobs() {
		if (depProjectJobs == null) {
			depProjectJobs = new HashSet<UMLJob>();
			depProjectJobs.add(projectJob);
			for (ProjectTODO subJobsTODO : subJobsTODOs) {
				depProjectJobs.addAll(subJobsTODO.getDepProjectJobs());
			}
		}
		return depProjectJobs;
	}

	@Override
	public boolean isSameTo(ProjectTODO toCompare) {
		return this.getProject().equals(toCompare.getProject());
	}
}
