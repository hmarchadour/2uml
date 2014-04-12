package org.obeonetwork.jdt2uml.core.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.handler.LazyHandler;

public class DomTypeResolver {

	protected Namespace context;

	protected Type rootType;

	protected Map<Type, Classifier> resolverMap;

	protected boolean isResolved;

	protected Set<LazyHandler> lazyHandlers;

	public DomTypeResolver(Namespace context, Type rootType, Set<LazyHandler> lazyHandlers) {
		if (context == null) {
			throw new IllegalStateException("Null context should not appended");
		}
		if (rootType == null) {
			throw new IllegalStateException("Null rootType should not appended");
		}
		this.context = context;
		this.rootType = rootType;
		this.lazyHandlers = lazyHandlers;
		resolverMap = new LinkedHashMap<Type, Classifier>();
		isResolved = false;
	}

	public boolean isResolved() {
		return isResolved;
	}

	public boolean tryToResolve() {
		return tryToResolve(rootType);
	}

	public Map<Type, Classifier> getResolverMap() {
		return resolverMap;
	}

	public Classifier getRootClassifier() {
		Classifier rootClassifier;
		if (rootType.isParameterizedType()) {
			// TODO enhance this
			rootClassifier = resolverMap.get(((ParameterizedType)rootType).getType());
		} else if (rootType.isArrayType()) {
			rootClassifier = resolverMap.get(((ArrayType)rootType).getElementType());
		} else {
			rootClassifier = resolverMap.get(rootType);
		}
		return rootClassifier;
	}

	public Type getRootType() {
		return rootType;
	}

	protected Classifier searchClassifierInModels(String qualifiedName) {
		Classifier classifier = Utils.searchClassifierInModels(context, qualifiedName);
		if (classifier == null) {
			for (LazyHandler lazyHandler : lazyHandlers) {
				if (lazyHandler.isCompatible(qualifiedName)) {
					NamedElement resolved = lazyHandler.resolve();
					if (resolved instanceof Classifier) {
						classifier = (Classifier)resolved;
						break;
					}
				}
			}
		}
		return classifier;
	}

	protected boolean tryToResolve(Type type) {
		boolean result = false;

		if (resolverMap.containsKey(type)) {
			result = true;
		} else {
			if (type.isPrimitiveType()) {
				result = tryToResolve((PrimitiveType)type);
			} else if (type.isQualifiedType()) {
				result = tryToResolve((QualifiedType)type);
			} else if (type.isArrayType()) {
				result = tryToResolve(((ArrayType)type).getElementType());
			} else if (type.isSimpleType()) {
				result = tryToResolve((SimpleType)type);
			} else if (type.isParameterizedType()) {
				result = tryToResolve((ParameterizedType)type);
			} else {
				CoreActivator.log(IStatus.INFO, "Type not handled " + type.toString());
			}
		}
		return result;
	}

	protected boolean tryToResolve(PrimitiveType primitiveType) {
		boolean result = false;

		PrimitiveType.Code typeCode = primitiveType.getPrimitiveTypeCode();
		org.eclipse.uml2.uml.PrimitiveType umlPrimitiveType;
		if (typeCode.equals(PrimitiveType.VOID)) {
			umlPrimitiveType = null;
			resolverMap.put(primitiveType, umlPrimitiveType);
			result = true;
		} else {
			umlPrimitiveType = Utils.searchPrimiveTypeInModels(context, typeCode.toString());
			if (umlPrimitiveType != null) {
				resolverMap.put(primitiveType, umlPrimitiveType);
				result = true;
			}
		}
		return result;
	}

	protected boolean tryToResolve(QualifiedType qualifiedType) {
		boolean result = false;

		if (resolverMap.containsKey(qualifiedType)) {
			result = true;
		} else {
			String qualifiedName = qualifiedType.getName().getFullyQualifiedName();
			Classifier classifier = searchClassifierInModels(qualifiedName);
			if (classifier != null) {
				resolverMap.put(qualifiedType, classifier);
				result = true;
			}
		}
		// No sub types to resolve
		return result;
	}

	protected boolean tryToResolve(SimpleType simpleType) {
		boolean result = false;

		if (resolverMap.containsKey(simpleType)) {
			result = true;
		} else {
			ITypeBinding resolveBinding = simpleType.resolveBinding();
			if (resolveBinding != null) {
				String qualifiedName = resolveBinding.getBinaryName();
				if (qualifiedName != null) {
					Classifier classifier = searchClassifierInModels(qualifiedName);
					if (classifier != null) {
						resolverMap.put(simpleType, classifier);
						result = true;
					}
				}
			}
		}
		// No sub types to resolve
		return result;
	}

	protected boolean tryToResolve(ParameterizedType parameterizedType) {
		boolean result = false;

		if (resolverMap.containsKey(parameterizedType)) {
			result = true;
		} else {
			result = true;
			@SuppressWarnings("rawtypes")
			List typeArguments = parameterizedType.typeArguments();
			for (Object object : typeArguments) {
				if (object instanceof Type) {
					if (!tryToResolve((Type)object)) {
						result = false;
						break;
					}
				}
			}
			if (result) {
				Type type = parameterizedType.getType();
				result = tryToResolve(type);
			}
		}
		return result;
	}
}
