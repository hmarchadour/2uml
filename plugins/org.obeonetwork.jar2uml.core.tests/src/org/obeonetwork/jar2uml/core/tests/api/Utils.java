package org.obeonetwork.jar2uml.core.tests.api;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class Utils {
	public static File getFile(String filename) {
		File file = null;
		try {
			URI uri = Utils.class.getClassLoader().getResource(filename).toURI();
			file = new File(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return file;
	}
}
