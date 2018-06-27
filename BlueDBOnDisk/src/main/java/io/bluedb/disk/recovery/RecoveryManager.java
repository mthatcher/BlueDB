package io.bluedb.disk.recovery;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.bluedb.api.exceptions.BlueDbException;
import io.bluedb.api.keys.BlueKey;
import io.bluedb.disk.Blutils;
import io.bluedb.disk.collection.BlueCollectionImpl;
import io.bluedb.disk.segment.Segment;

public class RecoveryManager<T extends Serializable> {
	
	private static String SUFFIX = ".pending";

	private final BlueCollectionImpl<T> collection;
	private final Path recoveryPath;

	public RecoveryManager(BlueCollectionImpl<T> collection) {
		this.collection = collection;
		this.recoveryPath = Paths.get(collection.getPath().toString(), ".pending");
	}

	public void saveChange(PendingChange<T> change) throws BlueDbException {
		String filename = getFileName(change);
		Path path = Paths.get(recoveryPath.toString(), filename);
		Blutils.save(path.toString(), change, collection.getSerializer());
	}

	public void removeChange(PendingChange<T> change) throws BlueDbException {
		String filename = getFileName(change);
		Path path = Paths.get(recoveryPath.toString(), filename);
		File file = new File(path.toString());
		if (!file.delete()) {
			// TODO do we want to throw an exception
			throw new BlueDbException("failed to remove pending change from recovery folder: " + change);
		}
	}

	public static String getFileName(PendingChange<?> change) {
		return  String.valueOf(change.getTimeCreated()) + SUFFIX;
	}

	public List<PendingChange<T>> getPendingChanges() {
		List<File> pendingChangeFiles = Blutils.listFiles(recoveryPath, SUFFIX);
		List<PendingChange<T>> changes = new ArrayList<>();
		for (File file: pendingChangeFiles) {
			// TODO also remember to throw out corrupted files
		}
		return changes;
	}


	public void recover() {
		List<PendingChange<T>> pendingChanges = getPendingChanges();
		for (PendingChange<T> change: pendingChanges) {
			BlueKey key = change.getKey();
			List<Segment<T>> segments = collection.getSegmentManager().getAllSegments(key);
			for (Segment<T> segment: segments) {
//				change.applyChange(segment);
			}
//			recoveryManager.removeChange(change);
		}
	}
}