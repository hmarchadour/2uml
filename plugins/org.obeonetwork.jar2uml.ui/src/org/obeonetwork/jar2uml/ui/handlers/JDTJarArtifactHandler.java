/*******************************************************************************
 * Copyright (c) 2014 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.ui.handlers;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.obeonetwork.jar2uml.core.api.JarConverterJob;

public class JDTJarArtifactHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!selection.isEmpty() && selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)selection;
			TreePath[] paths = treeSelection.getPaths();
			if (paths.length > 0) {
				Object rootSegment = paths[0].getFirstSegment();
				if (rootSegment instanceof IProject || rootSegment instanceof IJavaProject) {
					IProject project;
					if (rootSegment instanceof IProject) {
						project = (IProject)rootSegment;
					} else {
						project = ((IJavaProject)rootSegment).getProject();
					}
					Set<File> files = new HashSet<File>();
					for (Object item : treeSelection.toList()) {
						if (item instanceof IPackageFragmentRoot) {
							IPackageFragmentRoot jarFrag = (IPackageFragmentRoot)item;
							files.add(jarFrag.getPath().toFile());
						}
					}
					Job jars2uml = new JarConverterJob(project, "model", files);
					jars2uml.schedule();
				}
			}
		}

		return null;
	}

}
