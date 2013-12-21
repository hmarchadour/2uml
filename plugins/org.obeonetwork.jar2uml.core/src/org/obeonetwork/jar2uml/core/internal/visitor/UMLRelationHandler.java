/*******************************************************************************
 * Copyright (c) 2013 Hugo Marchadour (Obeo).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hugo Marchadour - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.obeonetwork.jar2uml.core.internal.visitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Type;
import org.obeonetwork.jar2uml.core.api.store.ModelStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

public class UMLRelationHandler implements JavaVisitorHandler<Void> {

	private final ModelStore modelStore;

	private Element context;

	public UMLRelationHandler(ModelStore modelStore) {
		this.modelStore = modelStore;
	}

	@Override
	public void caseClass(Class<?> aClass) {
		context = searchInJavaIndex(aClass);
	}

	@Override
	public void caseSuperClass(Class<?> aSuperClass) {
		if (context instanceof Classifier) {
			Classifier classifier = (Classifier)context;
			Element superClassElement = searchInJavaIndex(aSuperClass);
			if (superClassElement instanceof Classifier) {
				classifier.createGeneralization((Classifier)superClassElement);
			}
		}
	}

	@Override
	public void caseImplementedInterface(Class<?> anImplInterface) {
		if (context instanceof BehavioredClassifier) {
			BehavioredClassifier classifier = (BehavioredClassifier)context;
			Element interfaceElement = searchInJavaIndex(anImplInterface);
			if (interfaceElement instanceof Interface) {
				classifier.createInterfaceRealization(((Interface)interfaceElement).getName(),
						(Interface)interfaceElement);
			}
		}
	}

	@Override
	public void caseInterface(Class<?> anInterface) {
		context = searchInJavaIndex(anInterface);
	}

	@Override
	public void caseAnnotation(Class<?> anAnnotation) {
		context = searchInJavaIndex(anAnnotation);
	}

	@Override
	public void caseEnum(Class<?> anEnum) {
		context = searchInJavaIndex(anEnum);
	}

	@Override
	public void caseConstructor(Constructor<?> constructor) {
		// TODO
	}

	@Override
	public void caseField(Field aField) {
		Class<?> fieldType = aField.getType();

		int cardianality = 1;
		if (fieldType.isArray()) {
			cardianality = -1;
		}
		Element fieldUMLType = searchInJavaIndex(fieldType);

		if (fieldUMLType != null) {
			if (fieldUMLType instanceof Type) {
				if (context instanceof Interface) {
					((Interface)context).createOwnedAttribute(aField.getName(), (Type)fieldUMLType, 0,
							cardianality);
				} else if (context instanceof org.eclipse.uml2.uml.Class) {
					((org.eclipse.uml2.uml.Class)context).createOwnedAttribute(aField.getName(),
							(Type)fieldUMLType, 0, cardianality);
				} else {
					System.out.println("Not handled context:" + context);
				}
			} else {
				throw new IllegalStateException("Not handled fieldUMLType:" + fieldUMLType);
			}
		}
	}

	@Override
	public void caseMethod(Method method) {
		if (context instanceof Interface || context instanceof org.eclipse.uml2.uml.Class) {
			Classifier classifier = (Classifier)context;

			Operation createOwnedOperation;
			if (context instanceof Interface) {
				createOwnedOperation = ((Interface)classifier).createOwnedOperation(method.getName(), null,
						null);
			} else if (context instanceof org.eclipse.uml2.uml.Class) {
				createOwnedOperation = ((org.eclipse.uml2.uml.Class)classifier).createOwnedOperation(
						method.getName(), null, null);
			} else {
				throw new IllegalStateException("Not handled context:" + context);
			}
			if (!"void".equals(method.getReturnType().getName())) {
				Element returnType = searchInJavaIndex(method.getReturnType());
				if (returnType != null) {
					createOwnedOperation.createReturnResult("return", (Type)returnType);
				}
			}
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> parameterType : parameterTypes) {
				Element paramType = searchInJavaIndex(parameterType);
				if (paramType != null) {
					createOwnedOperation.createOwnedParameter("", (Type)paramType);
				}
			}
		}
	}

	@Override
	public Void getResult() {
		return null;
	}

	private Element searchInJavaIndex(Class<?> clazz) {
		Element result = null;
		Map<Class<?>, Element> indexJava2UML = modelStore.getJava2UMLBinding();
		if (clazz.isPrimitive()) {
			List<Package> importedPackages = modelStore.getModel().getImportedPackages();
			for (Package package_ : importedPackages) {
				NamedElement member = package_.getMember(clazz.getName());
				if (member instanceof Type) {
					result = member;
				}
			}
		} else if (clazz.isArray()) {
			result = searchInJavaIndex(clazz.getComponentType());
		} else {
			result = indexJava2UML.get(clazz);
		}
		if (result == null) {
			System.out.println("not found in the model " + clazz.getName());
		}
		return result;
	}
}
