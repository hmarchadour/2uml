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
package org.obeonetwork.jdt2uml.updator.api;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.uml2.uml.Model;
import org.obeonetwork.jdt2uml.updator.internal.handler.ProjectUpdatorHandler;

public final class UpdatorFactory {

	public static ProjectUpdatorHandler createJDTModelUpdatorHandler(IProgressMonitor monitor, Model model) {
		return new ProjectUpdatorHandler(monitor, model);
	}

}
