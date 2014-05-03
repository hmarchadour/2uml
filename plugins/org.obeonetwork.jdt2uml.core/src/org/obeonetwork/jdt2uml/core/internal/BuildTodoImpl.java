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
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.visitor.LibVisitor;
import org.obeonetwork.jdt2uml.core.internal.build.BuildLib;
import org.obeonetwork.jdt2uml.core.internal.build.BuildProject;

public class BuildTodoImpl implements BuildTodo {

	protected IJavaProject javaProject;

	protected BuildDescriptor projectDesc;

	protected BuildDescriptor libDesc;

	protected Set<BuildTodo> subBuildTODOs;

	public BuildTodoImpl(IJavaProject javaProject, BuildDescriptor projectDescriptor,
			BuildDescriptor libDescriptor) {
		this.javaProject = javaProject;
		this.projectDesc = projectDescriptor;
		this.libDesc = libDescriptor;

		subBuildTODOs = new HashSet<BuildTodo>();
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws InterruptedException {

		// Build dependencies before
		for (BuildTodo subBuildTODO : subBuildTODOs) {
			subBuildTODO.run(monitor);
		}

		// Import Java primitive in Lib model
		Utils.importUMLResource(libDesc.getModel(), UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		// Import dependencies in the current library model
		for (BuildDescriptor depProjectJob : getDepProjectDescriptors()) {
			if (!projectDesc.equals(depProjectJob)) {
				Utils.importUMLResource(libDesc.getModel(), depProjectJob.getSemanticModelURI());
			}
		}

		// Build the current library model
		Build buildLib = new BuildLib(libDesc);
		buildLib.run(monitor);

		// Import the library in the project model
		Utils.importUMLResource(projectDesc.getModel(), libDesc.getSemanticModelURI());

		// Build the current project model
		Set<LazyClass> lazyClasses = ((LibVisitor)libDesc.getVisitor()).getLazyClasses();
		Build exportModel = new BuildProject(lazyClasses, projectDesc);
		exportModel.run(monitor);

		return new Status(IStatus.OK, CoreActivator.PLUGIN_ID, null);
	}

	@Override
	public IJavaProject getProject() {
		return javaProject;
	}

	@Override
	public BuildDescriptor getLibDescriptor() {
		return libDesc;
	}

	@Override
	public BuildDescriptor getProjectDescriptor() {
		return projectDesc;
	}

	@Override
	public Set<BuildTodo> getSubBuildTodos() {
		return subBuildTODOs;
	}

	@Override
	public void avoidDuplicatedBuilds(Set<BuildTodo> toReplace) {
		BuildTodo[] subBuildTODOsArray = subBuildTODOs.toArray(new BuildTodo[0]);

		for (int i = 0; i < subBuildTODOsArray.length; i++) {
			BuildTodo subBuildTODO = subBuildTODOsArray[i];
			for (BuildTodo oneToReplace : toReplace) {
				if (subBuildTODO.isSameTo(oneToReplace)) {
					subBuildTODOs.remove(subBuildTODO);
					subBuildTODOs.add(oneToReplace);
				}
			}
		}
		for (BuildTodo subBuildTODO : subBuildTODOs) {
			subBuildTODO.avoidDuplicatedBuilds(toReplace);
		}
	}

	@Override
	public Set<BuildTodo> getAllBuildTodos() {
		Set<BuildTodo> allSubBuildTodo = new HashSet<BuildTodo>();
		allSubBuildTodo.add(this);
		for (BuildTodo subBuildTODO : subBuildTODOs) {
			allSubBuildTodo.addAll(subBuildTODO.getAllBuildTodos());
		}
		return allSubBuildTodo;
	}

	@Override
	public void addSubBuildTodos(BuildTodo buildTodo) {
		subBuildTODOs.add(buildTodo);
	}

	@Override
	public Set<BuildDescriptor> getDepLibDescriptors() {
		Set<BuildDescriptor> depLibDescriptors = new HashSet<BuildDescriptor>();
		depLibDescriptors.add(libDesc);
		for (BuildTodo subBuildTODO : subBuildTODOs) {
			depLibDescriptors.addAll(subBuildTODO.getDepLibDescriptors());
		}
		return depLibDescriptors;
	}

	@Override
	public Set<BuildDescriptor> getDepProjectDescriptors() {
		Set<BuildDescriptor> depProjectDescriptors = new HashSet<BuildDescriptor>();
		depProjectDescriptors.add(projectDesc);
		for (BuildTodo subBuildTODO : subBuildTODOs) {
			depProjectDescriptors.addAll(subBuildTODO.getDepProjectDescriptors());
		}
		return depProjectDescriptors;
	}

	@Override
	public boolean isSameTo(BuildTodo toCompare) {
		return this.getProject().equals(toCompare.getProject());
	}
}
