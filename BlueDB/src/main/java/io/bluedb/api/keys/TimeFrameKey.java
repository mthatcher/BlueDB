package io.bluedb.api.keys;

public class TimeFrameKey extends TimeKey {
	private static final long serialVersionUID = 1L;

	private long endTime;

	public TimeFrameKey(long id, long startTime, long endTime) {
		super(id, startTime);
	}

	public long getStartTime() {
		return super.getTime();
	}

	public long getEndTime() {
		return endTime;
	}
}