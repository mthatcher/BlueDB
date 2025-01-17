package org.bluedb.disk.segment.path;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bluedb.api.keys.BlueKey;

public class IntegerSegmentPathManager implements SegmentPathManager {

	private static final long SIZE_SEGMENT = 256;
	private static final long SIZE_FOLDER_BOTTOM = SIZE_SEGMENT * 64;
	private static final long SIZE_FOLDER_MIDDLE = SIZE_FOLDER_BOTTOM * 64;
	private static final long SIZE_FOLDER_TOP = SIZE_FOLDER_MIDDLE * 64;
	public final static List<Long> ROLLUP_LEVELS = Collections.unmodifiableList(Arrays.asList(1L, SIZE_SEGMENT));

	private final Path collectionPath;
	private final List<Long> folderSizes = Collections.unmodifiableList(Arrays.asList(SIZE_FOLDER_TOP, SIZE_FOLDER_MIDDLE, SIZE_FOLDER_BOTTOM, SIZE_SEGMENT));

	public IntegerSegmentPathManager(Path collectionPath) {
		this.collectionPath = collectionPath;
	}

	@Override
	public Path getSegmentPath(BlueKey key) {
		long groupingNumber = key.getGroupingNumber();
		return getSegmentPath(groupingNumber);
	}

	@Override
	public long getSegmentSize() {
		return SIZE_SEGMENT;
	}

	@Override
	public List<Long> getRollupLevels() {
		return ROLLUP_LEVELS;
	}

	@Override
	public List<Long> getFolderSizes() {
		return folderSizes;
	}

	@Override
	public Path getCollectionPath() {
		return collectionPath;
	}
}
