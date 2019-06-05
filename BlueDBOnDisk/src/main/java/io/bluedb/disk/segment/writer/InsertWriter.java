package io.bluedb.disk.segment.writer;

import java.io.Serializable;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.file.BlueObjectInput;
import io.bluedb.disk.file.BlueObjectOutput;
import io.bluedb.disk.serialization.BlueEntity;

public class InsertWriter<T extends Serializable> implements StreamingWriter<T> {

	final BlueKey newKey;
	final T newValue;

	public InsertWriter(BlueKey key, T value) {
		newKey = key;
		newValue = value;
	}

	public void process(BlueObjectInput<BlueEntity<T>> input, BlueObjectOutput<BlueEntity<T>> output) throws BlueDbException {
		BlueEntity<T> newEntity = new BlueEntity<T>(newKey, newValue);
		BlueEntity<T> toInsert = newEntity;
		while (input.hasNext()) {
			BlueEntity<T> iterEntity = input.next();
			BlueKey iterKey = iterEntity.getKey();
			if (iterKey.equals(newKey)) {
				output.write(newEntity);
				toInsert = null;
			} else if (toInsert != null && iterKey.compareTo(newKey) > 0) {
				output.write(newEntity);
				toInsert = null;
				output.writeBytes(input.getLastBytes());
			} else {
				output.writeBytes(input.getLastBytes());
			}
		}
		if (toInsert != null) {
			output.write(newEntity);
		}
	}
}