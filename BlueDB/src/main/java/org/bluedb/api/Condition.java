package org.bluedb.api;

import java.io.Serializable;

@FunctionalInterface
public interface Condition<T extends Serializable> {
	public boolean test(T object);
}
