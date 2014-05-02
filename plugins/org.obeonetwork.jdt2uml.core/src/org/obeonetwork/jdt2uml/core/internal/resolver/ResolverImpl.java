package org.obeonetwork.jdt2uml.core.internal.resolver;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.obeonetwork.jdt2uml.core.CoreActivator;
import org.obeonetwork.jdt2uml.core.api.Utils;
import org.obeonetwork.jdt2uml.core.api.lazy.LazyClass;
import org.obeonetwork.jdt2uml.core.api.resolver.Resolver;
import org.obeonetwork.jdt2uml.core.api.resolver.ResolverResult;

public class ResolverImpl implements Resolver {

	protected Namespace context;

	protected Set<LazyClass> lazyClasses;

	protected ResolverResult underResolution;

	public ResolverImpl(Namespace context, Set<LazyClass> lazyClasses) {
		if (context == null) {
			throw new IllegalStateException("Null context should not appended");
		}
		this.context = context;
		this.lazyClasses = lazyClasses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResolverResult resolve(Type rootType) {
		underResolution = new ResolverResultImpl(rootType);
		boolean resolved = tryToResolve(rootType);
		if (resolved) {
			underResolution.setAsResolved();
		}
		ResolverResult result = underResolution;
		underResolution = null;
		return result;
	}

	protected Classifier searchClassifierInModels(String qualifiedName) {
		Classifier classifier = Utils.searchClassifierInModels(context, qualifiedName);
		if (classifier == null) {
			for (LazyClass lazyClass : lazyClasses) {
				if (lazyClass.getLazyHandler().canHandle(qualifiedName)) {
					NamedElement resolved = lazyClass.resolve();
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

		if (underResolution.getResolverMap().containsKey(type)) {
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
			} else if (type.isWildcardType()) {
				result = tryToResolve((WildcardType)type);
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
			underResolution.getResolverMap().put(primitiveType, umlPrimitiveType);
			result = true;
		} else {
			umlPrimitiveType = Utils.searchPrimiveTypeInModels(context, typeCode.toString());
			if (umlPrimitiveType != null) {
				underResolution.getResolverMap().put(primitiveType, umlPrimitiveType);
				result = true;
			}
		}
		return result;
	}

	protected boolean tryToResolve(QualifiedType qualifiedType) {
		boolean result = false;

		if (underResolution.getResolverMap().containsKey(qualifiedType)) {
			result = true;
		} else {
			String qualifiedName = qualifiedType.getName().getFullyQualifiedName();
			Classifier classifier = searchClassifierInModels(qualifiedName);
			if (classifier != null) {
				underResolution.getResolverMap().put(qualifiedType, classifier);
				result = true;
			}
		}
		// No sub types to resolve
		return result;
	}

	protected boolean tryToResolve(SimpleType simpleType) {
		boolean result = false;

		if (underResolution.getResolverMap().containsKey(simpleType)) {
			result = true;
		} else {
			ITypeBinding resolveBinding = simpleType.resolveBinding();
			if (resolveBinding != null) {
				String qualifiedName = resolveBinding.getBinaryName();
				if (qualifiedName != null) {
					Classifier classifier = searchClassifierInModels(qualifiedName);
					if (classifier != null) {
						underResolution.getResolverMap().put(simpleType, classifier);
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

		if (underResolution.getResolverMap().containsKey(parameterizedType)) {
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

	protected boolean tryToResolve(WildcardType parameterizedType) {
		boolean result = true;
		if (parameterizedType.getBound() != null) {
			result = tryToResolve(parameterizedType.getBound());
		}
		return result;
	}
}
