package io.bluedb.disk.collection;

import java.io.Serializable;

import io.bluedb.api.CloseableIterator;
import io.bluedb.disk.lock.AutoCloseCountdown;
import io.bluedb.disk.segment.Range;
import io.bluedb.disk.segment.SegmentManager;

public class CollectionValueIterator<T extends Serializable> implements CloseableIterator<T> {

	private final static long TIMEOUT_DEFAULT_MILLIS = 15_000;
	private CollectionEntityIterator<T> entityIterator;
	private AutoCloseCountdown timeoutCloser;
	
	public CollectionValueIterator(SegmentManager<T> segmentManager, Range range, boolean byStartTime) {
		entityIterator = new CollectionEntityIterator<T>(segmentManager, range, byStartTime);
		timeoutCloser = new AutoCloseCountdown(this, TIMEOUT_DEFAULT_MILLIS);
	}

	public CollectionValueIterator(SegmentManager<T> segmentManager, Range range, long timeout, boolean byStartTime) {
		entityIterator = new CollectionEntityIterator<T>(segmentManager, range, byStartTime);
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
		timeoutCloser.snooze();
		return entityIterator.hasNext();
	}

	@Override
	public T next() {
		timeoutCloser.snooze();
		return entityIterator.next().getValue();
	}
}
