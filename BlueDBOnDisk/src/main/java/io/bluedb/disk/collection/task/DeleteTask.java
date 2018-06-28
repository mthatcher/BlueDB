package io.bluedb.disk.collection.task;

import java.io.Serializable;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.collection.BlueCollectionImpl;
import io.bluedb.disk.recovery.PendingChange;
import io.bluedb.disk.recovery.RecoveryManager;

public class DeleteTask<T extends Serializable> implements Runnable {
	private final BlueCollectionImpl<T> collection;
	private final BlueKey key;
	
	public DeleteTask(BlueCollectionImpl<T> collection, BlueKey key) {
		this.collection = collection;
		this.key = key;
	}

	@Override
	public void run() {
		try {
			RecoveryManager<T> recoveryManager = collection.getRecoveryManager();
			PendingChange<T> change = PendingChange.createDelete(key);
			recoveryManager.saveDelete(key);
			collection.applyChange(change);
			recoveryManager.removeChange(change);
		} catch (Throwable t) {
			// TODO rollback or try again?
		} finally {
		}

	}

	@Override
	public String toString() {
		return "<DeleteTask for key " + key + ">";
	}
}
