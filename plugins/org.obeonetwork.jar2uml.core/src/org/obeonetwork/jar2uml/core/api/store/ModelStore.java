package org.obeonetwork.jar2uml.core.api.store;

import java.util.Map;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;

public interface ModelStore {

	Model getModel();

	void add(Class<?> clazz, Element elem);

	void addAll(Map<Element, Class<?>> map);

	Map<Element, Class<?>> getUML2JavaBinding();

	Map<Class<?>, Element> getJava2UMLBinding();

}
