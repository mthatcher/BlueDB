package io.bluedb.disk.collection.task;

import java.io.Serializable;
import java.util.List;

import io.bluedb.api.Condition;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.collection.BlueCollectionImpl;
import io.bluedb.disk.recovery.PendingChange;
import io.bluedb.disk.recovery.RecoveryManager;
import io.bluedb.disk.segment.BlueEntity;

public class DeleteMultipleTask<T extends Serializable> implements Runnable {
	private final BlueCollectionImpl<T> collection;
	private final long minGroupingValue;
	private final long maxGroupingValue;
	private final List<Condition<T>> conditions;
	
	public DeleteMultipleTask(BlueCollectionImpl<T> collection, long min, long max, List<Condition<T>> conditions) {
		this.collection = collection;
		this.minGroupingValue = min;
		this.maxGroupingValue = max;
		this.conditions = conditions;
	}

	@Override
	public void run() {
		try {
			RecoveryManager<T> recoveryManager = collection.getRecoveryManager();
			List<BlueEntity<T>> entities = collection.findMatches(minGroupingValue, maxGroupingValue, conditions);
			for (BlueEntity<T> entity: entities) {
				BlueKey key = entity.getKey();
				PendingChange<T> change = PendingChange.createDelete(key);
				recoveryManager.saveChange(change);
				collection.applyChange(change);
				recoveryManager.removeChange(change);
			}
		} catch (BlueDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "<DeleteMultipleTask [" + minGroupingValue + ", " + maxGroupingValue + "] with " + conditions.size() + " conditions>";
	}
}
