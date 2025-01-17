package org.bluedb.api.index;

import java.io.Serializable;
import java.util.List;
import org.bluedb.api.exceptions.BlueDbException;
import org.bluedb.api.keys.BlueKey;

public interface BlueIndex<K extends BlueKey, T extends Serializable> {
	public List<T> get(K key) throws BlueDbException;
	public K getLastKey();
}
