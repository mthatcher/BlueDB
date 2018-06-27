package io.bluedb.disk.recovery;

import java.io.Serializable;

import io.bluedb.api.Updater;
import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.segment.Segment;
import io.bluedb.disk.serialization.BlueSerializer;

public class PendingChange<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private BlueKey key;
	private T oldValue;
	private T newValue;
	private long timeCreated;
	
	private PendingChange(BlueKey key, T oldValue, T newValue) {
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
		timeCreated = System.currentTimeMillis();
	}
	
	public static <X extends Serializable> PendingChange<X> createDelete(BlueKey key){
		return new PendingChange<X>(key, null, null);
	}

	public static <T extends Serializable> PendingChange<T> createInsert(BlueKey key, T newValue){
		return new PendingChange<T>(key, null, newValue);
	}

	public static <T extends Serializable> PendingChange<T> createUpdate(BlueKey key, T oldValue, Updater<T> updater, BlueSerializer serializer){
		T newValue = serializer.clone(oldValue);
		updater.update(newValue);
		return new PendingChange<T>(key, oldValue, newValue);
	}

	public void applyChange(Segment<T> segment) throws BlueDbException {
		if (isInsert()) {
			// TODO check for already existing?  probably not, probably do that in the calling function
			segment.put(key, newValue);
		} else if (isDelete()) {
			segment.delete(key);
		} else if (isUpdate()) {
			segment.put(key, newValue);
		} else {
			throw new BlueDbException("maleformed PendingChange: " + this.toString());
		}
	}

	public BlueKey getKey() {
		return key;
	}
	public T getOldValue() {
		return oldValue;
	}
	public T getNewValue() {
		return newValue;
	}

	public long getTimeCreated() {
		return timeCreated;
	}

	public boolean isDelete() {
		return newValue == null;
	}

	public boolean isInsert() {
		return newValue != null && oldValue == null;
	}

	public boolean isUpdate() {
		return newValue != null && oldValue != null;
	}

	@Override
	public String toString() {
		return "<" + key +": " + String.valueOf(oldValue) + "=> " + String.valueOf(newValue) + ">";
	}
}