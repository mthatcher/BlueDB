package io.bluedb.disk;

import java.io.Serializable;
import io.bluedb.api.BlueDb;
import io.bluedb.api.BlueCollection;

public class BlueDbOnDisk implements BlueDb {

	@Override
	public <T extends Serializable> BlueCollection<T> getCollection(Class<T> type) {
		return new BlueCollectionImpl<>(type);
	}

}