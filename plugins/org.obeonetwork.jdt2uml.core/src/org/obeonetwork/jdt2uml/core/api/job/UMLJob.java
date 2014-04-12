package org.obeonetwork.jdt2uml.core.api.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface UMLJob {

	IStatus run(IProgressMonitor monitor) throws InterruptedException;

	JobDescriptor getJobDescriptor();

}
