package io.bluedb.disk.collection.task;

import java.io.Serializable;
import java.util.List;
import io.bluedb.api.Updater;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.collection.BlueCollectionImpl;
import io.bluedb.disk.recovery.PendingChange;
import io.bluedb.disk.recovery.RecoveryManager;
import io.bluedb.disk.segment.Segment;

public class UpdateTask<T extends Serializable> implements Runnable {
	private final BlueCollectionImpl<T> collection;
	private final BlueKey key;
	private final Updater<T> updater;
	
	public UpdateTask(BlueCollectionImpl<T> collection, BlueKey key, Updater<T> updater) {
		this.collection = collection;
		this.key = key;
		this.updater = updater;
	}

	@Override
	public void run() {
		try {
			T value = collection.get(key);
			RecoveryManager<T> recoveryManager = collection.getRecoveryManager();
			PendingChange<T> change = recoveryManager.saveUpdate(key, value, updater);
			List<Segment<T>> segments = collection.getSegmentManager().getAllSegments(key);
			for (Segment<T> segment: segments) {
				change.applyChange(segment);
			}
			collection.getRecoveryManager().removeChange(change);
		} catch (BlueDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "<UpdateTask for key " + key + ">";
	}
}
