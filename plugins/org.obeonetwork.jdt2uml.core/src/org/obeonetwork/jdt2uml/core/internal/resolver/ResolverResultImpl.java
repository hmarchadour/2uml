package org.obeonetwork.jdt2uml.core.internal.resolver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.uml2.uml.Classifier;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public class ResolverResultImpl implements ResolverResult {

	protected Type rootType;

	protected Map<Type, Classifier> resolverMap;

	protected boolean isResolved;

	public ResolverResultImpl(Type rootType) {
		this.rootType = rootType;
		this.resolverMap = new LinkedHashMap<Type, Classifier>();
		this.isResolved = false;
	}

	@Override
	public boolean isResolved() {
		return isResolved;
	}

	@Override
	public void setAsResolved() {
		isResolved = true;
	}

	@Override
	public Map<Type, Classifier> getResolverMap() {
		return resolverMap;
	}

	@Override
	public Classifier getRootClassifier() {
		Classifier rootClassifier;
		if (rootType.isParameterizedType()) {
			// TODO enhance this
			rootClassifier = resolverMap.get(((ParameterizedType)rootType).getType());
		} else if (rootType.isArrayType()) {
			rootClassifier = resolverMap.get(((ArrayType)rootType).getElementType());
		} else if (rootType.isWildcardType()) {
			rootClassifier = resolverMap.get(((WildcardType)rootType).getBound());
		} else {
			rootClassifier = resolverMap.get(rootType);
		}
		return rootClassifier;
	}

	@Override
	public Type getRootType() {
		// TODO Auto-generated method stub
		return null;
	}

}
