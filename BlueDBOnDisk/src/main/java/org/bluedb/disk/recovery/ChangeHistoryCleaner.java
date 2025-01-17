package org.bluedb.disk.recovery;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bluedb.disk.Blutils;

public class ChangeHistoryCleaner implements Runnable {

	private static int DEFAULT_RETENTION_LIMIT = 200;
	private int completedChangeLimit = DEFAULT_RETENTION_LIMIT;
	private final AtomicInteger holdsOnHistoryCleanup = new AtomicInteger(0);
	private long waitBetweenCleanups = 500;
	private boolean isStopped = false;
	final Path historyFolderPath;
	RecoveryManager<?> recoveryManager;
	Thread thread;

	public ChangeHistoryCleaner(RecoveryManager<?> recoveryManager) {
		this.recoveryManager = recoveryManager;
		this.historyFolderPath = recoveryManager.getHistoryFolder();
		thread = new Thread(this);
		thread.start();
	}

	public void setRetentionLimit(int completedChangeLimit) {
		this.completedChangeLimit = completedChangeLimit;
	}

	public void placeHoldOnHistoryCleanup() {
		holdsOnHistoryCleanup.incrementAndGet();
	}

	public void removeHoldOnHistoryCleanup() {
		holdsOnHistoryCleanup.decrementAndGet();
	}

	public List<TimeStampedFile> getCompletedTimeStampedFiles() {
		List<File> historicChangeFiles = recoveryManager.getCompletedChangeFiles();
		List<TimeStampedFile> timestampedFiles = Blutils.mapIgnoringExceptions(historicChangeFiles, (f) -> new TimeStampedFile(f) );
		return timestampedFiles;
	}

	public void cleanupHistory() {
		if (holdsOnHistoryCleanup.get() > 0) {
			return;
		}
		List<TimeStampedFile> timestampedFiles = getCompletedTimeStampedFiles();
		Collections.sort(timestampedFiles);
		int numFilesToDelete = Math.max(0, timestampedFiles.size() - completedChangeLimit);
		List<TimeStampedFile> filesToDelete = timestampedFiles.subList(0, numFilesToDelete);
		filesToDelete.forEach((f) -> f.getFile().delete());
	}

	public void setWaitBetweenCleanups(long millis) {
		this.waitBetweenCleanups = millis;
	}

	public void stop() {
		isStopped = true;
	}

	@Override
	public void run() {
		while(!isStopped) {
			cleanupHistory();
			Blutils.trySleep(waitBetweenCleanups);
		}
		
	}
}
