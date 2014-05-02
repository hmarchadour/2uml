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
import org.obeonetwork.jdt2uml.core.api.build.Build;
import org.obeonetwork.jdt2uml.core.api.build.BuildDescriptor;
import org.obeonetwork.jdt2uml.core.api.build.BuildTodo;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.core.internal.build.BuildLib;
import org.obeonetwork.jdt2uml.core.internal.build.BuildProject;

public class BuildTodoImpl implements BuildTodo {

	protected IJavaProject javaProject;

	protected BuildDescriptor projectDescriptor;

	protected BuildDescriptor libDescriptor;

	protected Set<BuildTodo> subJobsTODOs;

	public BuildTodoImpl(IJavaProject javaProject, BuildDescriptor projectDescriptor,
			BuildDescriptor libDescriptor) {
		this.javaProject = javaProject;
		this.projectDescriptor = projectDescriptor;
		this.libDescriptor = libDescriptor;

		subJobsTODOs = new HashSet<BuildTodo>();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {
		for (BuildTodo subJobsTODO : subJobsTODOs) {
			subJobsTODO.run(monitor);
		}

		Utils.importUMLResource(libDescriptor.getModel(), UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);
		for (BuildDescriptor depProjectJob : getDepProjectJobs()) {
			if (!projectDescriptor.equals(depProjectJob)) {
				Utils.importUMLResource(libDescriptor.getModel(), depProjectJob.getSemanticModelURI());
			}
		}

		Build exportLibrary = new BuildLib(libDescriptor);
		exportLibrary.run(monitor);

		Utils.importUMLResource(projectDescriptor.getModel(), libDescriptor.getSemanticModelURI());

		LibVisitor libVisitor = (LibVisitor)libDescriptor.getVisitor();
		Build exportModel = new BuildProject(libVisitor.getLazyClasses(), projectDescriptor);
		exportModel.run(monitor);

		return new Status(IStatus.OK, CoreActivator.PLUGIN_ID, null);
	}

	@Override
	public IJavaProject getProject() {
		return javaProject;
	}

	@Override
	public BuildDescriptor getLibDescriptor() {
		return libDescriptor;
	}

	@Override
	public BuildDescriptor getProjectDescriptor() {
		return projectDescriptor;
	}

	@Override
	public Set<BuildTodo> getSubBuildTodos() {
		return subJobsTODOs;
	}

	@Override
	public void avoidDuplicatedBuilds(Set<BuildTodo> toReplace) {
		BuildTodo[] subJobsTODOsArray = subJobsTODOs.toArray(new BuildTodo[0]);

		for (int i = 0; i < subJobsTODOsArray.length; i++) {
			BuildTodo subJobsTODO = subJobsTODOsArray[i];
			for (BuildTodo oneToReplace : toReplace) {
				if (subJobsTODO.isSameTo(oneToReplace)) {
					subJobsTODOs.remove(subJobsTODO);
					subJobsTODOs.add(oneToReplace);
				}
			}
		}
		for (BuildTodo subJobsTODO : subJobsTODOs) {
			subJobsTODO.avoidDuplicatedBuilds(toReplace);
		}
	}

	@Override
	public Set<BuildTodo> getAllBuildTodos() {
		Set<BuildTodo> allSubJobsTODO = new HashSet<BuildTodo>();
		allSubJobsTODO.add(this);
		for (BuildTodo subJobsTODO : subJobsTODOs) {
			allSubJobsTODO.addAll(subJobsTODO.getAllBuildTodos());
		}
		return allSubJobsTODO;
	}

	@Override
	public void addSubBuilds(BuildTodo jobsTODO) {
		subJobsTODOs.add(jobsTODO);
	}

	@Override
	public Set<BuildDescriptor> getDepLibJobs() {
		Set<BuildDescriptor> depLibJobs = new HashSet<BuildDescriptor>();
		depLibJobs.add(libDescriptor);
		for (BuildTodo subJobsTODO : subJobsTODOs) {
			depLibJobs.addAll(subJobsTODO.getDepLibJobs());
		}
		return depLibJobs;
	}

	@Override
	public Set<BuildDescriptor> getDepProjectJobs() {
		Set<BuildDescriptor> depProjectJobs = new HashSet<BuildDescriptor>();
		depProjectJobs.add(projectDescriptor);
		for (BuildTodo subJobsTODO : subJobsTODOs) {
			depProjectJobs.addAll(subJobsTODO.getDepProjectJobs());
		}
		return depProjectJobs;
	}

	@Override
	public boolean isSameTo(BuildTodo toCompare) {
		return this.getProject().equals(toCompare.getProject());
	}
}
