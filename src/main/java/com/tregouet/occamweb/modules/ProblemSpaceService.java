package com.tregouet.occamweb.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class ProblemSpaceService {

	private Map<String, SorterWorker> sessionWorkers = new ConcurrentHashMap<>();

	public ProblemSpaceService() {
		try {
			FileSystemUtils.deleteRecursively(Paths.get("workers"));
		} catch (IOException e) {

		}
	}

	public ISorterWorker getOrCreateWorker(String sessionSorterId) {
		return sessionWorkers.computeIfAbsent(sessionSorterId, sid -> new SorterWorker(getWorkerDirectory(sid)));
	}

	private Path getWorkerDirectory(String sid) {
		return Paths.get("workers").resolve(sid);
	}

}
