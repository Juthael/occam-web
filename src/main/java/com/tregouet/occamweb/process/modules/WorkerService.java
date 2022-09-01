package com.tregouet.occamweb.process.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.tregouet.occamweb.process.modules.comparator.ComparatorWorker;
import com.tregouet.occamweb.process.modules.comparator.IComparatorWorker;
import com.tregouet.occamweb.process.modules.sorter.ISorterWorker;
import com.tregouet.occamweb.process.modules.sorter.SorterWorker;

@Service
public class WorkerService {

	private Map<String, ISorterWorker> sorterWorkers = new ConcurrentHashMap<>();
	private Map<String, IComparatorWorker> comparatorWorkers = new ConcurrentHashMap<>();

	public WorkerService() {
		try {
			FileSystemUtils.deleteRecursively(Paths.get("workers"));
		} catch (IOException e) {

		}
	}

	public ISorterWorker getOrCreateSorterWorker(String sessionSorterId) {
		return sorterWorkers.computeIfAbsent(sessionSorterId, sid -> new SorterWorker(getWorkerDirectory(sid)));
	}

	public IComparatorWorker getOrCreateComparatorWorker(String sessionSorterId) {
		return comparatorWorkers.computeIfAbsent(sessionSorterId, sid -> new ComparatorWorker(getWorkerDirectory(sid)));
	}

	private Path getWorkerDirectory(String sid) {
		return Paths.get("workers").resolve(sid);
	}

}
