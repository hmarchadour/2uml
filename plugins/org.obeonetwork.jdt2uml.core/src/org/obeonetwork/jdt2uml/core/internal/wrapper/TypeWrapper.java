package org.obeonetwork.jdt2uml.core.internal.wrapper;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.uml2.uml.Component;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.wrapper.ITypeWrapper;

public class TypeWrapper implements ITypeWrapper {

	private IType type;

	public TypeWrapper(IType type) {
		this.type = type;
	}

	public IType getType() {
		return type;
	}

	@Override
	public boolean isClass() {
		return Utils.isClass(type);
	}

	@Override
	public boolean isInterface() {
		return Utils.isInterface(type);
	}

	@Override
	public boolean isAnnotation() {
		return Utils.isAnnotation(type);
	}

	@Override
	public boolean isEnum() {
		return Utils.isEnum(type);
	}

	@Override
	public boolean isExternal() {
		return Utils.isExternal(type);
	}

	@Override
	public IPackageFragmentRoot getPackageFragmentRoot() {
		return Utils.getPackageFragmentRoot(type);
	}

	@Override
	public org.eclipse.uml2.uml.Package handlePackage(Component parent) {
		return Utils.handlePackage(parent, type.getPackageFragment());
	}

	@Override
	public String getElementName() {
		return type.getElementName();
	}

	@Override
	public String getSuperclassName() throws JavaModelException {
		return type.getSuperclassName();
	}

	@Override
	public Set<String> getSuperInterfaceNames() throws JavaModelException {
		Set<String> result = new LinkedHashSet<String>();
		Collections.addAll(result, type.getSuperInterfaceNames());
		return result;
	}

	@Override
	public Set<IType> getTypes() throws JavaModelException {
		Set<IType> result = new LinkedHashSet<IType>();
		Collections.addAll(result, type.getTypes());
		return result;
	}

	@Override
	public Set<IType> resolveType(String typeIdent) {
		return Utils.resolveType(type, typeIdent);
	}
}
