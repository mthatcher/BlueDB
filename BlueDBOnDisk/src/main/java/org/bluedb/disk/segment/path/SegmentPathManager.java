package org.bluedb.disk.segment.path;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bluedb.api.keys.BlueKey;
import org.bluedb.disk.Blutils;
import org.bluedb.disk.file.FileUtils;
import org.bluedb.disk.segment.Range;


public interface SegmentPathManager {

	public Path getSegmentPath(BlueKey key);

	public Path getCollectionPath();

	public long getSegmentSize();

	public List<Long> getFolderSizes();

	public List<Long> getRollupLevels();

	public default Path getSegmentPath(long groupingNumber) {
		Function<Long, String> calculateFolderName = (size) -> String.valueOf(groupingNumber / size);
		String[] folderNames= getFolderSizes().stream().map(calculateFolderName).toArray(String[]::new);
		return Paths.get(getCollectionPath().toString(), folderNames);
	}

	public default List<Path> getAllPossibleSegmentPaths(BlueKey key) {
		long segmentSize = getSegmentSize();
		List<Path> paths = new ArrayList<>();
		long groupingNumber = key.getGroupingNumber();
		long minTime = Blutils.roundDownToMultiple(groupingNumber, segmentSize);
		long i = minTime;
		while (key.isInRange(i, i + segmentSize - 1)) {
			Path path = getSegmentPath(i);
			paths.add(path);
			i += segmentSize;
		}
		return paths;
	}

	public default List<File> getExistingSegmentFiles(Range range) {
		return getExistingSegmentFiles(range.getStart(), range.getEnd());
	}

	public default List<File> getExistingSegmentFiles(long minValue, long maxValue) {
		File collectionFolder = getCollectionPath().toFile();
		List<File> foldersAtCurrentLevel = Arrays.asList(collectionFolder);
		for (Long folderSizeThisCurrentLevel: getFolderSizes()) {
			foldersAtCurrentLevel = SegmentPathManager.getSubfoldersInRange(foldersAtCurrentLevel, minValue/folderSizeThisCurrentLevel, maxValue/folderSizeThisCurrentLevel);
		}
		return foldersAtCurrentLevel;
	}

	public static List<File> getSubfoldersInRange(File folder, long minValue, long maxValue) {
		return FileUtils.getFolderContents(folder)
            .stream()
			.filter((f) -> f.isDirectory())
			.filter((f) -> folderNameIsLongInRange(f, minValue, maxValue))
			.collect(Collectors.toList());
	}

	public static List<File> getSubfoldersInRange(List<File> folders, long minValue, long maxValue) {
		List<File> results = new ArrayList<>();
		for (File folder: folders) {
			results.addAll(getSubfoldersInRange(folder, minValue, maxValue));
		}
		return results;
	}
	
	public static boolean folderNameIsLongInRange(File file, long minValue, long maxValue) {
		try {
			long fileNameAsLong = Long.valueOf(file.getName());
			return fileNameAsLong >= minValue && fileNameAsLong <= maxValue;
		} catch(Exception e) {
			return false;
		}
	}
}
