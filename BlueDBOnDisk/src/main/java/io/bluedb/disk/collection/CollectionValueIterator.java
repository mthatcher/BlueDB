package io.bluedb.disk.collection;

import java.io.Serializable;
import java.util.List;
import io.bluedb.api.CloseableIterator;
import io.bluedb.api.Condition;
import io.bluedb.disk.lock.AutoCloseCountdown;
import io.bluedb.disk.segment.Range;
import io.bluedb.disk.segment.SegmentManager;

public class CollectionValueIterator<T extends Serializable> implements CloseableIterator<T> {

	private final static long TIMEOUT_DEFAULT_MILLIS = 15_000;
	private CollectionEntityIterator<T> entityIterator;
	private AutoCloseCountdown timeoutCloser;

	public CollectionValueIterator(SegmentManager<T> segmentManager, Range range, boolean byStartTime, List<Condition<T>> objectConditions) {
		entityIterator = new CollectionEntityIterator<T>(segmentManager, range, byStartTime, objectConditions);
		timeoutCloser = new AutoCloseCountdown(this, TIMEOUT_DEFAULT_MILLIS);
	}

	public CollectionValueIterator(SegmentManager<T> segmentManager, Range range, long timeout, boolean byStartTime, List<Condition<T>> objectConditions) {
		entityIterator = new CollectionEntityIterator<T>(segmentManager, range, byStartTime, objectConditions);
		timeoutCloser = new AutoCloseCountdown(this, timeout);
	}

	@Override
	public void close() {
		if (entityIterator != null) {
			entityIterator.close();
			entityIterator = null;
		}
		timeoutCloser.cancel();
	}

	@Override
	public boolean hasNext() {
		if (entityIterator == null) {
			throw new RuntimeException("CollectionValueIterator has already been closed");
		}
		timeoutCloser.snooze();
		return entityIterator.hasNext();
	}

	@Override
	public T next() {
		if (entityIterator == null) {
			throw new RuntimeException("CollectionValueIterator has already been closed");
		}
		timeoutCloser.snooze();
		return entityIterator.next().getValue();
	}
}