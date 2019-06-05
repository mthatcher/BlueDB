package io.bluedb.disk.collection.index;

import java.util.Arrays;
import java.util.List;
import io.bluedb.api.index.KeyExtractor;
import io.bluedb.api.keys.IntegerKey;
import io.bluedb.disk.TestValue;

public class TestMultiRetrievalKeyExtractor implements KeyExtractor<IntegerKey, TestValue> {

	private static final long serialVersionUID = 1L;

	@Override
	public List<IntegerKey> extractKeys(TestValue object) {
		IntegerKey key1 = new IntegerKey(object.getCupcakes());
		IntegerKey key2 = new IntegerKey(object.getCupcakes() + 2);
		return Arrays.asList(key1, key2);
	}

	@Override
	public Class<IntegerKey> getType() {
		return IntegerKey.class;
	}
}