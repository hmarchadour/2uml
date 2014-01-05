package org.obeonetwork.jdt2uml.ui;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		// Force the plugin loading
		new org.obeonetwork.jdt2uml.core.api.listener.Startup().run();
	}

}
