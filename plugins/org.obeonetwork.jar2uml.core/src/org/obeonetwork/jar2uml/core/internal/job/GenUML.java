package org.obeonetwork.jar2uml.core.internal.job;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.jar2uml.core.api.Factory;
import org.obeonetwork.jar2uml.core.api.Utils;
import org.obeonetwork.jar2uml.core.api.store.ClassStore;
import org.obeonetwork.jar2uml.core.api.store.JarStore;
import org.obeonetwork.jar2uml.core.api.store.JavaStore;
import org.obeonetwork.jar2uml.core.api.store.ModelStore;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitor;
import org.obeonetwork.jar2uml.core.api.visitor.JavaVisitorHandler;

import com.google.common.collect.Maps;

public class GenUML extends Job {

	private JarStore jarStore;

	private ClassStore externalClasses;

	private String modelName;

	private Resource resource;

	private Model model;

	public GenUML(JarStore jarStore, ClassStore externalClasses, String modelName, Resource resource) {
		super(GenUML.class.getSimpleName());
		this.jarStore = jarStore;
		this.externalClasses = externalClasses;
		this.modelName = modelName;
		this.resource = resource;
	}

	public Model getModel() {
		return model;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		model = UMLFactory.eINSTANCE.createModel();
		resource.getContents().add(model);
		model.setName(modelName);
		Utils.importPrimitiveTypes(model, UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI);

		ModelStore modelStore = Factory.createModelStore(model);

		if (!externalClasses.getAllJavaItems().isEmpty()) {
			Component createdComponent = UMLFactory.eINSTANCE.createComponent();
			model.getPackagedElements().add(createdComponent);
			createdComponent.setName("external");
			Set<Element> createdElems = createModelElements(createdComponent, externalClasses, modelStore);
			System.out.println(createdElems);
		}

		Component createdComponent = UMLFactory.eINSTANCE.createComponent();
		model.getPackagedElements().add(createdComponent);
		createdComponent.setName("internal");
		Set<Element> createdElems = createModelElements(createdComponent, jarStore, modelStore);

		handleElementRelations(model, modelStore, createdElems);

		try {
			resource.save(Maps.newHashMap());
		} catch (IOException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private static void handleElementRelations(Model model, ModelStore modelStore, Set<Element> internalElems) {

		final JavaVisitorHandler<Void> modelRelationHandler = Factory.createUMLRelationHandler(modelStore);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(modelRelationHandler);

		for (Class<?> javaItem : modelStore.getJava2UMLBinding().keySet()) {
			javaVisitor.visit(javaItem);
		}

	}

	private static Set<Element> createModelElements(Component parent, JavaStore javaStore,
			ModelStore modelStore) {
		final JavaVisitorHandler<Set<Element>> modelInitializer = Factory.createInitializerHandler(parent,
				modelStore);
		final JavaVisitor javaVisitor = Factory.createJavaVisitor(modelInitializer);

		for (Class<?> javaItem : javaStore.getAllJavaItems()) {
			javaVisitor.visit(javaItem);
		}

		return modelInitializer.getResult();
	}
}
