package org.obeonetwork.jar2uml.core.api.store;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

public interface ClassStore {

	Set<File> getFiles();

	Map<File, Set<Class<?>>> getFile2JavaItemsBinding();

	Optional<File> retrieveFile(Class<?> clazz);

	Set<Class<?>> getAllJavaItems();

	Set<Class<?>> getAllJavaClasses();

	Set<Class<?>> getAllJavaInterfaces();

	Set<Class<?>> getAllJavaAnnotations();

	Set<Class<?>> getAllJavaEnums();

	void add(File file, Class<?> clazz);

	void addClass(File file, Class<?> clazz);

	void addInterface(File file, Class<?> clazz);

	void addAnnotation(File file, Class<?> clazz);

	void addEnum(File file, Class<?> clazz);

}
