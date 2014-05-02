package org.obeonetwork.jdt2uml.core.api.build;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface Build {

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	BuildDescriptor getDescriptor();

}
