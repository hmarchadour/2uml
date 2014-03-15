package org.obeonetwork.jdt2uml.core.api.wrapper;

import java.util.Set;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Component;

public interface ITypeWrapper {

	IType getType();

	boolean isClass();

	boolean isInterface();

	boolean isAnnotation();

	boolean isEnum();

	boolean isExternal();

	IPackageFragmentRoot getPackageFragmentRoot();

	org.eclipse.uml2.uml.Package handlePackage(Component parent);

	String getElementName();

	String getSuperclassName() throws JavaModelException;

	Set<String> getSuperInterfaceNames() throws JavaModelException;

	Set<IType> getTypes() throws JavaModelException;

	Set<IType> resolveType(String typeIdent);
}
