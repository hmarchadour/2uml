package org.obeonetwork.jdt2uml.core.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Namespace;
import org.obeonetwork.jdt2uml.core.CoreActivator;

public class DomTypeResolver {

	Namespace context;

	Type rootType;

	Map<Type, Classifier> resolverMap;

	boolean isResolved;

	public DomTypeResolver(Namespace context, Type rootType) {
		if (context == null) {
			throw new IllegalStateException("Null context should not appended");
		}
		if (rootType == null) {
			throw new IllegalStateException("Null rootType should not appended");
		}
		this.context = context;
		this.rootType = rootType;
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
			Classifier classifier = Utils.searchClassifierInModels(context, qualifiedType.getName()
					.getFullyQualifiedName());
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
				String binaryName = resolveBinding.getBinaryName();
				if (binaryName != null) {
					Classifier classifier = Utils.searchClassifierInModels(context, binaryName);
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
