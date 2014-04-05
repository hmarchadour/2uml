package org.obeonetwork.jdt2uml.creator.internal.handler.async;

public interface AsyncHandler {

	public abstract boolean isHandleable();

	public abstract void handle();

	public abstract boolean isHandled();

}
