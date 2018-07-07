package io.bluedb.disk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.bluedb.api.Condition;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.api.keys.TimeFrameKey;
import io.bluedb.disk.file.BlueObjectInput;
import io.bluedb.disk.file.BlueObjectOutput;

public class Blutils {
	public static <X extends Serializable> boolean meetsConditions(List<Condition<X>> conditions, X object) {
		for (Condition<X> condition: conditions) {
			if (!condition.test(object)) {
				return false;
			}
		}
		return true;
	}

	public static long roundDownToMultiple(long value, long multiple) {
		if (value >= 0) {
			return value - (value % multiple);
		} else {
			return value - (value % multiple) - multiple;
		}
	}

	public static <X extends Serializable> List<X> filter(List<X> values, Predicate<X> condition) {
		List<X> results = new ArrayList<>();
		for (X value: values) {
			if (condition.test(value)) {
				results.add(value);
			}
		}
		return results;
	}

	// TODO test
	public static boolean isInRange(BlueKey key, long min, long max) {
		if (key instanceof TimeFrameKey) {
			TimeFrameKey timeFrameKey = (TimeFrameKey) key;
			return timeFrameKey.getEndTime() >= min && timeFrameKey.getStartTime() <= max;
		} else {
			return key.getGroupingNumber() >= min && key.getGroupingNumber() <= max;
		}
	}

	// TODO test
    public static <X> void copyObjects(BlueObjectInput<X> objectInput, BlueObjectOutput<X> objectOutput) throws BlueDbException {
		while(objectInput.hasNext()) {
			X next = objectInput.next();
			objectOutput.write(next);
		}
    }
}
