package io.bluedb.disk.query;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import io.bluedb.api.BlueQuery;
import io.bluedb.api.CloseableIterator;
import io.bluedb.api.Condition;
import io.bluedb.api.Updater;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.disk.collection.BlueCollectionOnDisk;
import io.bluedb.disk.collection.CollectionValueIterator;
import io.bluedb.disk.collection.task.DeleteMultipleTask;
import io.bluedb.disk.collection.task.UpdateMultipleTask;

public class BlueQueryImpl<T extends Serializable> implements BlueQuery<T> {

	private BlueCollectionOnDisk<T> collection;
	private List<Condition<T>> objectConditions = new LinkedList<>();
	private long maxTime = Long.MAX_VALUE;
	private long minTime = Long.MIN_VALUE;

	public BlueQueryImpl(BlueCollectionOnDisk<T> collection) {
		this.collection = collection;
	}

	@Override
	public BlueQuery<T> where(Condition<T> c) {
		if (c != null) {
			objectConditions.add(c);
		}
		return this;
	}

	@Override
	public BlueQuery<T> afterTime(long time) {
		minTime = Math.max(minTime, Math.max(time + 1,time)); // last part to avoid overflow errors
		return this;
	}

	@Override
	public BlueQuery<T> afterOrAtTime(long time) {
		minTime = Math.max(minTime, time);
		return this;
	}

	@Override
	public BlueQuery<T> beforeTime(long time) {
		maxTime = Math.min(maxTime, Math.min(time - 1,time)); // last part to avoid overflow errors
		return this;
	}

	@Override
	public BlueQuery<T> beforeOrAtTime(long time) {
		maxTime = Math.min(maxTime, time);
		return this;
	}

	@Override
	public List<T> getList() throws BlueDbException {
		return collection.findMatches(minTime, maxTime, objectConditions)
				.stream()
				.map((e) -> e.getValue())
				.collect(Collectors.toList());
	}

	@Override
	public CloseableIterator<T> getIterator() throws BlueDbException {
		return new CollectionValueIterator<T>(collection, minTime, maxTime);
	}

	@Override
	public void delete() throws BlueDbException {
		Runnable deleteAllTask = new DeleteMultipleTask<T>(collection, minTime, maxTime, objectConditions);
		collection.executeTask(deleteAllTask);
	}

	@Override
	public void update(Updater<T> updater) throws BlueDbException {
		Runnable updateMultipleTask = new UpdateMultipleTask<T>(collection, minTime, maxTime, objectConditions, updater);
		collection.executeTask(updateMultipleTask);
	}

	@Override
	public int count() throws BlueDbException {
		return getList().size();
	}
}
