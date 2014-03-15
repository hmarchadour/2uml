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
package org.obeonetwork.jdt2uml.creator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.obeonetwork.jdt2uml.creator.api.listener.ChangeListener;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CreatorActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.obeonetwork.jdt2uml.creator"; //$NON-NLS-1$

	// The shared instance
	private static CreatorActivator plugin;

	private Set<IResourceChangeListener> changeListeners;

	/**
	 * The constructor
	 */
	public CreatorActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		changeListeners = new HashSet<IResourceChangeListener>();
		updateChangeListerners();
	}

	private void updateChangeListerners() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		ChangeListener changeListener = new ChangeListener();
		changeListeners.add(changeListener);
		workspace.addResourceChangeListener(changeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		for (IResourceChangeListener changeListener : changeListeners) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(changeListener);
		}
		changeListeners.clear();
		changeListeners = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CreatorActivator getDefault() {
		return plugin;
	}

	public static void logUnexpectedError(Throwable exception) {
		log(Status.ERROR, "Unexpected error.", exception);
	}

	public static void log(int severity, String message) {
		log(severity, message, null);
	}

	public static void log(int severity, String message, Throwable exception) {
		if (exception == null) {
			getDefault().getLog().log(new Status(severity, CreatorActivator.PLUGIN_ID, message));
		} else {
			getDefault().getLog().log(new Status(severity, CreatorActivator.PLUGIN_ID, message, exception));
		}
	}

}
